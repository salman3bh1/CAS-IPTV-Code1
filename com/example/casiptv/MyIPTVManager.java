package com.example.casiptv;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyIPTVManager {
    private static MyIPTVManager instance;
    private final List<ChannelModel> liveChannels = new ArrayList<>();
    private final List<MovieModel> moviesList = new ArrayList<>();
    private final List<MovieModel> seriesList = new ArrayList<>();
    private final List<ChannelModel> recentChannels = new ArrayList<>();

    private MyIPTVManager() {}

    public static synchronized MyIPTVManager getInstance() {
        if (instance == null) {
            instance = new MyIPTVManager();
        }
        return instance;
    }

    public void loadAllIPTVData(Context context, String url, String user, String pass, final IPTVCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String liveUrl = url + "/player_api.php?username=" + user + "&password=" + pass + "&action=get_live_streams";
        String moviesUrl = url + "/player_api.php?username=" + user + "&password=" + pass + "&action=get_vod_streams";
        String seriesUrl = url + "/player_api.php?username=" + user + "&password=" + pass + "&action=get_series";

        final AtomicInteger requestsCounter = new AtomicInteger(0);
        final int totalRequests = 3;

        Runnable checkCompletion = () -> {
            if (requestsCounter.incrementAndGet() == totalRequests) {
                if (callback != null) {
                    callback.onDataLoaded();
                }
            }
        };

        // 1. تحميل القنوات المباشرة (تحديد بـ 150 عنصر كحد أقصى)
        JsonArrayRequest liveRequest = new JsonArrayRequest(Request.Method.GET, liveUrl, null,
                response -> new Thread(() -> {
                    List<ChannelModel> tempLive = new ArrayList<>();
                    try {
                        int limit = Math.min(response.length(), 150);
                        for (int i = 0; i < limit; i++) {
                            JSONObject obj = response.getJSONObject(i);
                            ChannelModel ch = new ChannelModel();
                            ch.setName(obj.optString("name"));
                            ch.setStreamId(obj.optInt("stream_id"));
                            ch.setIcon(obj.optString("stream_icon"));
                            tempLive.add(ch);
                        }
                        synchronized (liveChannels) {
                            liveChannels.clear();
                            liveChannels.addAll(tempLive);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        checkCompletion.run();
                    }
                }).start(),
                error -> checkCompletion.run());

        // 2. تحميل الأفلام (تحديد بـ 150 عنصر)
        JsonArrayRequest moviesRequest = new JsonArrayRequest(Request.Method.GET, moviesUrl, null,
                response -> new Thread(() -> {
                    List<MovieModel> tempMovies = new ArrayList<>();
                    try {
                        int limit = Math.min(response.length(), 150);
                        for (int i = 0; i < limit; i++) {
                            JSONObject obj = response.getJSONObject(i);
                            MovieModel m = new MovieModel();
                            m.setName(obj.optString("name"));
                            m.setStreamId(obj.optInt("stream_id"));
                            m.setIcon(obj.optString("stream_icon"));
                            tempMovies.add(m);
                        }
                        synchronized (moviesList) {
                            moviesList.clear();
                            moviesList.addAll(tempMovies);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        checkCompletion.run();
                    }
                }).start(),
                error -> checkCompletion.run());

        // 3. تحميل المسلسلات (تحديد بـ 150 عنصر)
        JsonArrayRequest seriesRequest = new JsonArrayRequest(Request.Method.GET, seriesUrl, null,
                response -> new Thread(() -> {
                    List<MovieModel> tempSeries = new ArrayList<>();
                    try {
                        int limit = Math.min(response.length(), 150);
                        for (int i = 0; i < limit; i++) {
                            JSONObject obj = response.getJSONObject(i);
                            MovieModel s = new MovieModel();
                            s.setName(obj.optString("name"));
                            s.setStreamId(obj.optInt("series_id"));
                            s.setIcon(obj.optString("cover"));
                            tempSeries.add(s);
                        }
                        synchronized (seriesList) {
                            seriesList.clear();
                            seriesList.addAll(tempSeries);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        checkCompletion.run();
                    }
                }).start(),
                error -> checkCompletion.run());

        queue.add(liveRequest);
        queue.add(moviesRequest);
        queue.add(seriesRequest);

        loadRecentChannels(context);
    }

    // دوال جلب البيانات مع حمايتها بأخذ نسخة منفصلة تمنع تصادم الـ Threads والكراش الفوري
    public List<ChannelModel> getRecentChannels() {
        synchronized (recentChannels) {
            return new ArrayList<>(recentChannels);
        }
    }

    public List<ChannelModel> getLiveChannels() {
        synchronized (liveChannels) {
            return new ArrayList<>(liveChannels);
        }
    }

    public List<MovieModel> getMoviesList() {
        synchronized (moviesList) {
            return new ArrayList<>(moviesList);
        }
    }

    public List<MovieModel> getSeriesList() {
        synchronized (seriesList) {
            return new ArrayList<>(seriesList);
        }
    }

    // معالجة دالة beIN الحساسة بطريقة آمنة لا تسبب كراش الواجهة
    public List<ChannelModel> getBeInChannels() {
        List<ChannelModel> withIcons = new ArrayList<>();
        List<ChannelModel> withoutIcons = new ArrayList<>();
        List<ChannelModel> localCopy;

        synchronized (liveChannels) {
            localCopy = new ArrayList<>(liveChannels);
        }

        for (ChannelModel ch : localCopy) {
            if (ch != null && ch.getName() != null && ch.getName().toLowerCase().contains("bein")) {
                if (ch.getIcon() != null && !ch.getIcon().isEmpty() && ch.getIcon().startsWith("http")) {
                    withIcons.add(ch);
                } else {
                    withoutIcons.add(ch);
                }
            }
        }
        withIcons.addAll(withoutIcons);
        return withIcons;
    }

    public void addChannelToRecent(Context context, ChannelModel channel) {
        synchronized (recentChannels) {
            for (int i = 0; i < recentChannels.size(); i++) {
                if (recentChannels.get(i).getStreamId() == channel.getStreamId()) {
                    recentChannels.remove(i);
                    break;
                }
            }
            recentChannels.add(0, channel);
            if (recentChannels.size() > 15) recentChannels.remove(recentChannels.size() - 1);
        }
        saveRecentChannels(context);
    }

    private void saveRecentChannels(Context context) {
        SharedPreferences pref = context.getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        synchronized (recentChannels) {
            for (ChannelModel ch : recentChannels) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", ch.getName());
                    obj.put("id", ch.getStreamId());
                    obj.put("icon", ch.getIcon());
                    array.put(obj);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }
        pref.edit().putString("recent_channels_json", array.toString()).apply();
    }

    private void loadRecentChannels(Context context) {
        SharedPreferences pref = context.getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
        String json = pref.getString("recent_channels_json", "");
        synchronized (recentChannels) {
            recentChannels.clear();
            if (!json.isEmpty()) {
                try {
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        ChannelModel ch = new ChannelModel();
                        ch.setName(obj.optString("name"));
                        ch.setStreamId(obj.optInt("id"));
                        ch.setIcon(obj.optString("icon"));
                        recentChannels.add(ch);
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }
    }

    public interface IPTVCallback {
        void onDataLoaded();
    }
}