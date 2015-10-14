package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FetchService;

/**
 * Created by Anton on 7/24/2015.
 */
public class FootballAppWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Intent service_start = new Intent(context.getApplicationContext(), FetchService.class);
        context.getApplicationContext().startService(service_start);

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, FootballRemoteService.class);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setRemoteAdapter(appWidgetId, R.id.scores_list, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
