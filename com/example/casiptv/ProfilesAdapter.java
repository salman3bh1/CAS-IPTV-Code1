package com.example.casiptv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder> {

    private Context context;
    private List<ProfileModel> profileList;

    public ProfilesAdapter(Context context, List<ProfileModel> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileModel profile = profileList.get(position);
        holder.tvProfileName.setText(profile.getName());
        holder.imgProfileAvatar.setImageResource(R.drawable.ic_profile);

        // عند الضغط على البروفايل للدخول
        holder.itemView.setOnClickListener(v -> {
            String checkUrl = profile.getUrl() + "/player_api.php?username=" + profile.getUsername() + "&password=" + profile.getPassword();

            JsonObjectRequest checkRequest = new JsonObjectRequest(Request.Method.GET, checkUrl, null,
                    response -> {
                        try {
                            if (response.has("user_info")) {
                                JSONObject userInfo = response.getJSONObject("user_info");
                                String auth = userInfo.optString("auth");
                                long expDateLong = userInfo.optLong("exp_date", 0);
                                long currentTime = System.currentTimeMillis() / 1000;

                                if ("0".equals(auth) || (expDateLong != 0 && expDateLong < currentTime)) {
                                    Toast.makeText(context, "هذا الحساب منتهي الصلاحية!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            saveAndLogin(profile);
                        } catch (Exception e) {
                            // في حال حدوث خطأ بسيط في القراءة، ندعه يدخل كإجراء احتياطي
                            saveAndLogin(profile);
                        }
                    },
                    error -> {
                        // السماح بالدخول حتى لو فشل فحص الصلاحية السريع بسبب ضعف الشبكة
                        saveAndLogin(profile);
                    });

            Volley.newRequestQueue(context.getApplicationContext()).add(checkRequest);
        });

        // زر الحذف الصريح
        holder.btnDeleteProfile.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                profileList.remove(currentPos);
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, profileList.size());
                saveProfilesToStorage();
                Toast.makeText(context, "تم حذف البروفايل", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAndLogin(ProfileModel profile) {
        SharedPreferences pref = context.getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("active_profile_name", profile.getName());
        editor.putString("active_profile_url", profile.getUrl());
        editor.putString("username", profile.getUsername());
        editor.putString("password", profile.getPassword());
        editor.apply();

        // تمرير اسم بسيط جداً لا يسبب ثقل على الذاكرة
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("ANY_NAME", profile.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void saveProfilesToStorage() {
        SharedPreferences pref = context.getSharedPreferences("CAS_PREFS", Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (ProfileModel p : profileList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("name", p.getName());
                obj.put("url", p.getUrl());
                obj.put("username", p.getUsername());
                obj.put("password", p.getPassword());
                array.put(obj);
            } catch (Exception e) { e.printStackTrace(); }
        }
        pref.edit().putString("profiles_json", array.toString()).apply();
    }

    @Override
    public int getItemCount() {
        return profileList != null ? profileList.size() : 0;
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView tvProfileName;
        ImageView imgProfileAvatar;
        ImageButton btnDeleteProfile;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
            imgProfileAvatar = itemView.findViewById(R.id.imgProfileAvatar);
            btnDeleteProfile = itemView.findViewById(R.id.btnDeleteProfile);
        }
    }
}