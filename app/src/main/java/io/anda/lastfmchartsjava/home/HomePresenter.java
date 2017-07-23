package io.anda.lastfmchartsjava.home;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.anda.lastfmchartsjava.App;
import io.anda.lastfmchartsjava.model.TrackModel;
import io.anda.lastfmchartsjava.model.TrackResponse;
import io.anda.lastfmchartsjava.api.RestApiManager;
import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by anda on 7/15/2017.
 */

public class HomePresenter {

    private static final int LIMIT = 10;

    private RestApiManager restApiManager;
    private HomeView homeView;
    private List<TrackModel> trackModelList;
    private Subscription loadTrackSubscription, loadMoreTrackSubscription;

    public HomePresenter() {
        restApiManager = App.getInstance().getRestApiManager();
        trackModelList = new ArrayList<>();
    }

    public void initView(HomeView view){
        this.homeView = view;
    }

    public void loadTrackList(String country){
        trackModelList.clear();
        loadTrackSubscription = restApiManager.getTopChartCountryResponseObservable(1, LIMIT, country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<TrackResponse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        homeView.loadError("Load error : "+e.getMessage());
                    }


                    @Override
                    public void onNext(Response<TrackResponse> trackResponseResponse) {
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
                });
    }

    public void loadMoreTrackList(int page, String country){
        loadMoreTrackSubscription = restApiManager.getTopChartCountryResponseObservable(page, LIMIT, country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<TrackResponse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        homeView.loadError("Load error : "+e.getMessage());
                    }


                    @Override
                    public void onNext(Response<TrackResponse> trackResponseResponse) {
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
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<TrackModel> trackResponseToTrackModel(TrackResponse trackResponse) {
        return trackResponse.getTracks().getTrack().stream()
                .map(track -> new TrackModel(
                        track.getName(),
                        track.getArtist().getName(),
                        track.getUrl(),
                        track.getImage()[2].getText()
                )).distinct()
                .collect(Collectors.toList());
    }

    private List<TrackModel> trackResponseToTrackModel2(TrackResponse trackResponse) {
        List<TrackModel> trackModelList = new ArrayList<>();
        for(TrackResponse.Track track : trackResponse.getTracks().getTrack()){
            TrackModel trackModel = new TrackModel(
                    track.getName(),
                    track.getArtist().getName(),
                    track.getUrl(),
                    track.getImage()[2].getText());
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
