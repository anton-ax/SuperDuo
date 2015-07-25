package barqsoft.footballscores.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Anton on 7/24/2015.
 */
public class FootballRemoteService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new FootballViewsFactory(this.getApplicationContext(),
                intent));
    }
}
