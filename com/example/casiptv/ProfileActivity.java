package com.example.casiptv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView rvProfiles;
    private ProfilesAdapter adapter;
    private List<ProfileModel> profileList;
    private Button btnAddNewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        rvProfiles = findViewById(R.id.rvProfiles);
        btnAddNewProfile = findViewById(R.id.btnAddNewProfile);

        rvProfiles.setLayoutManager(new LinearLayoutManager(this));

        if (btnAddNewProfile != null) {
            btnAddNewProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // إعادة تحميل البيانات فوراً عند العودة للشاشة لتحديث الكروت المضافة أو المحذوفة
        loadProfiles();
    }

    private void loadProfiles() {
        profileList = new ArrayList<>();
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
            String profilesJson = sharedPreferences.getString("profiles_json", "[]");
            JSONArray jsonArray = new JSONArray(profilesJson);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                // استخدام الـ Constructor المحدث المتوافق مع تعديل البرنامج
                profileList.add(new ProfileModel(
                        obj.optString("name"),
                        obj.optString("url"),
                        obj.optString("username"),
                        obj.optString("password")
                ));
            }

            adapter = new ProfilesAdapter(this, profileList);
            rvProfiles.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}