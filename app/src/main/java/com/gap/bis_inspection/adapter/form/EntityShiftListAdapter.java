package com.gap.bis_inspection.adapter.form;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gap.bis_inspection.R;

import org.json.JSONObject;

import java.util.List;


public class EntityShiftListAdapter extends RecyclerView.Adapter<EntityShiftListAdapter.MyViewHolder> {

    private List<JSONObject> list;

    public EntityShiftListAdapter(List<JSONObject> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_shift_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject entityShiftListJsonObject = list.get(position);

        if (entityShiftListJsonObject != null) {
            try {
                if (!entityShiftListJsonObject.isNull("shiftTypeEn")) {
                    int shiftType = entityShiftListJsonObject.getInt("shiftTypeEn");
                    switch (shiftType) {
                        case 0:
                            holder.txt_shift.setText("شیفت صبح : ");
                            break;

                        case 1:
                            holder.txt_shift.setText("شیفت عصر : ");
                            break;
                    }

                }

                if (!entityShiftListJsonObject.isNull("timeFrom")) {
                    String timeFrom = entityShiftListJsonObject.getString("timeFrom");
                    holder.txt_start.setText("از ساعت : " + timeFrom);
                }

                if (!entityShiftListJsonObject.isNull("timeTo")) {
                    String timeTo = entityShiftListJsonObject.getString("timeTo");
                    holder.txt_end.setText(" تا ساعت : " + timeTo);
                }
            } catch (Exception e) {
                e.getMessage();
            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_shift, txt_start, txt_end;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_shift = (TextView) itemView.findViewById(R.id.txt_shift);//change Here
            txt_start = (TextView) itemView.findViewById(R.id.txt_start);//change Here
            txt_end = (TextView) itemView.findViewById(R.id.txt_end);//change Here
        }
    }
}
