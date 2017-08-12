package io.anda.lastfmchartsjava.api;

import android.content.Context;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.anda.lastfmchartsjava.R;
import io.anda.lastfmchartsjava.model.TrackResponse;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Single;

/**
 * Created by anda on 7/15/2017.
 */

public class RestApiManager {

    private static final long TIMEOUT = 10;
    private final String BASE_URL = "http://ws.audioscrobbler.com";

    private Context mContext;
    private Retrofit retrofit;
    private RestService restService;

    public RestApiManager(Context context) {
        this.mContext = context;
        retrofit = new Retrofit.Builder()
                .client(buildOkHttpsClient())
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restService = retrofit.create(RestService.class);
    }

    public void initRestService(){
        retrofit = new Retrofit.Builder()
                .client(buildOkHttpsClient())
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restService = retrofit.create(RestService.class);
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    private static OkHttpClient buildOkHttpsClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }


    public Single<Response<TrackResponse>> getTopChartCountryResponseSingle(int page, int limit, String country) {
        return restService.getTopChartCountryResponse(
                mContext.getString(R.string.api_key),
                country,
                String.valueOf(page),
                String.valueOf(limit));

    }

    public void onStop() {

    }
}
