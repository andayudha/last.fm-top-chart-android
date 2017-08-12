package io.anda.lastfmchartsjava;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import io.anda.lastfmchartsjava.api.RestApiManager;
import io.anda.lastfmchartsjava.api.RestService;
import io.anda.lastfmchartsjava.home.HomePresenter;
import io.anda.lastfmchartsjava.home.HomeView;
import io.anda.lastfmchartsjava.model.TrackResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Scheduler;
import rx.Single;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {


    private static final int AFTER_MILLIS = 500;

    private HomePresenter homePresenter;

    private RestApiManager restApiManager;
    private HomeView homeView;

    @Before
    public void setUp() throws Exception{
        homeView = mock(HomeView.class);
        restApiManager = mock(RestApiManager.class);

        homePresenter = new HomePresenter(restApiManager);
        homePresenter.initView(homeView);

        setupRxSchedulers();
    }

    @Test
    public void testErrorLoadTrack() throws Exception{
        String message = "Connection timeout";
         when(restApiManager.getTopChartCountryResponseSingle(anyInt(), anyInt(), anyString()) )
                .thenReturn(Single.error(new IOException(message)));
        homePresenter.loadTrackList("japan");

        verify(homeView, after(AFTER_MILLIS)).loadError(anyString());
    }

    @Test
    public void testResponseFailed() throws Exception{
        String errorCode = "401";
        Response<TrackResponse> dummyResponse = Response.error(Integer.parseInt(errorCode), mock(ResponseBody.class));
        when(restApiManager.getTopChartCountryResponseSingle(anyInt(), anyInt(), anyString()))
                .thenReturn(Single.just(dummyResponse));
        homePresenter.loadTrackList("japan");
        verify(homeView, after(AFTER_MILLIS)).loadError(errorCode);
    }

    private void setupRxSchedulers() {
        // Override RxJava schedulers
        RxJavaHooks.setOnIOScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return Schedulers.immediate();
            }
        });

        RxJavaHooks.setOnComputationScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return Schedulers.immediate();
            }
        });

        RxJavaHooks.setOnNewThreadScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return Schedulers.immediate();
            }
        });

        // Override RxAndroid schedulers
        final RxAndroidPlugins rxAndroidPlugins = RxAndroidPlugins.getInstance();
        rxAndroidPlugins.registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
    }


}