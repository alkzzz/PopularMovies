package com.example.administrator.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.administrator.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_NAME = "title";

        public static final String COLUMN_POSTER = "poster";

        public static final String COLUMN_CATEGORY = "category";

        public static final String COLUMN_SYNOPSIS = "synopsis";

        public static final String COLUMN_USER_RATING = "user_rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_IS_FAVORITE = "is_favorite";
    }

}
