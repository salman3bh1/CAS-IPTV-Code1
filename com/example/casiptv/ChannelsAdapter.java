package com.example.casiptv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder> {

    private Context context;
    private List<ChannelModel> channelList;

    public ChannelsAdapter(Context context, List<ChannelModel> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        ChannelModel channel = channelList.get(position);
        if (channel == null) return;

        String name = channel.getName();
        String logoUrl = channel.getStreamIcon();

        holder.tvTitle.setText(name);

        Glide.with(context)
                .load(logoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_profile)
                .error(android.R.drawable.ic_menu_slideshow)
                .fitCenter()
                .into(holder.imgPoster);

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Playing: " + name, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return channelList != null ? channelList.size() : 0;
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgChannelIcon);
            tvTitle = itemView.findViewById(R.id.tvChannelName);
        }
    }
}
