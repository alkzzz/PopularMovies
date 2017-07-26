package com.example.administrator.popularmovies.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE "+ MovieEntry.TABLE_NAME+ " (" +
                        MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY ," +
                        MovieEntry.COLUMN_NAME + " VARCHAR NOT NULL ," +
                        MovieEntry.COLUMN_POSTER + " VARCHAR NOT NULL " +
                        ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+ MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
