package kickr.gaa.AsyncTasks;

import android.os.AsyncTask;

import kickr.gaa.CustomObjects.MatchObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by cmcmanus on 11/29/2017.
 */

public class FixtureRetrieval extends AsyncTask<Void, Void, String> {
    public AsyncResponse delegate = null;
    private String countyName = "";
    private Boolean fixtures;

    public FixtureRetrieval(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    public FixtureRetrieval(String county, Boolean fixtures) {
        this.countyName = county.toLowerCase();
        this.fixtures = fixtures;
    }

    @Override
    protected String doInBackground(Void... params) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder sb = null;

        try {
            //reset string to usable variable
            if (countyName.equals("inter-county")) {
                countyName = "county";
            }

            if (fixtures) {
                url = new URL("https://kickr-api.herokuapp.com/fixtures/" + countyName);
            } else {
                url = new URL("https://kickr-api.herokuapp.com/results/" + countyName);
            }

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader br = null;
            sb = new StringBuilder();

            String line;

            try {

                br = new BufferedReader(new InputStreamReader(in));

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }//end if
            }//end finally
        }//end try
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set the response to the result string
        return sb.toString();
    }

    @Override
    protected void onPostExecute(final String success) {
        ArrayList<MatchObj> matchList = null;

        //check if the data return is empty or not
        if (success.equals("")) {

        } else {
            //create and sort JSON array into usable data.
            try {
                JSONArray arr = new JSONArray(success);

                matchList = createMatchObjArray(arr);

                //initialise the buttons for the menu bar
                delegate.processFinish(matchList);
            } catch (JSONException e) {

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled() {

    }

    private ArrayList<MatchObj> createMatchObjArray(JSONArray matchArray) {
        ArrayList<MatchObj> matchList = new ArrayList<MatchObj>();

        for (int i = 0; i < matchArray.length(); i++) {
            try {
                //get each match
                JSONObject match = matchArray.getJSONObject(i);

                if (null != match && match.length() != 0) {
                    MatchObj matchObj = null;

                    if (null != match.getString("winner") && !match.getString("winner").equals("") && !match.getString("winner").equals("N/A")) {
                        //create new result object
                        //result object does contain homeTeamScore or awayTeamScore as well as winner.
                        matchObj = new MatchObj();

                        matchObj.setId(Integer.parseInt(match.getString("id")));
                        matchObj.setHomeTeam(match.getString("homeTeam"));
                        matchObj.setHomeTeamScore(match.getString("homeTeamScore"));
                        matchObj.setAwayTeam(match.getString("awayTeam"));
                        matchObj.setAwayTeamScore(match.getString("awayTeamScore"));
                        matchObj.setTime(match.getString("time"));
                        matchObj.setDate(match.getString("date"));
                        matchObj.setVenue(match.getString("venue"));
                        matchObj.setCompetition(match.getString("competition"));
                        matchObj.setCounty(match.getString("county"));
                        matchObj.setWinner(match.getString("winner"));
                    } else {
                        //create new fixture object
                        //fixture object does not contain homeTeamScore or awayTeamScore as well as winner.
                        matchObj = new MatchObj();

                        matchObj.setId(Integer.parseInt(match.getString("id")));
                        matchObj.setHomeTeam(match.getString("homeTeam"));
                        matchObj.setHomeTeamScore("0-00");
                        matchObj.setAwayTeam(match.getString("awayTeam"));
                        matchObj.setAwayTeamScore("0-00");
                        matchObj.setTime(match.getString("time"));
                        matchObj.setDate(match.getString("date"));
                        matchObj.setVenue(match.getString("venue"));
                        matchObj.setCompetition(match.getString("competition"));
                        matchObj.setCounty(match.getString("county"));
                        matchObj.setWinner("N/A");
                        //add all the match objects to the list
                        matchList.add(i, matchObj);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return matchList;
    }
}
