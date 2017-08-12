package io.anda.lastfmchartsjava.home;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.anda.lastfmchartsjava.App;
import io.anda.lastfmchartsjava.R;
import io.anda.lastfmchartsjava.model.TrackModel;

public class HomeActivity extends AppCompatActivity implements HomeView, AdapterView.OnItemSelectedListener {

    private HomePresenter presenter;

    private List<TrackModel> trackList;
    private String[] countryArray;

    private RecyclerView mRecyclerView;
    private EndlessScrollListener scrollListener;
    private ContentAdapter adapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App application = (App) getApplication();
        presenter = new HomePresenter(application.getRestApiManager());


        mRecyclerView = findViewById(R.id.content_list);

        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager;

        layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
               int selected = spinner.getSelectedItemPosition();
                presenter.loadMoreTrackList(page+1, getCountryCode(selected));
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        presenter.initView(this);

    }

    @Override
    public void showListItem(List<TrackModel> contentList) {
        this.trackList = contentList;
        adapter = new ContentAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.smoothScrollBy(0, 100);
    }

    @Override
    public void showNewItem(List<TrackModel> contentList) {
        trackList = contentList;
        if(adapter==null) return;
        adapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollBy(0, 100);
    }

    @Override
    public void loadError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String countryCode = getCountryCode(position);
        if(trackList!=null && adapter!=null){
            trackList.clear();
            adapter.notifyDataSetChanged();
        }
        presenter.loadTrackList(countryCode);
    }

    private String getCountryCode(int position) {
        String name = getResources().getStringArray(R.array.country_list)[position];
        return name.toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TrackModel track = trackList.get(position);
            holder.setTrack(track);
            holder.title.setText(track.getTitle());
            holder.artist.setText(track.getArtist());
            Picasso.with(holder.img.getContext())
                    .load(track.getImgUrl()).fit()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.img);
        }

        @Override
        public int getItemCount() {
            return trackList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView title, artist;
            private ImageView img;
            private TrackModel selected;

            public void setTrack(TrackModel selected) {
                this.selected = selected;
            }

            public ViewHolder(View row) {
                super(row);
                title = row.findViewById(R.id.card_title);
                artist = row.findViewById(R.id.card_artist);
                img = row.findViewById(R.id.card_image);
                row.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selected.getUrl()));
                    startActivity(intent);
                });
                artist.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selected.getArtistUrl()));
                    startActivity(intent);
                });
            }
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        if(spinner==null) return false;
        countryArray = getResources().getStringArray(R.array.country_list);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                R.layout.item_spinner,
                R.id.country_text,
                countryArray);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        return true;
    }
}
