package io.anda.lastfmchartsjava.home;

import java.util.List;

import io.anda.lastfmchartsjava.model.TrackModel;

/**
 * Created by anda on 7/15/2017.
 */

public interface HomeView {
    void showListItem(List<TrackModel> contentList);

    void showNewItem(List<TrackModel> contentList);

    void loadError(String message);
}
