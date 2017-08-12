package io.anda.lastfmchartsjava;

import android.app.Application;

import io.anda.lastfmchartsjava.api.RestApiManager;

/**
 * Created by anda on 7/15/2017.
 */

public class App extends Application {

    private RestApiManager restApiManager;

    @Override
    public void onCreate() {
        super.onCreate();
        restApiManager = new RestApiManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        restApiManager.onStop();
    }


    public RestApiManager getRestApiManager() {
        return restApiManager;
    }
}
