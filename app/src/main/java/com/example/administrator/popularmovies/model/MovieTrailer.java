package com.example.administrator.popularmovies.model;

import java.util.List;

/**
 * Created by Administrator on 7/25/2017.
 */

public class MovieTrailer {

    /**
     * id : 297762
     * results : [{"id":"590df0499251414e89010704","iso_639_1":"en","iso_3166_1":"US","key":"tsJeu11shJg","name":"Power","site":"YouTube","size":1080,"type":"Teaser"},{"id":"58ba5869925141609e01849f","iso_639_1":"en","iso_3166_1":"US","key":"5lGoQhFb4NM","name":"Official Comic-Con Trailer","site":"YouTube","size":1080,"type":"Trailer"},{"id":"590df062c3a36864c600ffd1","iso_639_1":"en","iso_3166_1":"US","key":"e9waCtSVoZ0","name":"Together","site":"YouTube","size":1080,"type":"Teaser"},{"id":"590df032c3a368650a00f87c","iso_639_1":"en","iso_3166_1":"US","key":"tnbDVsL_JpQ","name":"Goddess","site":"YouTube","size":1080,"type":"Teaser"},{"id":"58c5cb929251411d30005d71","iso_639_1":"en","iso_3166_1":"US","key":"INLzqh7rZ-U","name":"Official Origin Trailer","site":"YouTube","size":1080,"type":"Trailer"},{"id":"590df016c3a36864fc01000c","iso_639_1":"en","iso_3166_1":"US","key":"HUCBxfHjayo","name":"Bang Bang","site":"YouTube","size":1080,"type":"Teaser"},{"id":"58ba5859925141606f019801","iso_639_1":"en","iso_3166_1":"US","key":"1Q8fG0TtVAY","name":"Official Trailer","site":"YouTube","size":1080,"type":"Trailer"}]
     */

    private int id;
    private List<ResultsBean> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * id : 590df0499251414e89010704
         * iso_639_1 : en
         * iso_3166_1 : US
         * key : tsJeu11shJg
         * name : Power
         * site : YouTube
         * size : 1080
         * type : Teaser
         */

        private String id;
        private String iso_639_1;
        private String iso_3166_1;
        private String key;
        private String name;
        private String site;
        private int size;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public void setIso_3166_1(String iso_3166_1) {
            this.iso_3166_1 = iso_3166_1;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
