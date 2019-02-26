package kickr.gaa.DataSorting;

import android.util.Log;

import kickr.gaa.CustomObjects.MatchObj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by cmcmanus on 11/27/2017.
 */

public class SortMatchInfo {
    //list of match objects
    private ArrayList<MatchObj> sortedMatches = null;

    public SortMatchInfo() {
    }

    public Map<Date, List<MatchObj>> sortFixturesByDate(ArrayList<MatchObj> matchList, boolean search) throws ParseException {


        Collections.sort(matchList, new Comparator<MatchObj>() {
            @Override
            public int compare(MatchObj o1, MatchObj o2) {
                try
                {
                    return new SimpleDateFormat("dd-MM-yyyy").parse(o1.getDate()).compareTo(new SimpleDateFormat("dd-MM-yyyy").parse(o2.getDate()));
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Map<Date, List<MatchObj>> map = new HashMap<>();

        int count = 0;

        for (MatchObj match : matchList)
        {
            if(count > 20 && !search)
            {
                break;
            }

            //create new list of matches and add them based on the map key
            List<MatchObj> list = map.get(new SimpleDateFormat("dd-MM-yyyy").parse(match.getDate()));
            if (list == null) {
                list = new ArrayList<>();
                map.put(new SimpleDateFormat("dd-MM-yyyy").parse(match.getDate()), list);
            }

            list.add(match);

            count++;
        }

        return new TreeMap<>(map);
    }


    public Map<String, List<MatchObj>> sortFixturesByCompetition(ArrayList<MatchObj> matchList, String date, String sortByInput) {
        //instantiate the lists
        ArrayList<MatchObj> sortList = new ArrayList<>();
        ArrayList<MatchObj> matchObjToBeSorted = new ArrayList<>();
        //create list of times in ascending order
        ArrayList<String> matchTimes = new ArrayList<>();

        //limit size of match list for better performance.
        for (int k = 0; k < (matchList.size() > 20 ? matchList.size() : 20); k++) {
            //make sure we sort by date ONLY if we are not searching by team
            if (sortByInput == null || !sortByInput.equals("sort") || !sortByInput.equals("team") || !sortByInput.equals("comp")) {
                //check to make sure we are sorting matches for todays date
                if (date.equals(matchList.get(k).getDate())) {
                    if (!matchTimes.contains(matchList.get(k).getTime())) {
                        //put all the times of todays matches in a list
                        matchTimes.add(matchTimes.listIterator().nextIndex(), matchList.get(k).getTime());
                        matchObjToBeSorted.add(matchObjToBeSorted.listIterator().nextIndex(), matchList.get(k));
                    } else {
                        matchObjToBeSorted.add(matchObjToBeSorted.listIterator().nextIndex(), matchList.get(k));
                    }
                }
            }
        }

        Map<String, List<MatchObj>> map = new HashMap<String, List<MatchObj>>();

        //we cannot sort this if we have a sortByInput
        if (null == sortByInput || sortByInput.equals("")) {
            //sort the list by time.
            Collections.sort(matchTimes, Collections.<String>reverseOrder());

            //place the match objects into a sorted object list
            for (int k = 0; k < matchTimes.size(); k++) {
                for (int l = 0; l < matchObjToBeSorted.size(); l++) {
                    if (matchTimes.get(k).equals(matchObjToBeSorted.get(l).getTime())) {
                        sortList.add(sortList.listIterator().nextIndex(), matchObjToBeSorted.get(l));
                    }
                }
            }
        } else {
            if (matchObjToBeSorted.size() == 0) {
                sortList = matchList;
            } else {
                sortList = matchObjToBeSorted;
            }
        }

        //sort by competition
        Collections.sort(sortList, new SortByComp());

        for (MatchObj match : sortList) {
            //create new list of matches and add them based on the map key
            List<MatchObj> list = map.get(match.getCompetition());
            if (list == null) {
                list = new ArrayList<MatchObj>();
                map.put(match.getCompetition(), list);
            }

            list.add(match);
        }

        //sort matches by key alphabetically
        Map<String, List<MatchObj>> sortedMap = new TreeMap<String, List<MatchObj>>(map);

        return sortedMap;
    }

    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }


    public void resetData() {
        this.sortedMatches = null;
    }

    class SortByComp implements Comparator<MatchObj> {
        // Used for sorting in ascending order of competition name
        public int compare(MatchObj a, MatchObj b) {
            return b.getCompetition().compareTo(a.getCompetition());
        }
    }

    class SortByDate implements Comparator<MatchObj>
    {
        public int compare(MatchObj a, MatchObj b) {
            try
            {
                return new SimpleDateFormat("dd/MM/yyyy").parse(b.getDate()).compareTo(new SimpleDateFormat("dd/MM/yyyy").parse(a.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
