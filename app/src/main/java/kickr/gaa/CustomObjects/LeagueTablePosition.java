package kickr.gaa.CustomObjects;

import org.json.JSONException;
import org.json.JSONObject;

public class LeagueTablePosition
{
    private String teamName = "";
    private String played;
    private String won;
    private String drawn;
    private String lost;
    private String pointsFor;
    private String pointsAgainst;
    private String pointsDiff;
    private String pointsTotal;
    private String leaguePos;

    public LeagueTablePosition(JSONObject obj) throws JSONException
    {
        this.teamName = obj.get("teamName").toString();
        this.played = obj.get("played").toString();
        this.won = obj.get("won").toString();
        this.drawn = obj.get("drawn").toString();
        this.lost = obj.get("lost").toString();
        this.pointsFor = obj.get("pointsFor").toString();
        this.pointsAgainst = obj.get("pointsAgainst").toString();
        this.pointsDiff = obj.get("pointsDiff").toString();
        this.pointsTotal = obj.get("pointsTotal").toString();
        this.leaguePos = obj.get("leaguePos").toString();
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPlayed() {
        return played;
    }

    public void setPlayed(String played) {
        this.played = played;
    }

    public String getWon() {
        return won;
    }

    public void setWon(String won) {
        this.won = won;
    }

    public String getDrawn() {
        return drawn;
    }

    public void setDrawn(String drawn) {
        this.drawn = drawn;
    }

    public String getLost() {
        return lost;
    }

    public void setLost(String lost) {
        this.lost = lost;
    }

    public String getPointsFor() {
        return pointsFor;
    }

    public void setPointsFor(String pointsFor) {
        this.pointsFor = pointsFor;
    }

    public String getPointsAgainst() {
        return pointsAgainst;
    }

    public void setPointsAgainst(String pointsAgainst) {
        this.pointsAgainst = pointsAgainst;
    }

    public String getPointsDiff() {
        return pointsDiff;
    }

    public void setPointsDiff(String pointsDiff) {
        this.pointsDiff = pointsDiff;
    }

    public String getPointsTotal() {
        return pointsTotal;
    }

    public void setPointsTotal(String pointsTotal) {
        this.pointsTotal = pointsTotal;
    }

    public String getLeaguePos() {
        return leaguePos;
    }

    public void setLeaguePos(String leaguePos) {
        this.leaguePos = leaguePos;
    }
}
