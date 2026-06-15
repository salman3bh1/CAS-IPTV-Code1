package com.example.casiptv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SeriesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // يمكنك استخدام نفس كود تصميم الأفلام للمسلسلات لتوفير الوقت والجهد وتوحيد الستايل
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }
}