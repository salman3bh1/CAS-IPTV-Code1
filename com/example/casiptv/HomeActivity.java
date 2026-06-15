package com.example.casiptv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView tvTime, tvCurrentUserName;
    private ImageButton navHome, navMovies, navSeries, navLive, btnSearch, btnFavorites;
    private LinearLayout layoutProfileInfo;

    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvTime = findViewById(R.id.tvTime);
        tvCurrentUserName = findViewById(R.id.tvCurrentUserName);
        navHome = findViewById(R.id.navHome);
        navMovies = findViewById(R.id.navMovies);
        navSeries = findViewById(R.id.navSeries);
        navLive = findViewById(R.id.navLive);
        btnSearch = findViewById(R.id.btnSearch);
        btnFavorites = findViewById(R.id.btnFavorites);
        layoutProfileInfo = findViewById(R.id.layoutProfileInfo);

        SharedPreferences pref = getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
        String currentName = getIntent().getStringExtra("ANY_NAME");
        if (currentName == null) {
            currentName = pref.getString("active_profile_name", "User");
        }
        if (tvCurrentUserName != null) {
            tvCurrentUserName.setText(currentName);
        }

        String url = pref.getString("active_profile_url", "");
        String user = pref.getString("username", "");
        String pass = pref.getString("password", "");

        startClockUpdate();

        if (navHome != null) navHome.setOnClickListener(v -> switchFragment(new HomeFragment(), navHome));
        if (navMovies != null) navMovies.setOnClickListener(v -> switchFragment(new MoviesFragment(), navMovies));
        if (navSeries != null) navSeries.setOnClickListener(v -> switchFragment(new SeriesFragment(), navSeries));
        if (navLive != null) navLive.setOnClickListener(v -> switchFragment(new LiveFragment(), navLive));
        if (btnSearch != null) btnSearch.setOnClickListener(v -> switchFragment(new SearchFragment(), null));

        if (layoutProfileInfo != null) {
            layoutProfileInfo.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        // فتح الشاشة الرئيسية فوراً وبثبات
        switchFragment(new HomeFragment(), navHome);

        // تعديل أمان جوهري: تشغيل دالة التحميل مع حماية الـ UI Thread لمنع الارتداد لشاشة البروفايلات
        if (!url.isEmpty() && !user.isEmpty() && !pass.isEmpty()) {
            try {
                MyIPTVManager.getInstance().loadAllIPTVData(getApplicationContext(), url, user, pass, new MyIPTVManager.IPTVCallback() {
                    @Override
                    public void onDataLoaded() {
                        // التأكد من أن الـ Activity لا تزال نشطة في الشاشة ولم يتم تدميرها
                        runOnUiThread(() -> {
                            try {
                                if (!isFinishing() && !isDestroyed()) {
                                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                    if (currentFragment instanceof HomeFragment) {
                                        // تحديث البيانات ديناميكياً في مكانها بدون إعادة تشغيل الواجهة
                                        ((HomeFragment) currentFragment).refreshData();
                                        ((HomeFragment) currentFragment).notifyAdapters();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void switchFragment(Fragment fragment, ImageButton activeBtn) {
        if (isFinishing() || isDestroyed()) return;

        if (navHome != null) navHome.setAlpha(0.4f);
        if (navMovies != null) navMovies.setAlpha(0.4f);
        if (navSeries != null) navSeries.setAlpha(0.4f);
        if (navLive != null) navLive.setAlpha(0.4f);

        if (activeBtn != null) {
            activeBtn.setAlpha(1.0f);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commitAllowingStateLoss(); // استخدام StateLoss لحماية حالة الـ Fragment عند التحميل المكثف بالخلفية
    }

    private void startClockUpdate() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                if (tvTime != null && !isFinishing() && !isDestroyed()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    tvTime.setText(sdf.format(new Date()));
                    timeHandler.postDelayed(this, 60000);
                }
            }
        };
        timeHandler.post(timeRunnable);
    }

    @Override
    protected void onDestroy() {
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        super.onDestroy();
    }
}