package kickr.gaa.AsyncTasks;

import android.os.AsyncTask;

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

import kickr.gaa.CustomObjects.LeagueTablePosition;
import kickr.gaa.CustomObjects.MatchObj;

public class LeagueInfoRetrieval extends AsyncTask<Void, Void, String> {
    public AsyncResponse delegate = null;
    private String leagueName;

    public LeagueInfoRetrieval(String leagueName) {
        this.leagueName = leagueName;
    }

    @Override
    protected String doInBackground(Void... voids) {
        URL url;
        HttpURLConnection urlConnection;
        StringBuilder sb = null;

        try {
            url = new URL("https://kickr-api.herokuapp.com/leagueInfo/" + leagueName);

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
                return "";
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        return "";
                    }
                }//end if
            }//end finally
        }//end try
        catch (MalformedURLException e) {
            return "";
        } catch (IOException e) {
            return "";
        }

        //set the response to the result string
        return sb.toString();
    }

    @Override
    protected void onPostExecute(final String success)
    {
        if(success == "" || success == null)
        {
            try
            {
                delegate.processLeagueInfo(null, null);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        JSONObject jsonObj;
        JSONArray fixturesObject;
        JSONArray leagueTable;
        try {
            jsonObj = new JSONObject(success);

            fixturesObject = jsonObj.getJSONArray("Fixtures");
            leagueTable = jsonObj.getJSONArray("LeagueTable");

            ArrayList<MatchObj> matchList = null;
            ArrayList<LeagueTablePosition> leagueInfo = null;

            //check if the data return is empty or not
            if (success.equals("")) {

            } else {
                //create and sort JSON array into usable data.
                try {
                    matchList = createMatchObjArray(fixturesObject);
                    leagueInfo = createLeagueTableObjArray(leagueTable);

                    //initialise the buttons for the menu bar
                    delegate.processLeagueInfo(leagueInfo, matchList);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<LeagueTablePosition> createLeagueTableObjArray(JSONArray leagueTable)
    {
        ArrayList<LeagueTablePosition> leagueTableArr = new ArrayList<>();

        for (int i = 0; i < leagueTable.length(); i++) {
            try {
                //get each match
                JSONObject leagueTablePos = leagueTable.getJSONObject(i);

                if (null != leagueTablePos && leagueTablePos.length() != 0)
                {
                    LeagueTablePosition leaguePosObj = new LeagueTablePosition(leagueTablePos);
                    leagueTableArr.add(leaguePosObj);
                }

            } catch (JSONException e) {

            }
        }

        return leagueTableArr;

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
                        matchObj.setLeagueInfo(true);
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
                        matchObj.setLeagueInfo(true);
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
