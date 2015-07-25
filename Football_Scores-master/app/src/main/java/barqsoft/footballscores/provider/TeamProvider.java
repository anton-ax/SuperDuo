package barqsoft.footballscores.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import barqsoft.footballscores.database.TeamsData;

/**
 * Created by Anton on 7/22/2015.
 */
public class TeamProvider extends ContentProvider {

    static final String PROVIDER_NAME = "barqsoft.footballscores.teams";
    static final String URL = "content://" + PROVIDER_NAME + "/teams";
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final String _ID = "_id";
    private static final String TABLE_NAME = "teams";
    static final int TEAMS = 1;
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "favorites", TEAMS);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        TeamsData data = new TeamsData(getContext());
        db = data.getWritableDatabase();
        return db != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        if (uriMatcher.match(uri) == TEAMS) {
            return "vnd.android.cursor.dir/vnd.teams";
        } else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(TABLE_NAME, null, values);
        return getUriForId(id, uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rows = db.delete(TABLE_NAME, selection, selectionArgs);
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long rows = db.update(TABLE_NAME, values, selection, selectionArgs);
        return (int) rows;
    }

    // Appends the given ID to the end of the Uri path.
    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }

        throw new SQLException("Problem while inserting into uri: " + uri);
    }

}
