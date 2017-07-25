package io.anda.lastfmchartsjava.model;

/**
 * Created by anda on 7/15/2017.
 */

public class TrackModel {
    private String title;
    private String artist;
    private String url;
    private String imgUrl;
    private String artistUrl;

    public TrackModel(String title, String artist, String url, String imgUrl, String artistUrl) {

        this.title = title;
        this.artist = artist;
        this.url = url;
        this.imgUrl = imgUrl;
        this.artistUrl = artistUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    @Override
    public boolean equals(Object obj) {
        return title.equals(((TrackModel)obj).getTitle());
    }
}
