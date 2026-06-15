package com.example.casiptv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<ChannelModel> movieList;

    public MovieAdapter(Context context, List<ChannelModel> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        ChannelModel channel = movieList.get(position);
        holder.tvTitle.setText(channel.getName());

        // التأكد من جلب الصورة برمجياً بأمان مع حماية الأبعاد وضمان عدم الاختفاء
        if (channel.getIcon() != null && !channel.getIcon().isEmpty()) {
            Glide.with(context)
                    .load(channel.getIcon())
                    .placeholder(R.drawable.ic_profile) // صورة مؤقتة تظهر أثناء التحميل
                    .error(R.drawable.ic_profile)       // صورة تظهر لو الرابط مكسور
                    .centerCrop()                       // لجعل الصورة تمتد لتملأ الكرت بأناقة دون تشويه
                    .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.ic_profile);
        }

        // عند الضغط على القناة لتشغيلها وإضافتها لـ Continue Watching
        holder.itemView.setOnClickListener(v -> {
            MyIPTVManager.getInstance().addChannelToRecent(context, channel);
            // كود تشغيل الفيديو الخاص بك هنا (مثال: Intent لفتح المشغل)
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgChannelIcon);
            tvTitle = itemView.findViewById(R.id.tvChannelName);
        }
    }
}
