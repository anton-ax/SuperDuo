package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.model.Team;
import barqsoft.footballscores.provider.TeamProvider;
import retrofit.RestAdapter;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService {
    public static final String LOG_TAG = "myFetchService";
    private final FootballService service;
    private ArrayList<String> loadTeamArray = new ArrayList<>();

    public myFetchService() {
        super("myFetchService");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.football-data.org/alpha/")
                .build();
        service = restAdapter.create(FootballService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getData("n15"); //n2
        //getData("p2");
    }

    private void getData(String timeFrame) {
        try {
            JsonElement team = service.getFixtures("2015-08-01", "2015-08-20");

            processJSONdata(team.toString(), getApplicationContext(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processJSONdata(String JSONdata, Context mContext, boolean isReal) {

        final String SERIE_A = "357";
        final String SECOND_BUNDESLIGA = "394";
        final String PREMIER_LEGAUE = "354";
        final String CHAMPIONS_LEAGUE = "362";
        final String PRIMERA_DIVISION = "358";
        final String BUNDESLIGA = "351";
        final String BUNDESLIGA1 = "395"; // actually it's also bundesliga
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        //Match data
        String League;
        String mDate;
        String mTime;
        String Home;
        String Away;
        String Home_goals;
        String Away_goals;
        String match_id;
        String match_day;

        try {
            Log.e("processJSONdata", "processJSONdata");
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

            Vector<ContentValues> values = new Vector<>(matches.length());
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");

                League = League.replace(SEASON_LINK, "");
                if (League.equals(PREMIER_LEGAUE) ||
                        League.equals(SERIE_A) ||
                        League.equals(CHAMPIONS_LEAGUE) ||
                        League.equals(BUNDESLIGA) ||
                        League.equals(BUNDESLIGA1) ||
                        League.equals(PRIMERA_DIVISION) ||
                        League.equals(SECOND_BUNDESLIGA)) {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString("href");
                    match_id = match_id.replace(MATCH_LINK, "");
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id = match_id + Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0, mDate.indexOf("T"));
                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(mDate + mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0, mDate.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate = mformat.format(fragmentdate);
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG, e.getMessage());
                    }
                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    JSONObject awayTeam = match_data.getJSONObject(LINKS).getJSONObject("awayTeam");
                    String awayTeamUrl = awayTeam.getString("href");
                    final String awayTeamId = getTeamId(awayTeamUrl);
                    JSONObject homeTeam = match_data.getJSONObject(LINKS).getJSONObject("homeTeam");
                    String homeTeamUrl = homeTeam.getString("href");
                    final String homeTeamId = getTeamId(homeTeamUrl);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);
                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID, match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL, mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL, mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL, Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL, Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL, Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL, Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL, League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY, match_day);

                    match_values.put(DatabaseContract.scores_table.AWAY_ID, awayTeamId);
                    match_values.put(DatabaseContract.scores_table.HOME_ID, homeTeamId);

                    setAvatar(awayTeamId, match_values, DatabaseContract.scores_table.AWAY_AVA);
                    setAvatar(homeTeamId, match_values, DatabaseContract.scores_table.HOME_AVA);
                    values.add(match_values);
                }
            }
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insert_data);

            if (!loadTeamArray.isEmpty()) {
                for (final String key : loadTeamArray) {
                    try {
                        Team team = service.getTeamInfo(key);

                        ContentValues contentValue = new ContentValues();
                        contentValue.put("id", key);
                        contentValue.put("avatar", team.getCrestUrl());
                        contentValue.put("name", team.getName());
                        contentValue.put("shortName", team.getShortName());
                        getApplicationContext().getContentResolver()
                                .insert(TeamProvider.CONTENT_URI, contentValue);

                        ContentValues awayValues = new ContentValues();
                        awayValues.put(DatabaseContract.scores_table.AWAY_AVA, team.getCrestUrl());
                        getApplicationContext().getContentResolver()
                                .update(DatabaseContract.BASE_CONTENT_URI, awayValues,
                                        DatabaseContract.scores_table.AWAY_ID + "=?", new String[]{key});

                        ContentValues homeValues = new ContentValues();
                        homeValues.put(DatabaseContract.scores_table.HOME_AVA, team.getCrestUrl());
                        getApplicationContext().getContentResolver()
                                .update(DatabaseContract.BASE_CONTENT_URI, homeValues,
                                        DatabaseContract.scores_table.HOME_ID + "=?", new String[]{key});

                        Intent intent = new Intent("RefreshBroadcastIntent");
                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(intent);
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

    }

    private void setAvatar(final String id, ContentValues match_values, String key) {
        Cursor c = getApplication().getContentResolver().query(TeamProvider.CONTENT_URI,
                null, "id=?", new String[]{id},
                TeamProvider._ID);
        if (c.getCount() > 0) {
            c.moveToFirst();
            match_values.put(key, c.getString(2));
        } else {
            if (!loadTeamArray.contains(id)) {
                loadTeamArray.add(id);
            }
        }
        c.close();
    }

    private String getTeamId(String awayTeamUrl) {
        String[] split = awayTeamUrl.split("/");
        return split[split.length - 1];
    }
}