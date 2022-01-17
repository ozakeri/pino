package com.gap.bis_inspection.adapter.driver;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CarEDAListAdapter extends RecyclerView.Adapter<CarEDAListAdapter.CustomView> {

    private List<JSONObject> arrayList;
    private LayoutInflater inflater;
    private Context context;
    private String date;

    public CarEDAListAdapter(Context context, List<JSONObject> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_driver_daily_event_items, parent, false);
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomView holder, int position) {

        JSONObject driverEventJsonObject = arrayList.get(position);
        System.out.println("driverEventJsonObject====" + driverEventJsonObject);
        if (driverEventJsonObject != null) {
            try {

                if (!driverEventJsonObject.isNull("driverProfile")) {
                    JSONObject driverProfileJSONObject = driverEventJsonObject.getJSONObject("driverProfile");

                    if (!driverProfileJSONObject.isNull("shiftTypeBaseEn_text")) {
                        holder.txt_driver.setText(driverProfileJSONObject.getString("shiftTypeBaseEn_text"));
                    }


                    if (!driverProfileJSONObject.isNull("autoCompleteLabel")) {
                        holder.txt_type.setText(driverProfileJSONObject.getString("autoCompleteLabel"));
                    }
                }

                if (!driverEventJsonObject.isNull("lineCompany")) {
                    JSONObject lineCompanyJSONObject = driverEventJsonObject.getJSONObject("lineCompany");
                    holder.txt_status.setText(lineCompanyJSONObject.getString("lineCode"));
                }


                if (!driverEventJsonObject.isNull("entityDailyActivityVO")) {
                    JSONObject entityDailyActivityVOJSONObject = driverEventJsonObject.getJSONObject("entityDailyActivityVO");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = sdf.parse(entityDailyActivityVOJSONObject.getString("startTime"));

                    if (CommonUtil.compareDates(date, new Date()) == 0) {
                        this.date = CalendarUtil.convertPersianDateTime(date, "HH:mm");
                        holder.txt_shift.setText(this.date);
                    } else {
                        this.date = CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd - HH:mm:ss");
                        holder.txt_shift.setText(this.date);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CustomView extends RecyclerView.ViewHolder {

        private TextView txt_status, txt_type, txt_driver, txt_shift;

        public CustomView(View itemView) {
            super(itemView);

            txt_status = itemView.findViewById(R.id.txt_status);
            txt_type = itemView.findViewById(R.id.txt_type);
            txt_driver = itemView.findViewById(R.id.txt_driver);
            txt_shift = itemView.findViewById(R.id.txt_shift);
        }
    }
}
