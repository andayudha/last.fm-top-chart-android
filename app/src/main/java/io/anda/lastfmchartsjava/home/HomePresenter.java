package io.anda.lastfmchartsjava.home;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.anda.lastfmchartsjava.model.TrackModel;
import io.anda.lastfmchartsjava.model.TrackResponse;
import io.anda.lastfmchartsjava.api.RestApiManager;
import retrofit2.Response;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePresenter {

    private static final int LIMIT = 10;

    private RestApiManager restApiManager;
    private HomeView homeView;
    private List<TrackModel> trackModelList;
    private Subscription loadTrackSubscription, loadMoreTrackSubscription;

    public HomePresenter(RestApiManager restApiManager) {
        this.restApiManager = restApiManager;
    }

    public void initView(HomeView view){
        this.homeView = view;
    }

    public void loadTrackList(String country){
        trackModelList = new ArrayList<>();
        loadTrackSubscription = restApiManager.getTopChartCountryResponseSingle(1, LIMIT, country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Response<TrackResponse>>() {
                    @Override
                    public void onSuccess(Response<TrackResponse> trackResponseResponse) {
                        if(trackResponseResponse.isSuccessful()){
                            TrackResponse trackResponse = trackResponseResponse.body();
                            List<TrackModel> trackModels;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                trackModels = trackResponseToTrackModel(trackResponse);
                            }else{
                                trackModels = trackResponseToTrackModel2(trackResponse);
                            }
                            if(trackModels!=null && trackModels.size()>0){
                                for(TrackModel t : trackModels){
                                    if(!trackModelList.contains(t)) trackModelList.add(t);
                                }
                                homeView.showListItem(trackModelList);
                            }else{
                                homeView.loadError("List empty");
                            }
                        }else{
                            int status = trackResponseResponse.code();
                            homeView.loadError("load failed : "+status);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        homeView.loadError("Load error : "+e.getMessage());
                    }
                });

    }

    public void loadMoreTrackList(int page, String country){
        loadMoreTrackSubscription = restApiManager.getTopChartCountryResponseSingle(page, LIMIT, country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Response<TrackResponse>>() {
                    @Override
                    public void onSuccess(Response<TrackResponse> trackResponseResponse) {
                        if(trackResponseResponse.isSuccessful()){
                            TrackResponse trackResponse = trackResponseResponse.body();
                            List<TrackModel> trackModels;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                trackModels = trackResponseToTrackModel(trackResponse);
                            }else{
                                trackModels = trackResponseToTrackModel2(trackResponse);
                            }

                            if(trackModels!=null && trackModels.size()>0){
                                for(TrackModel t : trackModels){
                                    if(!trackModelList.contains(t)) trackModelList.add(t);
                                }
                                homeView.showNewItem(trackModelList);
                            }else{
                                homeView.loadError("List empty");
                            }
                        }else{
                            int status = trackResponseResponse.code();
                            homeView.loadError("load failed : "+status);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        homeView.loadError("Load error : "+e.getMessage());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<TrackModel> trackResponseToTrackModel(TrackResponse trackResponse) {
        return trackResponse.getTracks().getTrack().stream()
                .map(track -> new TrackModel(
                        track.getName(),
                        track.getArtist().getName(),
                        track.getUrl(),
                        track.getImage()[2].getText(),
                        track.getArtist().getUrl())).distinct()
                .collect(Collectors.toList());
    }

    private List<TrackModel> trackResponseToTrackModel2(TrackResponse trackResponse) {
        List<TrackModel> trackModelList = new ArrayList<>();
        for(TrackResponse.Track track : trackResponse.getTracks().getTrack()){
            TrackModel trackModel = new TrackModel(
                    track.getName(),
                    track.getArtist().getName(),
                    track.getUrl(),
                    track.getImage()[2].getText(),
                    track.getArtist().getUrl());
            trackModelList.add(trackModel);
        }
        return trackModelList;
    }

    public List<TrackModel> getTrackModelList() {
        return trackModelList;
    }

    void onStop(){
        if(loadTrackSubscription!=null && !loadTrackSubscription.isUnsubscribed()) loadTrackSubscription.unsubscribe();
        if(loadMoreTrackSubscription !=null && !loadMoreTrackSubscription.isUnsubscribed()) loadMoreTrackSubscription.unsubscribe();
    }
}
