package com.gap.bis_inspection.adapter.advert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdvertListAdapter extends RecyclerView.Adapter<AdvertListAdapter.CustomViewHolder> {

    private final List<JSONObject> jsonObjectList;

    public AdvertListAdapter(List<JSONObject> jsonObjectList) {
        this.jsonObjectList = jsonObjectList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_list_items, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        JSONObject json = jsonObjectList.get(position);
        System.out.println("size=-==-=-=-" + jsonObjectList.size());


        try {

            if (position % 2 == 0){
                holder.linearLayout.setBackgroundResource(R.color.mdtp_circle_color);
            }else {
                holder.linearLayout.setBackgroundResource(R.color.mdtp_accent_color_focused);
            }

            if (!json.isNull("adverNameStrFV")) {
                holder.txt_name.setText(json.getString("adverNameStrFV"));
            }

            if (!json.isNull("lineCompany")) {
                JSONObject jsonDate = json.getJSONObject("lineCompany");
                holder.txt_line.setText(CommonUtil.latinNumberToPersian(jsonDate.getString("lineCode")));
            }

            if (!json.isNull("car")) {
                JSONObject jsonDate = json.getJSONObject("car");
                holder.txt_car.setText(jsonDate.getString("plateText"));
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

        private TextView txt_name, txt_line, txt_car;
        private LinearLayout linearLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_line = itemView.findViewById(R.id.txt_line);
            txt_car = itemView.findViewById(R.id.txt_car);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
