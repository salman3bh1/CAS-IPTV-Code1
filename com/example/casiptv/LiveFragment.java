package com.example.casiptv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LiveFragment extends Fragment {

    private RecyclerView rvLiveChannels;
    private MovieAdapter liveChannelsAdapter;
    private List<ChannelModel> liveChannelsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        rvLiveChannels = view.findViewById(R.id.rvLiveChannels);
        rvLiveChannels.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchLiveChannels();

        return view;
    }

    private void fetchLiveChannels() {
        liveChannelsList = MyIPTVManager.getInstance().getLiveChannels();

        if (liveChannelsList != null && !liveChannelsList.isEmpty()) {
            liveChannelsAdapter = new MovieAdapter(getContext(), liveChannelsList);
            rvLiveChannels.setAdapter(liveChannelsAdapter);
        } else {
            Toast.makeText(getContext(), "No Live Channels found or still loading...", Toast.LENGTH_SHORT).show();
        }
    }
}
