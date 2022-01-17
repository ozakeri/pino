package com.gap.bis_inspection.adapter.advert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.HejriUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.CustomViewHolder> {

    private final List<JSONObject> jsonObjectList;
    private AppController application;

    public AdvertAdapter(List<JSONObject> jsonObjectList,AppController application) {
        this.jsonObjectList = jsonObjectList;
        this.application = application;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_items, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        JSONObject json = jsonObjectList.get(position);
        System.out.println("size=-==-=-=-" + jsonObjectList.size());
        String requestDateFrom = "";
        String fromTime = "";
        String toTime = "";
        String description = "";
        String appPermission = "";
        String processBisDataParamValue = "";
        int processStatus = 0;
        boolean processStatusIsValidForEdit = false;


        try {

            if (position % 2 == 0){
                holder.relativeLayout.setBackgroundResource(R.color.mdtp_circle_color);
            }else {
                holder.relativeLayout.setBackgroundResource(R.color.white);
            }

            if (!json.isNull("processBisDataVO")) {
                JSONObject jsonDate = json.getJSONObject("processBisDataVO");

                if (!jsonDate.isNull("processBisSetting")) {
                    JSONObject processBisSettingJsonObject = jsonDate.getJSONObject("processBisSetting");
                    if (!processBisSettingJsonObject.isNull("name")) {
                        holder.txt_process.setText(processBisSettingJsonObject.getString("name"));
                    }
                }

                if (!jsonDate.isNull("requestDateFrom")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    String date = jsonDate.getString("requestDateFrom");
                    Date startDate = simpleDateFormat.parse(date);
                    requestDateFrom = HejriUtil.chrisToHejri(startDate);
                    System.out.println("hejriStartDate=-==-=-=-" + requestDateFrom);
                }

                if (!jsonDate.isNull("processStatus")) {
                    processStatus = jsonDate.getInt("processStatus");
                }

                if (!jsonDate.isNull("processStatus")) {
                    processStatus = jsonDate.getInt("processStatus");
                }

                if (!jsonDate.isNull("processStatusIsValidForEdit")) {
                    processStatusIsValidForEdit = jsonDate.getBoolean("processStatusIsValidForEdit");
                }

                if (!jsonDate.isNull("appPermission")) {
                    appPermission = jsonDate.getString("appPermission");
                }

                if (!jsonDate.isNull("requestDateFrom")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String date = jsonDate.getString("requestDateFrom");
                    Date startDate = simpleDateFormat.parse(date);
                    fromTime = CalendarUtil.convertPersianDateTime(startDate, "HH:mm");
                    System.out.println("toTime=-==-=-=-" + fromTime);
                }

                if (!jsonDate.isNull("requestDateTo")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String date = jsonDate.getString("requestDateTo");
                    Date startDate = simpleDateFormat.parse(date);
                    toTime = CalendarUtil.convertPersianDateTime(startDate, "HH:mm");
                    System.out.println("toTime=-==-=-=-" + toTime);
                }

                if (!jsonDate.isNull("employeeDo")) {
                    holder.txt_userCreation.setText(jsonDate.getString("employeeDo"));
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String date = jsonDate.getString("startDate");

                System.out.println("date=-==-=-=-" + date);

                Date startDate = simpleDateFormat.parse(date);
                String hejriStartDate = HejriUtil.chrisToHejriDateTimeForAdvert(startDate);
                holder.txt_date.setText(CommonUtil.latinNumberToPersian(hejriStartDate));

                System.out.println("hejriStartDate=-==-=-=-" + hejriStartDate);
            }

            System.out.println("processStatusIsValidForEdit====" + processStatusIsValidForEdit);
            System.out.println("processStatus====" + processStatus);
            System.out.println("getPermissionMap====" + application.getPermissionMap().containsKey(appPermission));

            if (processStatusIsValidForEdit) {
                holder.img_edit.setVisibility(View.VISIBLE);
            } else {
                holder.img_edit.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonObjectList.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_process, txt_date, txt_userCreation;
        private ImageView img_edit;
        private RelativeLayout relativeLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_process = itemView.findViewById(R.id.txt_process);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_userCreation = itemView.findViewById(R.id.txt_userCreation);
            img_edit = itemView.findViewById(R.id.img_edit);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}
