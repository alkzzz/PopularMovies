package com.example.administrator.popularmovies.model;

import java.util.List;

public class MovieReview {
    private int id;
    private List<MovieReview.ResultsBean> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MovieReview.ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<MovieReview.ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * id: "59303b4c92514166e9000f76",
         * author: "Gimly",
         * content: "I'd just like to thank Patty Jenkins for making a DCIThoughtSheWasWithUniverse movie that wasn't fucking garbage. If I'm being completely honest, the two people I went to the cinema to watch _Wonder Woman_ with and I did spend the next two hours after coming out of our screening discussing the various problems with the movie, but we also all agreed on one thing: We still loved it. Maybe it's just the rose-coloured glasses of comparison, but I had an excellent time with _Wonder Woman_, and I'm excited to go back to the cinema and watch it, at least one more time. It's the first time I've said that about a DC movie since _The Dark Knight Rises_. _Final rating:★★★½ - I strongly recommend you make the time._",
         * url: "https://www.themoviedb.org/review/59303b4c92514166e9000f76"
         */

        private String id;
        private String author;
        private String content;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
