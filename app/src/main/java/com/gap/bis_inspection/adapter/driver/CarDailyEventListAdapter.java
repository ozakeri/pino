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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CarDailyEventListAdapter extends RecyclerView.Adapter<CarDailyEventListAdapter.CustomView> {

    private List<JSONObject> arrayList;
    private LayoutInflater inflater;
    private Context context;

    public CarDailyEventListAdapter(Context context, List<JSONObject> arrayList) {
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

                if (!driverEventJsonObject.isNull("event")) {
                    JSONObject eventJSONObject = driverEventJsonObject.getJSONObject("event");
                    holder.txt_shift.setText(eventJSONObject.getString("eventTypeEn_text"));
                }

                if (!driverEventJsonObject.isNull("startTime")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date startDateConfirm = simpleDateFormat.parse(driverEventJsonObject.getString("startTime"));
                    holder.txt_driver.setText(CalendarUtil.convertPersianDateTime(startDateConfirm, "yyyy/MM/dd HH:mm:ss"));
                }

                if (!driverEventJsonObject.isNull("car")) {
                    JSONObject carJSONObject = driverEventJsonObject.getJSONObject("car");
                    holder.txt_type.setText(carJSONObject.getString("propertyCode"));
                }

                if (!driverEventJsonObject.isNull("line")) {
                    JSONObject lineJSONObject = driverEventJsonObject.getJSONObject("line");
                    holder.txt_status.setText(lineJSONObject.getString("lineCode"));
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
