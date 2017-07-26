package com.example.administrator.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.administrator.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_NAME = "title";

        public static final String COLUMN_POSTER = "poster";
    }

}
