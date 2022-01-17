package com.gap.bis_inspection.adapter.advert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdvertButtonStatusAdapter extends RecyclerView.Adapter<AdvertButtonStatusAdapter.CustomViewHolder> {

    private final List<JSONObject> jsonObjectList;
    private String permissionName = null;
    private String appPermission = null;
    private AppController application;


    public AdvertButtonStatusAdapter(List<JSONObject> jsonObjectList, AppController application) {
        this.jsonObjectList = jsonObjectList;
        this.application = application;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_button_items, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        JSONObject json = jsonObjectList.get(position);

        try {
            if (!json.isNull("processBisSettingVO")) {
                JSONObject processBisSettingVO = json.getJSONObject("processBisSettingVO");
                if (!processBisSettingVO.isNull("name")) {
                    String name = processBisSettingVO.getString("name");
                    holder.button.setText(name);
                }
            }

            if (!json.isNull("permissionName")) {
                permissionName = json.getString("permissionName");
               /* if (application.getPermissionMap().containsKey(permissionName)) {
                    holder.button.setVisibility(View.VISIBLE);
                }else {
                    holder.button.setVisibility(View.GONE);
                }*/
            }

            if (!json.isNull("appPermission")) {
                appPermission = json.getString("appPermission");
            }

            if (appPermission != null){
                if (application.getPermissionMap().containsKey(appPermission)) {
                    holder.button.setVisibility(View.VISIBLE);
                }else {
                    holder.button.setVisibility(View.GONE);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonObjectList.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private Button button;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            button = itemView.findViewById(R.id.btn_action);
        }
    }
}
