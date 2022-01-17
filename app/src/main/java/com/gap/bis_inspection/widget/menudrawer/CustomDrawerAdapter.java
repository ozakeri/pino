package com.gap.bis_inspection.widget.menudrawer;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.BuildConfig;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.service.CoreService;

import java.util.List;

class CustomDrawerAdapter extends RecyclerView.Adapter<CustomDrawerAdapter.MyViewHolder> {

    private Context context;
    private List<DrawerItem> drawerItemList;
    private int layoutResID;
    private Handler handler = new Handler();
    private View view;
    private CoreService coreService;
    private AppController application;
    //Typeface tf;

    CustomDrawerAdapter(Context context, List<DrawerItem> listItems) {
        this.context = context;
        this.drawerItemList = listItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_drawer_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        DrawerItem dItem = this.drawerItemList.get(position);
        holder.itemLayout.setVisibility(LinearLayout.VISIBLE);
        //holder.icon.setImageDrawable(R.drawable);
        holder.icon.setImageDrawable(view.getResources().getDrawable(dItem.getImgResID()));
        holder.ItemName.setText(dItem.getItemName());

        coreService = new CoreService(new DatabaseManager(context));
        application = (AppController) context.getApplicationContext();

        if (dItem.getItemName() == R.string.label_menu_notification) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (application.getCurrentUser().getServerUserId() != null) {
                        Integer count = coreService.getCountOfUnreadMessage(application.getCurrentUser().getServerUserId());
                        if (count.compareTo(0) > 0) {
                            holder.counterVT.setText(String.valueOf(count));
                            holder.counterVT.setVisibility(View.VISIBLE);
                        } else {
                            holder.counterVT.setVisibility(View.INVISIBLE);
                        }
                    }
                    handler.postDelayed(this, 500);
                }
            }, 500);
        }

       /* if (dItem.getItemName() == R.string.label_menu_sync) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.ItemName.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(2, 0, 10, 0);

            holder.icon.setVisibility(View.INVISIBLE);
            holder.sync_icon.setVisibility(View.VISIBLE);
            holder.ItemName.setTextColor(context.getResources().getColor(R.color.txtColor_sync));
            holder.syncDateTV.setTextColor(context.getResources().getColor(R.color.txtColor_sync));

            holder.syncDateTV.setVisibility(View.VISIBLE);
            DeviceSetting deviceSetting = coreService.getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_TOTAL_SYNC_DATE);
            String lastSyncDate = "";
            if (deviceSetting != null && deviceSetting.getDateLastChange() != null) {
                lastSyncDate = HejriUtil.chrisToHejriDateTime(deviceSetting.getDateLastChange());
                //Toast.makeText(context, R.string.label_menu_syncToast, Toast.LENGTH_LONG).show();
            }
            holder.syncDateTV.setText(lastSyncDate);
        }*/
    }

    @Override
    public int getItemCount() {
        return drawerItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ItemName, counterVT, syncDateTV;
        ImageView icon, sync_icon;
        LinearLayout itemLayout;

        MyViewHolder(View view) {
            super(view);
            ItemName = (TextView) view.findViewById(R.id.drawer_itemName);
            counterVT = (TextView) view.findViewById(R.id.counter_VT);
            syncDateTV = (TextView) view.findViewById(R.id.syncDate_TV);
            icon = (ImageView) view.findViewById(R.id.drawer_icon);
            sync_icon = (ImageView) view.findViewById(R.id.sync_icon);
            itemLayout = (LinearLayout) view.findViewById(R.id.itemLayout);
        }

    }
}