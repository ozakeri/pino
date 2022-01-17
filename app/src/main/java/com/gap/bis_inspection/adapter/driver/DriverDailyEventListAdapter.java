package com.gap.bis_inspection.adapter.driver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gap.bis_inspection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DriverDailyEventListAdapter extends RecyclerView.Adapter<DriverDailyEventListAdapter.CustomView> {

    private List<JSONObject> arrayList;
    private LayoutInflater inflater;

    public DriverDailyEventListAdapter(List<JSONObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CustomView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_event_activity_driver_layout_items, parent, false);
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomView holder, int position) {

        JSONObject driverEventJsonObject = arrayList.get(position);
        System.out.println("driverEventJsonObject====" + driverEventJsonObject);
        if (driverEventJsonObject != null) {
            try {

                if (!driverEventJsonObject.isNull("person")) {
                    JSONObject personJsonObject = driverEventJsonObject.getJSONObject("person");

                    if (!personJsonObject.isNull("nameFv")) {
                        String name = personJsonObject.getString("nameFv");
                        holder.txt_name.setText(name);
                    }

                    if (!personJsonObject.isNull("employeeCode")) {
                        String name = personJsonObject.getString("employeeCode");
                        holder.txt_driverCode.setText(name);
                    }

                    if (!personJsonObject.isNull("pictureAF")) {
                        JSONObject pictureAFJSONObject = personJsonObject.getJSONObject("pictureAF");

                        if (!pictureAFJSONObject.isNull("pictureBytes")) {
                            JSONArray pictureBytesJsonArray = pictureAFJSONObject.getJSONArray("pictureBytes");
                            byte[] bytes = new byte[pictureBytesJsonArray.length()];
                            for (int i = 0; i < pictureBytesJsonArray.length(); i++) {
                                bytes[i] = Integer.valueOf(pictureBytesJsonArray.getInt(i)).byteValue();
                            }
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            holder.img_driver.setImageBitmap(bitmap);
                        }


                    } else {
                        holder.img_driver.setBackgroundResource(R.drawable.driver_image_null);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CustomView extends RecyclerView.ViewHolder {

        private TextView txt_name, txt_driverCode;
        private ImageView img_driver;

        public CustomView(View itemView) {
            super(itemView);

            txt_name = itemView.findViewById(R.id.txt_name);
            txt_driverCode = itemView.findViewById(R.id.txt_driverCode);
            img_driver = itemView.findViewById(R.id.img_driver);
        }
    }
}
