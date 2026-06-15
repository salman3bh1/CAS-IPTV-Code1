package com.example.casiptv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MoviesFragment extends Fragment {

    private RecyclerView rvMoviesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        rvMoviesList = view.findViewById(R.id.rvMoviesList);
        rvMoviesList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // عرض قائمة الأفلام المستلمة من الـ IPTV
        if (!MyIPTVManager.getInstance().getMoviesList().isEmpty()) {
            List<MovieModel> movies = MyIPTVManager.getInstance().getMoviesList();
            MoviesAdapter movieAdapter = new MoviesAdapter(getContext(), movies);
            rvMoviesList.setAdapter(movieAdapter);
        }

        return view;
    }
}