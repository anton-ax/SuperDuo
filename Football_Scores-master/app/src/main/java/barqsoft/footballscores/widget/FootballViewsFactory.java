package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;

public class FootballViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context ctxt = null;
    private int appWidgetId;
    private Cursor mCursor;

    public FootballViewsFactory(Context ctxt, Intent intent) {
        this.ctxt = ctxt;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor == null || mCursor.getCount() == 0) {
            return 1;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        final RemoteViews row = new RemoteViews(ctxt.getPackageName(),
                R.layout.widget_list_item);
        if (mCursor != null && mCursor.getCount() > 0) {
            if (mCursor.moveToPosition(position)) {
                row.setTextViewText(R.id.home_name, mCursor.getString(scoresAdapter.COL_HOME));
                row.setTextViewText(R.id.away_name, mCursor.getString(scoresAdapter.COL_AWAY));
            }

            row.setTextViewText(R.id.data_textview, mCursor.getString(scoresAdapter.COL_MATCHTIME));
            String scores = Utilies.getScores(mCursor.getInt(scoresAdapter.COL_HOME_GOALS),
                    mCursor.getInt(scoresAdapter.COL_AWAY_GOALS));
            if (scores != null) {
                row.setTextViewText(R.id.score_textview, scores);
                row.setViewVisibility(R.id.score_textview, View.VISIBLE);
            } else {
                row.setViewVisibility(R.id.score_textview, View.GONE);
            }

            row.setOnClickFillInIntent(R.id.home_name, new Intent());
            return row;
        } else {
            // show empty label if there are no games for today
            return new RemoteViews(ctxt.getPackageName(),
                    R.layout.empty_list_item);
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        Date date = new Date(System.currentTimeMillis());
        String fragmentDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            fragmentDate = format.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // warning! debug only:
        if (Utilies.DEBUG) {
            fragmentDate = "2015-08-16";
        }
        mCursor = ctxt.getContentResolver().query(
                DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{fragmentDate}, null);
    }
}