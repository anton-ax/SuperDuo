package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies {
    public static final int SERIE_A = 357;
    public static final int SECOND_BUNDESLIGA = 395;
    public static final int BUNDESLIGA1 = 394;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    // todo debug only
    public static final boolean DEBUG = true;

    public static String getLeague(Context context, int league_num) {
        switch (league_num) {
            case SERIE_A:
                return "Seria A";
            case PREMIER_LEGAUE:
                return "Premier League";
            case CHAMPIONS_LEAGUE:
                return "UEFA Champions League";
            case PRIMERA_DIVISION:
                return "Primera Division";
            case BUNDESLIGA:
                return "Bundesliga";
            case BUNDESLIGA1:
                return "Bundesliga";
            case SECOND_BUNDESLIGA:
                return "2. Bundesliga";
            default:
                return context.getResources().getString(R.string.uknown_league);
        }
    }

    public static String getMatchDay(Context context, int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return context.getString(R.string.group_stages_matchday_6);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.quarter_final_title);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.semi_final_title);
            } else {
                return context.getString(R.string.final_title);
            }
        } else {
            return context.getResources().getString(R.string.matchday) + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return null;
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }
}
