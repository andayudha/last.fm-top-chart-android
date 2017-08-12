package io.anda.lastfmchartsjava.api;

import io.anda.lastfmchartsjava.model.TrackResponse;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Created by anda on 7/15/2017.
 */

public interface RestService {

    @GET("/2.0/?method=geo.gettoptracks&format=json")
    Single<Response<TrackResponse>> getTopChartCountryResponse (
            @Query("api_key") String api_key,
            @Query("country") String country,
            @Query("page") String page,
            @Query("limit") String limit
    );
}
