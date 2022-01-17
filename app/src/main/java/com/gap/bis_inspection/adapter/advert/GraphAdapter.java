package com.gap.bis_inspection.adapter.advert;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.util.MyValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.CustomViewHolder> {

    private List<JSONObject> jsonObjectList;
    private Context context;

    public GraphAdapter(List<JSONObject> jsonObjectList, Context context) {
        this.jsonObjectList = jsonObjectList;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_items, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        JSONObject json = jsonObjectList.get(position);

        try {

            if (!json.isNull("countLabel")) {
                holder.countLabel.setText(" تعداد کل " + " : " + json.getString("countLabel"));
            }

            if (!json.isNull("titlelabel")) {
                holder.titlelabel.setText(json.getString("titlelabel"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (!json.isNull("chartStr")) {
            String chartStr = null;
            String str_0 = null;
            String str_2 = null;
            int str_1 = 0;
            try {
                chartStr = json.getString("chartStr");
                JSONObject chartStrJsonObject = new JSONObject(chartStr);
                List<BarEntry> entries = new ArrayList<BarEntry>();
                List<String> xValues = new ArrayList<String>();
                List<Integer> colors = new ArrayList<>();
                if (!chartStrJsonObject.isNull("value")) {
                    JSONArray valueJSONArray = (JSONArray) chartStrJsonObject.getJSONArray("value");
                    //colors.add(context.getResources().getColor(R.color.colorAccent));
                    for (int j = 1; j < valueJSONArray.length(); j++) {
                        JSONArray valueObjectJSONArray = (JSONArray) valueJSONArray.get(j);
                        if (valueObjectJSONArray.get(0) != null && valueObjectJSONArray.get(1) != null && valueObjectJSONArray.get(2) != null) {
                            str_0 = (String) valueObjectJSONArray.get(0);
                            str_1 = (int) valueObjectJSONArray.get(1);
                            str_2 = (String) valueObjectJSONArray.get(2);

                            if (str_0 != null && str_1 != 0) {
                                    entries.add(new BarEntry(str_1, j - 1));
                                    xValues.add(str_0);
                                    colors.add(Color.parseColor(str_2));

                                    System.out.println("======================================================================= " + j + " ==== " + position);
                                    System.out.println("==========str_0==========" + str_0);
                                    System.out.println("==========str_1==========" + str_1);
                                    System.out.println("==========str_2==========" + str_2);

                            }
                        }


                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Label");
                    if (colors.size() != 0){
                        dataSet.setColors(colors);
                    }

                    dataSet.setValueTextColor(Color.rgb(0, 155, 0));
                    dataSet.setValueFormatter(new MyValueFormatter());

                    BarData barData = new BarData(xValues, dataSet);
                    holder.chart.setData(barData);
                    holder.chart.setDescription("");
                    holder.chart.invalidate();
                    holder.chart.animateXY(1000, 1000);
                    holder.chart.setDrawBarShadow(false);
                    holder.chart.setDrawValueAboveBar(true);
                    holder.chart.setPinchZoom(true);
                    holder.chart.setDrawGridBackground(false);
                    holder.chart.getAxisRight().setEnabled(false);
                    holder.chart.getAxisLeft().setEnabled(false);
                    holder.chart.enableScroll();
                    holder.chart.setHorizontalScrollBarEnabled(true);
                    XAxis xl = holder.chart.getXAxis();
                    xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xl.setDrawLabels(true);
                    xl.setDrawAxisLine(true);
                    xl.setDrawGridLines(false);
                    xl.setSpaceBetweenLabels(0);

                    Legend l = holder.chart.getLegend();
                    l.setEnabled(false);
                    // l.setFormSize(8f);
                    l.setXEntrySpace(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public int getItemCount() {
        return jsonObjectList.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private BarChart chart;
        private TextView titlelabel, countLabel;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.chart);
            titlelabel = itemView.findViewById(R.id.titlelabel);
            countLabel = itemView.findViewById(R.id.countLabel);
        }
    }
}
