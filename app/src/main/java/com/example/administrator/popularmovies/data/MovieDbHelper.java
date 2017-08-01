package com.example.administrator.popularmovies.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.administrator.popularmovies.data.MovieContract.MovieEntry;
import com.example.administrator.popularmovies.model.Movie;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 3;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE "+ MovieEntry.TABLE_NAME+ " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        MovieEntry.COLUMN_NAME + " VARCHAR NOT NULL," +
                        MovieEntry.COLUMN_POSTER + " VARCHAR NULLABLE," +
                        MovieEntry.COLUMN_CATEGORY + " VARCHAR NOT NULL," +
                        MovieEntry.COLUMN_SYNOPSIS + " TEXT NULLABLE, " +
                        MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL," +
                        MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                        MovieEntry.COLUMN_RUNTIME + " INTEGER NULLABLE," +
                        MovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0," +
                        " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_MOVIE_TRAILERS =
                "CREATE TABLE "+ MovieContract.TrailerEntry.TABLE_NAME+ " (" +
                        MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                        MovieContract.TrailerEntry.COLUMN_NAME + " VARCHAR NULLABLE," +
                        MovieContract.TrailerEntry.COLUMN_KEY + " VARCHAR NULLABLE," +
                        MovieContract.TrailerEntry.COLUMN_TYPE + " VARCHAR NULLABLE," +
                        " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ")" +
                        " REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ")" +
                        " UNIQUE (" + MovieContract.TrailerEntry.COLUMN_KEY + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_MOVIE_TRAILERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+ MovieEntry.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS "+ MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(db);
    }
}
