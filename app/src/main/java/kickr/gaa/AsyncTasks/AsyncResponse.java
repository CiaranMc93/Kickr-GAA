package kickr.gaa.AsyncTasks;

import kickr.gaa.CustomObjects.MatchObj;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by cmcmanus on 11/29/2017.
 */

public interface AsyncResponse {
    void processFinish(ArrayList<MatchObj> matchList) throws ParseException;

    void processDBQueries(ArrayList<MatchObj> resultData) throws ParseException;
}
