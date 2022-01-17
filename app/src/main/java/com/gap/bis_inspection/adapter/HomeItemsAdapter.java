package com.gap.bis_inspection.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.entitiy.Permission;

import java.util.List;

public class HomeItemsAdapter extends RecyclerView.Adapter<HomeItemsAdapter.CustomView> {

    private List<Permission> permissionList;

    public HomeItemsAdapter(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    @NonNull
    @Override
    public CustomView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomView(LayoutInflater.from(parent.getContext()).inflate(R.layout.permission_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomView holder, int position) {

        Permission permissionName = permissionList.get(position);

        if (permissionName.getName().equals("ROLE_APP_INSPECTION_DRIVER_VIEW_LIST")) {

            if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
                holder.txt_permissionTitle.setText("راهبر");
            } else {
                holder.txt_permissionTitle.setText("راننده");
            }
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_driver);

        } else if (permissionName.getName().equals("ROLE_APP_INSPECTION_CAR_VIEW_LIST")) {
            holder.txt_permissionTitle.setText("خودرو");
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_car);

        } else if (permissionName.getName().equals("ROLE_APP_INSPECTION_LINE_VIEW_LIST")) {
            holder.txt_permissionTitle.setText("خط");
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_line);

        } else if (permissionName.getName().equals("ROLE_APP_GET_MNG_FLEET_VIEW")) {
            holder.txt_permissionTitle.setText("مدیریت ناوگان");
            holder.img_permissionPic.setBackgroundResource(R.drawable.logo_chart);

        } else if (permissionName.getName().equals("ROLE_APP_GET_ADVERTISEMENT_VIEW")) {
            holder.txt_permissionTitle.setText("تبلیغات");
            holder.img_permissionPic.setBackgroundResource(R.drawable.test1);

        } else if (permissionName.getName().equals("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST2")) {
            holder.txt_permissionTitle.setText("چک لیست ها");
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_checklist);

        } else if (permissionName.getName().equals("ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST1")) {
            holder.txt_permissionTitle.setText("فرم ها");
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_form);

        } else if (permissionName.getName().equals("ROLE_APP_INSPECTION_CREATE_COMPLAINT_REPORT")) {
            holder.txt_permissionTitle.setText("گزارشات");
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_report);

        } else if (permissionName.getName().equals("ROLE_APP_INSPECTION_WRITE_NOTIFICATION_MESSAGE")) {
            holder.txt_permissionTitle.setText("پیام ها");
            holder.img_permissionPic.setBackgroundResource(R.drawable.main_icon_message);
        }

    }

    @Override
    public int getItemCount() {
        return permissionList.size();
    }

    class CustomView extends RecyclerView.ViewHolder {
        ImageView img_permissionPic;
        TextView txt_permissionTitle;

        public CustomView(@NonNull View itemView) {
            super(itemView);
            img_permissionPic = itemView.findViewById(R.id.img_permissionPic);
            txt_permissionTitle = itemView.findViewById(R.id.txt_permissionTitle);
        }
    }
}
