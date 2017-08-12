package io.anda.lastfmchartsjava.web;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import io.anda.lastfmchartsjava.R;

public class WebActivity extends AppCompatActivity {

    private WebView mwebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_web);
    }
}
