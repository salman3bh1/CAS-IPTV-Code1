package com.example.casiptv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // تعريف حقول الإدخال والزر فقط لمنع أي تضارب مع الـ RecyclerView في التصميم الجديد
    private EditText etProfileName, etUsername, etPassword, etServerUrl;
    private AppCompatButton btnAddProfile;
    private List<ProfileModel> profileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // 1. ربط عناصر واجهة تسجيل الدخول الجديدة الفخمة بدقة من الـ XML
            etProfileName = findViewById(R.id.etProfileName);
            etUsername = findViewById(R.id.etUsername);
            etPassword = findViewById(R.id.etPassword);
            etServerUrl = findViewById(R.id.etServerUrl);
            btnAddProfile = findViewById(R.id.btnAddProfile);

            // 2. تحميل البيانات المتواجدة مسبقاً في الجهاز لتجنب مسحها
            loadProfilesFromStorage();

            // 3. برمجة زر الحفظ والأمان لمنع الخروج المفاجئ
            if (btnAddProfile != null) {
                btnAddProfile.setOnClickListener(v -> {
                    // جلب النصوص والتأكد من عدم وجود قيم فارغة تسبب كراش
                    String name = (etProfileName != null) ? etProfileName.getText().toString().trim() : "";
                    String user = (etUsername != null) ? etUsername.getText().toString().trim() : "";
                    String pass = (etPassword != null) ? etPassword.getText().toString().trim() : "";
                    String url = (etServerUrl != null) ? etServerUrl.getText().toString().trim() : "";

                    if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || url.isEmpty()) {
                        Toast.makeText(MainActivity.this, "الرجاء تعبئة البيانات كاملة أولاً!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        // إنشاء الكائن بالـ Constructor الرباعي الجديد المتوافق مع برنامجك
                        ProfileModel newProfile = new ProfileModel(name, url, user, pass);

                        // إضافة البروفايل وحفظه في الذاكرة محلياً
                        profileList.add(newProfile);
                        saveProfilesToStorage();

                        Toast.makeText(MainActivity.this, "تم إضافة بروفايل جديد بنجاح!", Toast.LENGTH_SHORT).show();

                        // العودة فوراً لشاشة البروفايلات (Who's Watching) لرؤية الكرت الجديد مضافاً
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "خطأ في تشكيل كائن البروفايل: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "خطأ أثناء تهيئة واجهة تسجيل الدخول: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadProfilesFromStorage() {
        try {
            SharedPreferences pref = getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
            String json = pref.getString("profiles_json", "[]");
            profileList.clear();

            if (!json.isEmpty()) {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    profileList.add(new ProfileModel(
                            obj.optString("name"),
                            obj.optString("url"),
                            obj.optString("username"),
                            obj.optString("password")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfilesToStorage() {
        try {
            SharedPreferences pref = getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
            JSONArray array = new JSONArray();
            for (ProfileModel p : profileList) {
                JSONObject obj = new JSONObject();
                obj.put("name", p.getName());
                obj.put("url", p.getUrl());
                obj.put("username", p.getUsername());
                obj.put("password", p.getPassword());
                array.put(obj);
            }
            pref.edit().putString("profiles_json", array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}