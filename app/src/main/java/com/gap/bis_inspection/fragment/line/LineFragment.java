package com.gap.bis_inspection.fragment.line;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.report.ReportActivity;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;

import org.json.JSONException;
import org.json.JSONObject;

public class LineFragment extends Fragment {
    ImageView backIcon;
    TextView codeTV, nameTV, lineGreadeTV, lineCompanyControllerTV, lineCompanyProfit1TV, lineCompanyProfit2TV, serviceTimeTV, LineProfitCompanyTypeTV, priceCoFinalTV, eCardPriceTV;
    Long lineId = null;
    String displayName = null;
    RelativeLayout addIcon;
    String addIcon1 = "line";
    String nameFv;

    public LineFragment() {
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line, container, false);
        init(view);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        if (getArguments() != null) {

            String getLineInfo = getArguments().getString("line");
            logLargeString(getLineInfo);
            try {
                JSONObject lineInfoJsonObject = new JSONObject(getLineInfo);

                if (!lineInfoJsonObject.isNull("id")) {
                    lineId = lineInfoJsonObject.getLong("id");
                }

                if (!lineInfoJsonObject.isNull("code")) {
                    codeTV.setText(lineInfoJsonObject.getString("code"));
                    displayName = lineInfoJsonObject.getString("code");
                } else {
                    codeTV.setText("---");
                }


                if (!lineInfoJsonObject.isNull("name")) {
                    nameTV.setText(lineInfoJsonObject.getString("name"));
                    if (displayName == null) {
                        displayName = lineInfoJsonObject.getString("name");
                    } else {
                        displayName += " " + lineInfoJsonObject.getString("name");
                    }
                }


                if (!lineInfoJsonObject.isNull("lineGrade")) {
                    int type = lineInfoJsonObject.getInt("lineGrade");
                    if (type == 0) {
                        lineGreadeTV.setText(R.string.enumType_GeneralEnum_Val1_Line);
                    } else if (type == 1) {
                        lineGreadeTV.setText(R.string.enumType_GeneralEnum_Val2_Line);
                    } else if (type == 2) {
                        lineGreadeTV.setText(R.string.enumType_GeneralEnum_Val3_Line);
                    } else if (type == 3) {
                        lineGreadeTV.setText(R.string.enumType_GeneralEnum_Val4_Line);
                    }

                } else {
                    lineGreadeTV.setText("---");
                }


                if (!lineInfoJsonObject.isNull("lineCompanyController")) {
                    JSONObject lineCompanyControllerJsonObject = lineInfoJsonObject.getJSONObject("lineCompanyController");
                    if (!lineCompanyControllerJsonObject.isNull("company")) {
                        JSONObject companyJsonObject = lineCompanyControllerJsonObject.getJSONObject("company");
                        lineCompanyControllerTV.setText(companyJsonObject.getString("name"));
                    }
                } else {
                    lineCompanyControllerTV.setText("---");
                }


                if (!lineInfoJsonObject.isNull("lineCompanyProfit1")) {
                    JSONObject lineCompanyProfit1JsonObject = lineInfoJsonObject.getJSONObject("lineCompanyProfit1");
                    if (!lineCompanyProfit1JsonObject.isNull("company")) {
                        JSONObject companyJsonObject = lineCompanyProfit1JsonObject.getJSONObject("company");
                        lineCompanyProfit1TV.setText(companyJsonObject.getString("name"));
                    }
                } else {
                    lineCompanyProfit1TV.setText("---");
                }


                if (!lineInfoJsonObject.isNull("lineCompanyProfit2")) {
                    JSONObject lineCompanyProfit2JsonObject = lineInfoJsonObject.getJSONObject("lineCompanyProfit2");
                    if (!lineCompanyProfit2JsonObject.isNull("company")) {
                        JSONObject companyJsonObject = lineCompanyProfit2JsonObject.getJSONObject("company");
                        lineCompanyProfit2TV.setText(companyJsonObject.getString("name"));
                    }
                } else {
                    lineCompanyProfit2TV.setText("---");
                }


                if (!lineInfoJsonObject.isNull("serviceTime")) {
                    int type = lineInfoJsonObject.getInt("serviceTime");
                    if (type == 0) {
                        serviceTimeTV.setText(R.string.enumType_LineServiceTime_Daily);
                    } else if (type == 1) {
                        serviceTimeTV.setText(R.string.enumType_LineServiceTime_Nightly);
                    } else if (type == 2) {
                        serviceTimeTV.setText(R.string.enumType_LineServiceTime_BothItem);
                    }
                } else {
                    serviceTimeTV.setText("---");
                }



                if (!lineInfoJsonObject.isNull("lineCompanyProfit")) {
                    JSONObject lineCompanyProfitJsonObject = lineInfoJsonObject.getJSONObject("lineCompanyProfit");
                    if (!lineCompanyProfitJsonObject.isNull("company")) {
                        JSONObject companyJsonObject = lineCompanyProfitJsonObject.getJSONObject("company");
                        nameFv = companyJsonObject.getString("nameFv");
                    }
                }

                if (!lineInfoJsonObject.isNull("lineProfitCompanyType")) {
                    int type = lineInfoJsonObject.getInt("lineProfitCompanyType");

                    switch (type) {
                        case 0:
                            LineProfitCompanyTypeTV.setText(getResources().getString(R.string.enumType_LineProfitCompanyType_Organization) + " - " + nameFv);
                            break;
                        case 1:
                            LineProfitCompanyTypeTV.setText(getResources().getString(R.string.enumType_LineProfitCompanyType_PrivateCo) + " - " + nameFv);
                            break;
                        case 2:
                            LineProfitCompanyTypeTV.setText(getResources().getString(R.string.enumType_LineProfitCompanyType_BothItem) + " - " + nameFv);
                            break;
                    }

                } else {
                    LineProfitCompanyTypeTV.setText("---");
                }


                if (!lineInfoJsonObject.isNull("linePrice")) {
                    JSONObject linePricejsonObject = lineInfoJsonObject.getJSONObject("linePrice");
                    String rialStr = getActivity().getResources().getString(R.string.rial_label);

                    String getpriceCoFinal = linePricejsonObject.getString("priceCoFinal");
                    priceCoFinalTV.setText(getpriceCoFinal + " " + rialStr);

                    String geteCardPrice = linePricejsonObject.getString("eCardPrice");
                    eCardPriceTV.setText(geteCardPrice + " " + rialStr);
                } else {
                    priceCoFinalTV.setText("---");
                    eCardPriceTV.setText("---");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("entityNameEn", EntityNameEn.Line.ordinal());
                intent.putExtra("entityId", Long.valueOf(lineId));
                intent.putExtra("displayName", displayName);
                intent.putExtra("addIcon", addIcon1);
                startActivity(intent);
            }
        });
        return view;
    }

    private void init(View view) {
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        addIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        codeTV = (TextView) view.findViewById(R.id.code_TV);
        nameTV = (TextView) view.findViewById(R.id.name_TV);
        lineGreadeTV = (TextView) view.findViewById(R.id.lineGreade_TV);
        lineCompanyControllerTV = (TextView) view.findViewById(R.id.lineCompanyController_TV);
        lineCompanyProfit1TV = (TextView) view.findViewById(R.id.lineCompanyProfit1_TV);
        lineCompanyProfit2TV = (TextView) view.findViewById(R.id.lineCompanyProfit2_TV);
        serviceTimeTV = (TextView) view.findViewById(R.id.serviceTime_TV);
        LineProfitCompanyTypeTV = (TextView) view.findViewById(R.id.LineProfitCompanyType_TV);
        priceCoFinalTV = (TextView) view.findViewById(R.id.priceCoFind_TV);
        eCardPriceTV = (TextView) view.findViewById(R.id.eCardPrice_TV);
    }

    public void logLargeString(String str) {
        String Tag = "LineFragment = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }

}
