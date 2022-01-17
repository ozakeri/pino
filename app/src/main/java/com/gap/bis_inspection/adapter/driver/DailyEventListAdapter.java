package com.gap.bis_inspection.adapter.driver;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DailyEventListAdapter extends RecyclerView.Adapter<DailyEventListAdapter.CustomView> {

    private List<JSONObject> arrayList;
    private LayoutInflater inflater;
    private DateFormat sdf;
    private String date;

    public DailyEventListAdapter(List<JSONObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CustomView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_event_activity_layout_items, parent, false);
        if (arrayList.size() % 2 == 0){
            view.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }else {
            view.setBackgroundColor(Color.parseColor("#FDF6F6"));
        }
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomView holder, int position) {

        JSONObject driverEventJsonObject = arrayList.get(position);
        System.out.println("driverEventJsonObject====" + driverEventJsonObject);
        if (driverEventJsonObject != null) {
            try {


                if (!driverEventJsonObject.isNull("event")) {
                    JSONObject eventJsonObject = driverEventJsonObject.getJSONObject("event");
                    if (!eventJsonObject.isNull("name")) {
                        String name = eventJsonObject.getString("name");
                        holder.txt_name.setText(name);
                    }
                }

                if (!driverEventJsonObject.isNull("car")) {
                    JSONObject carJsonObject = driverEventJsonObject.getJSONObject("car");
                    if (!carJsonObject.isNull("propertyCode")) {
                        String name = carJsonObject.getString("propertyCode");
                        holder.txt_car.setText(name);
                    }
                }

                if (!driverEventJsonObject.isNull("line")) {
                    JSONObject lineJsonObject = driverEventJsonObject.getJSONObject("line");
                    if (!lineJsonObject.isNull("lineCode")) {
                        String name = lineJsonObject.getString("lineCode");
                        holder.txt_line.setText(name);
                    }
                }

                /*if (!driverEventJsonObject.isNull("userCreation")) {
                    JSONObject userCreationJsonObject = driverEventJsonObject.getJSONObject("userCreation");
                    if (!userCreationJsonObject.isNull("nameFamily")) {
                        String name = userCreationJsonObject.getString("nameFamily");
                        holder.txt_userCreation.setText(name);
                    }
                }*/

                /*if (!driverEventJsonObject.isNull("startTime")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    String time = driverEventJsonObject.getString("startTime");
                    Date data = simpleDateFormat.parse(time);
                    holder.txt_startTime.setText(CalendarUtil.convertPersianDateTime(data, "yyyy/MM/dd - HH:mm:ss"));
                }*/


                if (!driverEventJsonObject.isNull("startTime")) {
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = sdf.parse(driverEventJsonObject.getString("startTime"));

                    if (CommonUtil.compareDates(date, new Date()) == 0) {
                        this.date = CalendarUtil.convertPersianDateTime(date, "HH:mm");
                        holder.txt_dateCreation.setText(this.date);
                        System.out.println("1======" + this.date);
                    } else {
                        this.date = CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd - HH:mm:ss");
                        holder.txt_dateCreation.setText(this.date);
                        System.out.println("2======" + this.date);
                    }
                    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    //String time = driverEventJsonObject.getString("dateCreation");
                    //Date data = simpleDateFormat.parse(time);
                    //holder.txt_dateCreation.setText(CalendarUtil.convertPersianDateTime(data, "yyyy/MM/dd - HH:mm:ss"));

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

        private TextView txt_name, txt_car, txt_line, txt_dateCreation;
        private LinearLayout row_layout;

        public CustomView(View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.txt_name);
            txt_car = itemView.findViewById(R.id.txt_car);
            txt_line = itemView.findViewById(R.id.txt_line);
            txt_dateCreation = itemView.findViewById(R.id.txt_dateCreation);
            row_layout = itemView.findViewById(R.id.row_layout);
        }
    }
}
