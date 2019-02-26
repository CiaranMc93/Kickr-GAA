package kickr.gaa.CustomObjects;

/**
 * Created by cmcmanus on 8/25/2017.
 */

public class MatchObj implements Comparable<MatchObj>{
    //member variables
    private Boolean isLeagueInfo = false;
    private String time = "";
    private String homeTeam = "";
    private String awayTeam = "";
    private String venue = "";
    private String competition = "";
    private String date = "";
    private String homeTeamScore = "";
    private String awayTeamScore = "";
    private String winner = "";
    private String county = "";
    private int id = 0;

    public MatchObj() {

    }

    public Boolean getLeagueInfo() {
        return isLeagueInfo;
    }

    public void setLeagueInfo(Boolean leagueInfo) {
        isLeagueInfo = leagueInfo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int compareTo(MatchObj o) {
        return o.getDate().compareTo(o.getDate());
    }

}
