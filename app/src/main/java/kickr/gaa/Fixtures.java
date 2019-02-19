package kickr.gaa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kickr.gaa.ArrayAdapters.CustomArrayAdapter;
import kickr.gaa.AsyncTasks.AsyncResponse;
import kickr.gaa.AsyncTasks.FixtureRetrieval;
import kickr.gaa.CustomObjects.MatchObj;
import kickr.gaa.CustomViews.CustomViews;
import kickr.gaa.DataSorting.SortMatchInfo;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Fixtures extends AppCompatActivity implements AsyncResponse {

    public static boolean search = false;
    public static boolean tabUsed = false;
    public static boolean calInUse = false;
    public static LinearLayout all_match_info_display = null;
    //progress view variable
    public View mProgressView;
    public View backgroundView;
    public boolean dbSucess = false;
    public boolean noFutureMatches = false;
    public boolean isLoading = false;
    //network variable
    FixtureRetrieval retrieveData = null;
    //class variable
    Fixtures fixtureVar;
    //display the cards in a relative layout
    RelativeLayout const_action_bar = null;
    CustomViews competition_info_card = null;
    //define variables here
    LinearLayout calendarLayout = null;
    CompactCalendarView compactCalendarView = null;
    Button dateSearch;
    Button monthTitle;
    String date_str = "";
    TabLayout tabLayout;
    TabLayout searchTab;
    //layout variables
    AutoCompleteTextView textSearch;
    //define variables needed
    private ArrayList<MatchObj> matchObjList = null;
    private String county = "";
    private String sortBy = "";
    private SortMatchInfo sortMatches = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        //instantiate class
        fixtureVar = new Fixtures();

        tabLayout = findViewById(R.id.tabLayout);
        searchTab = findViewById(R.id.textSearch);

        //reset tabUsed
        tabUsed = false;

        //define the current date
        //get current date
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        date_str = dateFormat.format(date); // eg. 22-01-2018

        //instantiate the views
        mProgressView = findViewById(R.id.info_progress);
        backgroundView = findViewById(R.id.lvExp);

        county = "laois";

        const_action_bar = findViewById(R.id.action_bar_const);

        all_match_info_display = findViewById(R.id.comp_display);

        queryAPI(true);
    }

    private void queryAPI(Boolean fixtures) {
        //show loading bar
        showProgress(true);

        retrieveData = new FixtureRetrieval(county, fixtures);
        retrieveData.execute();
        retrieveData.delegate = this;
        //calls processFinish()
    }

    @Override
    public void processFinish(ArrayList<MatchObj> matchList) throws ParseException {
        isLoading = false;
        noFutureMatches = false;

        //remove the progress circle
        showProgress(false);

        matchObjList = matchList;

        //display the data
        initMenu(matchList, tabLayout, "");
    }

    @Override
    public void processDBQueries(ArrayList<MatchObj> matches) throws ParseException {
        //remove the progress circle
        showProgress(false);

        //check to see if we retrieved the data from the database
        if (null == matches || matches.size() == 0) {
            isLoading = true;
            //query with load
            showProgress(true);
            tabUsed = true;
            //retrieve the data from the API
            queryAPI(true);
        } else {
            dbSucess = true;
            matchObjList = matches;

            //create the match data layout
            initMenu(matchObjList, tabLayout, "");

            //tab layout already loaded so do not try again
            if (noFutureMatches) {
                isLoading = true;
                //query with load
                showProgress(true);
                tabUsed = true;
                queryAPI(true);
            } else {
                isLoading = false;
                tabUsed = true;
                //query in background
                queryAPI(true);
            }
        }
    }

    public void initMenu(final ArrayList<MatchObj> matchList, final TabLayout tabLayout, final String sortBy) throws ParseException {
        calInUse = false;

        //remove any existing views
        all_match_info_display.removeAllViews();

        displayMatchesByDate(matchList);

        //init the buttons
        dateSearch = findViewById(R.id.date);

        //tab layout logic
        Drawable dateIcon = getDrawable(R.drawable.ic_date_range_white_24dp);
        Drawable searchIcon = getDrawable(R.drawable.ic_magnify_white_24dp);

        if (!tabUsed) {
            tabLayout.addTab(tabLayout.newTab().setText("Fixtures"));
            tabLayout.addTab(tabLayout.newTab().setIcon(dateIcon));
            tabLayout.addTab(tabLayout.newTab().setIcon(searchIcon));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0 && !isLoading) {
                    tabUsed = true;
                    calInUse = false;
                    search = false;
                    //reset the matches to today
                    try {
                        initMenu(matchObjList, tabLayout, "");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (tabLayout.getSelectedTabPosition() == 1 && !isLoading) {
                    tabUsed = true;
                    search = false;
                    //instantiate the sorting class
                    sortMatches = new SortMatchInfo();
                    sortMatches.resetData();
                    sortMatches.sortFixturesByCompetition(matchList, date_str, null);
                    displayCalendar(matchList);
                } else if (tabLayout.getSelectedTabPosition() == 2 && !isLoading) {
                    //remove all views and inflate auto text view
                    search = true;
                    calInUse = false;

                    all_match_info_display.removeAllViews();
                    //add the new auto complete text view
                    textSearch = competition_info_card.getAutoCompleteTextView();

                    List<String> list = new ArrayList<String>();

                    //add all teams to the list so it can be searched
                    for (int i = 0; i < matchObjList.size(); i++) {
                        //if the list is empty,
                        if (!(list.isEmpty())) {
                            //make sure we only add names to the list if they are not already there
                            if (!(list.contains(matchObjList.get(i).getHomeTeam()))) {
                                list.add(list.listIterator().nextIndex(), matchObjList.get(i).getHomeTeam());
                            } else if (!(list.contains(matchObjList.get(i).getAwayTeam()))) {
                                list.add(list.listIterator().nextIndex(), matchObjList.get(i).getAwayTeam());
                            } else if (!(list.contains(matchObjList.get(i).getCompetition()))) {
                                list.add(list.listIterator().nextIndex(), matchObjList.get(i).getCompetition());
                            }
                        } else {
                            list.add(list.listIterator().nextIndex(), matchObjList.get(i).getHomeTeam());
                        }
                    }

                    CustomArrayAdapter adapter = new CustomArrayAdapter(Fixtures.this,
                            android.R.layout.simple_dropdown_item_1line, list);

                    textSearch.setAdapter(adapter);

                    textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = (String) parent.getItemAtPosition(position);

                            ArrayList<MatchObj> filtered = filterMatchObjs(selection);
                            //clear the views
                            all_match_info_display.removeAllViews();
                            //set flag to be true
                            tabUsed = true;
                            search = false;

                            InputMethodManager inputManager = (InputMethodManager) Fixtures.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                            displayMatchInfo(filtered, date_str, "team");
                        }
                    });

                    all_match_info_display.addView(textSearch);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0 && !isLoading) {
                    tabUsed = true;
                    calInUse = false;
                    search = false;
                    //reset the matches to today
                    try {
                        initMenu(matchObjList, tabLayout, "");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (tabLayout.getSelectedTabPosition() == 1 && !isLoading) {
                    search = false;
                    //instantiate the sorting class
                    sortMatches = new SortMatchInfo();
                    sortMatches.resetData();
                    sortMatches.sortFixturesByCompetition(matchList, date_str, null);
                    displayCalendar(matchList);
                } else if (tabLayout.getSelectedTabPosition() == 2 && !isLoading) {
                    //remove all views and inflate auto text view
                    search = true;
                    tabUsed = true;
                    calInUse = false;

                    all_match_info_display.removeAllViews();
                    //add the new auto complete text view
                    textSearch = competition_info_card.getAutoCompleteTextView();

                    List<String> list = new ArrayList<String>();

                    //add all teams to the list so it can be searched
                    for (int i = 0; i < matchObjList.size(); i++) {
                        //if the list is empty,
                        if (!(list.isEmpty())) {
                            //make sure we only add names to the list if they are not already there
                            if (!(list.contains(matchObjList.get(i).getHomeTeam()))) {
                                list.add(list.listIterator().nextIndex(), matchObjList.get(i).getHomeTeam());
                            } else if (!(list.contains(matchObjList.get(i).getAwayTeam()))) {
                                list.add(list.listIterator().nextIndex(), matchObjList.get(i).getAwayTeam());
                            } else if (!(list.contains(matchObjList.get(i).getCompetition()))) {
                                list.add(list.listIterator().nextIndex(), matchObjList.get(i).getCompetition());
                            }
                        } else {
                            list.add(list.listIterator().nextIndex(), matchObjList.get(i).getHomeTeam());
                        }
                    }

                    CustomArrayAdapter adapter = new CustomArrayAdapter(Fixtures.this,
                            android.R.layout.simple_dropdown_item_1line, list);

                    textSearch.setAdapter(adapter);

                    textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = (String) parent.getItemAtPosition(position);

                            ArrayList<MatchObj> filtered = filterMatchObjs(selection);
                            //clear the views
                            all_match_info_display.removeAllViews();
                            //set flag to be true
                            tabUsed = true;
                            search = false;

                            InputMethodManager inputManager = (InputMethodManager) Fixtures.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                            displayMatchInfo(filtered, date_str, "team");

                        }
                    });

                    all_match_info_display.addView(textSearch);
                }
            }
        });
    }

    private ArrayList<MatchObj> filterMatchObjs(String selection) {
        ArrayList<MatchObj> filteredMatches = new ArrayList<MatchObj>();

        for (int i = 0; i < matchObjList.size(); i++) {
            if (matchObjList.get(i).getAwayTeam().equals(selection) || matchObjList.get(i).getHomeTeam().equals(selection) || matchObjList.get(i).getCompetition().equals(selection)) {
                filteredMatches.add(filteredMatches.listIterator().nextIndex(), matchObjList.get(i));
            }
        }

        return filteredMatches;
    }

    private void displayCalendar(final ArrayList<MatchObj> matchList) {
        //calender in use flag
        calInUse = true;
        tabUsed = true;

        //set the calendar view
        CustomViews compact = new CustomViews(Fixtures.this);
        compact.setCustomerCalendar();

        calendarLayout = compact.getCustomCal();

        //set the button
        //month button
        monthTitle = calendarLayout.findViewById(R.id.monthTitle);

        //display the calendar
        all_match_info_display.removeAllViews();
        all_match_info_display.addView(calendarLayout);

        //get the calender view to be manipulated
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        ArrayList<String> dateList = new ArrayList<String>();

        //for each match in the match list
        for (MatchObj match : matchList) {
            if (!dateList.isEmpty()) {
                //if the string array does not contain the match date, then we add it to the list for the next iteration
                if (!dateList.contains(match.getDate())) {
                    dateList.add(dateList.listIterator().nextIndex(), match.getDate());
                }
            } else {
                //add to first iteration
                dateList.add(dateList.listIterator().nextIndex(), match.getDate());
            }
        }

        Event ev1 = null;

        //format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        //set the current month
        DateFormat fmt = new SimpleDateFormat("MMMM");
        Date curr = new Date();
        monthTitle.setText(fmt.format(curr));

        //for each different date we add a marker on the calendar
        for (String date : dateList) {
            try {
                Date result = sdf.parse(date);
                long millis = result.getTime();

                ev1 = new Event(Color.BLUE, millis, "Match Date");
                compactCalendarView.addEvent(ev1);
            } catch (ParseException e) {
                System.out.println("Parse Exception");
            }
        }

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String parseDate = "";

                try {
                    //parse the date from the calendar clicked date
                    parseDate = sdf.format(dateClicked);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //instantiate the sorting class
                sortMatches = new SortMatchInfo();
                sortMatches.resetData();
                all_match_info_display.removeAllViews();
                //display the data
                displayMatchInfo(matchList, parseDate, sortBy);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                DateFormat fmt = new SimpleDateFormat("MMMM");
                String date = fmt.format(firstDayOfNewMonth);

                monthTitle.setText(date);
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        backgroundView.setVisibility(show ? View.GONE : View.VISIBLE);
        backgroundView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backgroundView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void displayMatchesByDate(ArrayList<MatchObj> matchList) throws ParseException {
        sortMatches = new SortMatchInfo();
        CardView cardView = null;
        //set the different types of matches by competition
        Map<Date, List<MatchObj>> filterList = null;

        filterList = sortMatches.sortFixturesByDate(matchList);

        //if there are no scheduled matches, we need to display this to user
        if (filterList == null || filterList.size() == 0) {
            noFutureMatches = true;
            // Initialize a new custom CardView
            competition_info_card = new CustomViews(Fixtures.this);

            competition_info_card.setMatchDataLayout("", null, sortBy);

            //create a new card view with our custom card
            cardView = competition_info_card.getCard();

            //add cardview to linearLayout
            all_match_info_display.addView(cardView);
        } else {
            if (filterList != null) {
                noFutureMatches = false;
                //for each entry in the map should have a key and matches relating to the key
                for (Map.Entry<Date, List<MatchObj>> entry : filterList.entrySet()) {
                    Date key = entry.getKey();
                    List<MatchObj> value = entry.getValue();

                    //for each object in the json array
                    // Initialize a new custom CardView
                    competition_info_card = new CustomViews(Fixtures.this);

                    competition_info_card.setMatchDataLayout(new SimpleDateFormat("dd-MM-yyyy").format(key), value, sortBy);

                    //create a new card view with our custom card
                    cardView = competition_info_card.getCard();

                    //add cardview to linearLayout
                    all_match_info_display.addView(cardView);
                }
            }
        }
    }

    //method to display the matches to the user
    private void displayMatchInfo(ArrayList<MatchObj> matchList, String date, String sortBy) {
        sortMatches = new SortMatchInfo();
        CardView cardView = null;

        //set the different types of matches by competition
        Map<String, List<MatchObj>> filterList = null;

        if (null != sortBy && !(sortBy.equals(""))) {
            if (sortBy.equals("team") || sortBy.equals("comp") || sortBy.equals("sort")) {
                filterList = sortMatches.sortFixturesByCompetition(matchList, "", sortBy);
            }
        } else {
            filterList = sortMatches.sortFixturesByCompetition(matchList, date, null);
        }

        //if there are no scheduled matches, we need to display this to user
        if (filterList == null || filterList.size() == 0) {
            noFutureMatches = true;
            // Initialize a new custom CardView
            competition_info_card = new CustomViews(Fixtures.this);

            ArrayList<MatchObj> obj = null;

            competition_info_card.setMatchDataLayout("", null, sortBy);

            //create a new card view with our custom card
            cardView = competition_info_card.getCard();

            //add cardview to linearLayout
            all_match_info_display.addView(cardView);
        } else {
            if (filterList != null) {
                noFutureMatches = false;
                //for each entry in the map should have a key and matches relating to the key
                for (Map.Entry<String, List<MatchObj>> entry : filterList.entrySet()) {
                    String key = entry.getKey();
                    List<MatchObj> value = entry.getValue();

                    //for each object in the json array
                    // Initialize a new custom CardView
                    competition_info_card = new CustomViews(Fixtures.this);

                    competition_info_card.setMatchDataLayout(key, value, sortBy);

                    //create a new card view with our custom card
                    cardView = competition_info_card.getCard();

                    //add cardview to linearLayout
                    all_match_info_display.addView(cardView);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (all_match_info_display.isDirty()) {
            //display todays matches when back button is pressed
            displayMatchInfo(matchObjList, date_str, sortBy);
        }
    }
}