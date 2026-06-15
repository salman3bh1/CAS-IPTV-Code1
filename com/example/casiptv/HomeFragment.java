package com.example.casiptv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvContinueWatching, rvBeinChannels, rvPopularMovies, rvPopularSeries;
    private TextView titleContinue;

    private ChannelsAdapter continueAdapter;
    private ChannelsAdapter beinAdapter;
    private MoviesAdapter moviesAdapter;
    private MoviesAdapter seriesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvContinueWatching = view.findViewById(R.id.rvContinueWatching);
        rvBeinChannels = view.findViewById(R.id.rvBeinChannels);
        rvPopularMovies = view.findViewById(R.id.rvPopularMovies);
        rvPopularSeries = view.findViewById(R.id.rvPopularSeries);
        titleContinue = view.findViewById(R.id.titleContinue);

        setupHorizontalLayout(rvContinueWatching);
        setupHorizontalLayout(rvBeinChannels);
        setupHorizontalLayout(rvPopularMovies);
        setupHorizontalLayout(rvPopularSeries);

        refreshData();

        return view;
    }

    public void refreshData() {
        if (getContext() == null) return;

        // 1. القنوات الأخيرة
        List<ChannelModel> recents = MyIPTVManager.getInstance().getRecentChannels();
        if (recents.isEmpty()) {
            if (titleContinue != null) titleContinue.setVisibility(View.GONE);
            if (rvContinueWatching != null) rvContinueWatching.setVisibility(View.GONE);
        } else {
            if (titleContinue != null) titleContinue.setVisibility(View.VISIBLE);
            if (rvContinueWatching != null) rvContinueWatching.setVisibility(View.VISIBLE);
            List<ChannelModel> safeRecents = (recents.size() > 5) ? new ArrayList<>(recents.subList(0, 5)) : recents;
            continueAdapter = new ChannelsAdapter(getContext(), safeRecents);
            rvContinueWatching.setAdapter(continueAdapter);
        }

        // 2. قنوات beIN
        List<ChannelModel> beinList = MyIPTVManager.getInstance().getBeInChannels();
        List<ChannelModel> safeBein = (beinList.size() > 20) ? new ArrayList<>(beinList.subList(0, 20)) : beinList;
        beinAdapter = new ChannelsAdapter(getContext(), safeBein);
        rvBeinChannels.setAdapter(beinAdapter);

        // 3. الأفلام
        List<MovieModel> moviesList = MyIPTVManager.getInstance().getMoviesList();
        List<MovieModel> safeMovies = (moviesList.size() > 20) ? new ArrayList<>(moviesList.subList(0, 20)) : moviesList;
        moviesAdapter = new MoviesAdapter(getContext(), safeMovies);
        rvPopularMovies.setAdapter(moviesAdapter);

        // 4. المسلسلات
        List<MovieModel> seriesList = MyIPTVManager.getInstance().getSeriesList();
        List<MovieModel> safeSeries = (seriesList.size() > 20) ? new ArrayList<>(seriesList.subList(0, 20)) : seriesList;
        seriesAdapter = new MoviesAdapter(getContext(), safeSeries);
        rvPopularSeries.setAdapter(seriesAdapter);
    }

    public void notifyAdapters() {
        if (continueAdapter != null) continueAdapter.notifyDataSetChanged();
        if (beinAdapter != null) beinAdapter.notifyDataSetChanged();
        if (moviesAdapter != null) moviesAdapter.notifyDataSetChanged();
        if (seriesAdapter != null) seriesAdapter.notifyDataSetChanged();
    }

    private void setupHorizontalLayout(RecyclerView rv) {
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}