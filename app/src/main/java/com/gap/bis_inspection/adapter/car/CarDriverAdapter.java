package com.gap.bis_inspection.adapter.car;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/25/2016.
 */
public class CarDriverAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;
    private IDatabaseManager databaseManager;
    private String driverCode;
    private Bitmap bitmap;

    public CarDriverAdapter(Context context, int resourceId, List<JSONObject> list) {
        super(context, resourceId, list);
        this.list = list;
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            }
            final JSONObject driverJobListJsonObject = list.get(position);
            databaseManager = new DatabaseManager(getContext());
            //myTask = new ASync();
            //myTask.execute();

            if (driverJobListJsonObject != null) {
                if (!driverJobListJsonObject.isNull("type")) {
                    int driverType = driverJobListJsonObject.getInt("type");
                    switch (driverType) {
                        case 0:
                            ((TextView) convertView.findViewById(R.id.driverType_TV)).setText(R.string.enumType_DriverType_MainDriver);
                            break;
                        case 1:
                            ((TextView) convertView.findViewById(R.id.driverType_TV)).setText(R.string.enumType_DriverType_AssistantDriver);
                            break;
                        case 2:
                            ((TextView) convertView.findViewById(R.id.driverType_TV)).setText(R.string.enumType_DriverType_OrganizationDriver);
                            break;
                    }
                } else {
                    ((TextView) convertView.findViewById(R.id.driverType_TV)).setText("---");
                }

                if (!driverJobListJsonObject.isNull("driver")) {
                    JSONObject driverJsonObject = driverJobListJsonObject.getJSONObject("driver");
                    driverCode = driverJsonObject.getString("driverCode");

                    String nameFamily = "";
                    if (!driverJsonObject.isNull("person")) {
                        JSONObject personJsonObject = driverJsonObject.getJSONObject("person");
                        nameFamily = personJsonObject.getString("name") + " - " + personJsonObject.getString("family");
                        ((TextView) convertView.findViewById(R.id.driverName_TV)).setText(nameFamily + "(" + driverCode + ")");

                        if (!personJsonObject.isNull("address")) {
                            JSONObject addressJsonObject = personJsonObject.getJSONObject("address");
                            if (!addressJsonObject.isNull("mobileNo")) {
                                ((TextView) convertView.findViewById(R.id.mobileNo_TV)).setText(addressJsonObject.getString("mobileNo"));
                            }
                        } else {
                            ((TextView) convertView.findViewById(R.id.mobileNo_TV)).setText("---");
                        }


                    } else {
                        ((TextView) convertView.findViewById(R.id.driverName_TV)).setText("---");
                    }


                    if (!driverJobListJsonObject.isNull("driverJobTypeEn")) {
                        int type = driverJobListJsonObject.getInt("driverJobTypeEn");
                        switch (type) {
                            case 0:
                                ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText(R.string.enumType_DriverJobTypeEn_DetermineCarForDriver);
                                break;
                            case 1:
                                ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText(R.string.enumType_DriverJobTypeEn_RotatoryDriverInLine);
                                break;
                            case 2:
                                ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText(R.string.enumType_DriverJobTypeEn_DriverInParking);
                                break;
                            case 3:
                                ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText(R.string.enumType_DriverJobTypeEn_RescuerSOS);
                                break;
                            case 4:
                                ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText(R.string.enumType_DriverJobTypeEn_AssistantRescuerSOS);
                                break;
                            case 5:
                                ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText(R.string.enumType_DriverJobTypeEn_WorkOnContract);
                                break;
                        }

                    } else {
                        ((TextView) convertView.findViewById(R.id.driverJob_TV)).setText("---");
                    }

                    if (!driverJobListJsonObject.isNull("person")) {
                        JSONObject personJsonObject = driverJobListJsonObject.getJSONObject("person");
                        if (!personJsonObject.isNull("pictureBytes")) {
                            JSONArray pictureBytesJsonArray = personJsonObject.getJSONArray("pictureBytes");
                            byte[] bytes = new byte[pictureBytesJsonArray.length()];
                            for (int i = 0; i < pictureBytesJsonArray.length(); i++) {
                                bytes[i] = Integer.valueOf(pictureBytesJsonArray.getInt(i)).byteValue();
                            }
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            //imageUser.setImageBitmap(bitmap);
                            ((ImageView) convertView.findViewById(R.id.image_User)).setImageBitmap(bitmap);
                        } else {
                            //imageUser.setBackgroundResource(R.drawable.driver_image_null);
                            ((ImageView) convertView.findViewById(R.id.image_User)).setBackgroundResource(R.drawable.driver_image_null);
                        }
                    }
                }

            }
            //System.out.println("returned_bitmap===" + bitmap);
            //((ImageView) convertView.findViewById(R.id.image_User)).setImageBitmap(bitmap);

        } catch (Exception e) {
            Log.i(CarDriverAdapter.class.toString(), e.getMessage());
        }

      /*  if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#efd3d2"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }*/

        return convertView;
    }

    @Override
    public long getItemId(int position) {

        try {
            JSONObject jsonObject = list.get(position);
            return jsonObject.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
