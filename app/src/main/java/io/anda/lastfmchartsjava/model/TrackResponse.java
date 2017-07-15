package io.anda.lastfmchartsjava.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by anda on 7/15/2017.
 */

public class TrackResponse implements Serializable{

    @SerializedName("tracks")
    private Tracks tracks;

    public Tracks getTracks() {
        return tracks;
    }

    public class Tracks implements Serializable{
        @SerializedName("track")
        private List<Track> track;

        public List<Track> getTrack() {
            return track;
        }
    }

    public class Track implements Serializable{
        @SerializedName("name")
        private String name;
        @SerializedName("artist")
        private Artist artist;
        @SerializedName("image")
        private Image[] image;
        @SerializedName("url")
        private String url;

        public String getName() {
            return name;
        }

        public Artist getArtist() {
            return artist;
        }

        public Image[] getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }
    }

    public class Artist implements Serializable{
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    public class Image implements Serializable{
        @SerializedName("#text")
        private String text;
        @SerializedName("size")
        private String size;

        public String getText() {
            return text;
        }

        public String getSize() {
            return size;
        }
    }

}
