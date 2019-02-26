package kickr.gaa.AsyncTasks;

import org.json.JSONObject;

import kickr.gaa.CustomObjects.LeagueTablePosition;
import kickr.gaa.CustomObjects.MatchObj;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by cmcmanus on 11/29/2017.
 */

public interface AsyncResponse {
    void processFixtures(ArrayList<MatchObj> matchList) throws ParseException;

    void processLeagueInfo(ArrayList<LeagueTablePosition> leagueTable, ArrayList<MatchObj> matchList) throws ParseException;

    void processDBQueries(ArrayList<MatchObj> resultData) throws ParseException;
}
