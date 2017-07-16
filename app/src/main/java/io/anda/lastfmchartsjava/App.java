package io.anda.lastfmchartsjava;

import android.app.Application;

import io.anda.lastfmchartsjava.api.RestApiManager;

/**
 * Created by anda on 7/15/2017.
 */

public class App extends Application {

    private static App instance;
    private RestApiManager restApiManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        restApiManager = RestApiManager.createInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        restApiManager.onStop();
    }

    public static App getInstance() {
        return instance;
    }

    public RestApiManager getRestApiManager() {
        return restApiManager;
    }
}
