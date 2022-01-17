package com.gap.bis_inspection.activity.advert;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.adapter.advert.AdvertAdapter;
import com.gap.bis_inspection.adapter.advert.AdvertAddAttachAdapter;
import com.gap.bis_inspection.adapter.advert.AdvertButtonStatusAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.GlobalDomain;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.EventBusModel;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.util.RecyclerTouchListener;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchAdvertActivity extends AppCompatActivity {

    private TextView txt_car;
    private TextView txt_line;
    private TextView txt_advertName;
    private TextView txt_status;
    private TextView txt_advert_date;
    private RecyclerView recyclerView, buttonRecyclerView, recyclerView_dialog;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private String id;
    private Button btn_attach_action;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final int REQUEST_CAMERA = 1;
    private Uri mCapturedImageURI;
    private Services services;
    private List<JSONObject> processBisSettingVOList;
    private String advertisementId = "";
    private List<AttachFile> attachFileList;
    private String ProcessBisDataVOId = "";
    private String startDate = "";
    private String requestDateStr = "";
    private Dialog dialog;
    private boolean show = false;
    private String propertyCode = null;
    private String plateText = "";
    private JSONObject jsonObj;
    private List<JSONObject> processBisDataVOList;
    private GlobalDomain globalDomain = GlobalDomain.getInstance();
    private int actionProcessStatus;
    private AppController application;
    private boolean processStatusIsValidForEdit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_advert);

        recyclerView = findViewById(R.id.recyclerView);
        buttonRecyclerView = findViewById(R.id.buttonRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        services = new Services(getApplicationContext());
        txt_car = findViewById(R.id.txt_car);
        txt_status = findViewById(R.id.txt_status);
        txt_advert_date = findViewById(R.id.txt_advert_date);
        txt_line = findViewById(R.id.txt_line);
        txt_advertName = findViewById(R.id.txt_advertName);
        RelativeLayout closeIcon = findViewById(R.id.closeIcon);
        application = (AppController) getApplication();
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //  String result = getIntent().getExtras().toString();
        try {
            if (getIntent().getStringExtra("result") != null) {
                jsonObj = new JSONObject(getIntent().getStringExtra("result"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateData();

        buttonRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject json = processBisSettingVOList.get(position);
                String name = "";
                String processBisSettingId = "";
                boolean haveAttachment = false;

                try {
                    if (!json.isNull("processBisSettingVO")) {
                        JSONObject processBisSettingVOJSONObject = json.getJSONObject("processBisSettingVO");
                        if (!processBisSettingVOJSONObject.isNull("name")) {
                            name = processBisSettingVOJSONObject.getString("name");
                            System.out.println("name===" + name);
                        }

                        if (!processBisSettingVOJSONObject.isNull("id")) {
                            processBisSettingId = processBisSettingVOJSONObject.getString("id");
                            System.out.println("====id===" + processBisSettingId);
                        }

                        if (!processBisSettingVOJSONObject.isNull("haveAttachment")) {
                            haveAttachment = processBisSettingVOJSONObject.getBoolean("haveAttachment");
                            System.out.println("haveAttachment===" + haveAttachment);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(SearchAdvertActivity.this, EditAdvertActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("processBisSettingId", processBisSettingId);
                intent.putExtra("haveAttachment", haveAttachment);
                intent.putExtra("isEdit", false);
                intent.putExtra("advertisementId", advertisementId);
                intent.putExtra("processBisSettingName", name);
                intent.putExtra("actionProcessStatus", actionProcessStatus);
                startActivity(intent);

                //showDialog(name, processBisSettingId, haveAttachment);
            }
        }));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject json = processBisDataVOList.get(position);
                String processBisSettingName = "";
                String processBisSettingId = "";
                String processBisDataParamValue = "";
                String id = null, description = null, permissionId = null, permissionName = null,conf2Permission = null;
                int processStatus = 0;

                boolean haveAttachment = false, conf2Req = false;

                try {


                    if (!json.isNull("processBisDataVO")) {

                        JSONObject processBisDataVO = json.getJSONObject("processBisDataVO");

                        System.out.println("appPermission=-=-=--" + processBisDataVO.getString("appPermission"));
                        if (!application.getPermissionMap().containsKey(processBisDataVO.getString("appPermission"))){
                            return;
                        }

                        if (!processBisDataVO.isNull("processBisSetting")) {
                            JSONObject processBisSettingVO = processBisDataVO.getJSONObject("processBisSetting");
                            if (!processBisSettingVO.isNull("name")) {
                                processBisSettingName = processBisSettingVO.getString("name");
                                System.out.println("name===" + processBisSettingName);
                            }

                            if (!processBisSettingVO.isNull("haveAttachment")) {
                                haveAttachment = processBisSettingVO.getBoolean("haveAttachment");
                            }

                            if (!processBisSettingVO.isNull("conf2Req")) {
                                conf2Req = processBisSettingVO.getBoolean("conf2Req");
                            }

                            if (!processBisSettingVO.isNull("permissionId")) {
                                permissionId = processBisSettingVO.getString("permissionId");
                            }

                            if (!processBisSettingVO.isNull("permissionName")) {
                                permissionName = processBisSettingVO.getString("permissionName");
                            }

                            if (!processBisSettingVO.isNull("conf2Permission")) {
                                conf2Permission = processBisSettingVO.getString("conf2Permission");
                            }

                            if (!processBisSettingVO.isNull("id")) {
                                processBisSettingId = processBisSettingVO.getString("id");
                                System.out.println("processBisSettingId===" + processBisSettingId);
                            }
                        }

                        JSONObject jsonDate = json.getJSONObject("processBisDataVO");
                        if (!jsonDate.isNull("id")) {
                            id = jsonDate.getString("id");
                        }

                        if (!jsonDate.isNull("processStatus")) {
                            processStatus = jsonDate.getInt("processStatus");
                        }

                        if (!jsonDate.isNull("description")) {
                            description = jsonDate.getString("description");
                        }

                        if (!jsonDate.isNull("startDate")) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            String date = jsonDate.getString("startDate");
                            Date requestDate = simpleDateFormat.parse(date);
                            startDate = date;
                        }

                        if (!jsonDate.isNull("processStatusIsValidForEdit")) {
                            processStatusIsValidForEdit = jsonDate.getBoolean("processStatusIsValidForEdit");
                        }
                       // if (processStatusIsValidForEdit && processStatus == 0) {
                            Intent intent = new Intent(SearchAdvertActivity.this, EditAdvertActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("description", description);
                            intent.putExtra("isEdit", true);
                            intent.putExtra("processBisSettingName", processBisSettingName);
                            intent.putExtra("processBisSettingId", processBisSettingId);
                            intent.putExtra("processStatus", processStatus);
                            intent.putExtra("haveAttachment", haveAttachment);
                            intent.putExtra("conf2Req", conf2Req);
                            intent.putExtra("permissionId", permissionId);
                            intent.putExtra("permissionName", permissionName);
                            intent.putExtra("conf2Permission", conf2Permission);
                            intent.putExtra("actionProcessStatus", actionProcessStatus);
                            intent.putExtra("advertisementId", advertisementId);
                            intent.putExtra("processStatusIsValidForEdit", processStatusIsValidForEdit);
                            intent.putExtra("car", txt_car.getText());
                            intent.putExtra("advertName", txt_advertName.getText());
                            intent.putExtra("requestDate", requestDateStr);
                            intent.putExtra("startDate", startDate);
                            startActivity(intent);
                       // }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }));

        //editAdvert("","");

    }

    private void updateData() {
        try {
            if (jsonObj != null) {
                if (!jsonObj.isNull(Constants.RESULT_KEY)) {
                    JSONObject jsonObject = jsonObj.getJSONObject(Constants.RESULT_KEY);
                    System.out.println("jsonObject=========" + jsonObject);
                    if (!jsonObject.isNull("advertisementDetailVO")) {
                        JSONObject advertisementDetailVOJsonObject = jsonObject.getJSONObject("advertisementDetailVO");
                        if (!advertisementDetailVOJsonObject.isNull("processBisDataList")) {
                            JSONArray processBisDataVOListJsonObject = advertisementDetailVOJsonObject.getJSONArray("processBisDataList");
                            processBisDataVOList = new ArrayList<>();
                            System.out.println("length=-=-=-=-" + processBisDataVOListJsonObject.length());
                            for (int i = 0; i < processBisDataVOListJsonObject.length(); i++) {

                                JSONObject processBisDataVOJsonObject = (JSONObject) processBisDataVOListJsonObject.get(i);
                                processBisDataVOList.add(processBisDataVOJsonObject);

                            }
                            System.out.println("processBisDataVOList=-=-=-=-" + processBisDataVOList.size());
                            AdvertAdapter advertAdapter = new AdvertAdapter(processBisDataVOList,application);
                            recyclerView.setAdapter(advertAdapter);

                            JSONObject json = processBisDataVOList.get(processBisDataVOList.size() - 1);
                            if (!json.isNull("processBisDataVO")) {
                                JSONObject jsonDate = json.getJSONObject("processBisDataVO");
                                String idForAttachFile = jsonDate.getString("id");
                                System.out.println("idForAttachFile=-==-=-=-" + idForAttachFile);
                            }
                        }

                        if (!advertisementDetailVOJsonObject.isNull("id")) {
                            advertisementId = advertisementDetailVOJsonObject.getString("id");
                        }

                        if (!advertisementDetailVOJsonObject.isNull("actionProcessStatus")) {
                            actionProcessStatus = advertisementDetailVOJsonObject.getInt("actionProcessStatus");
                        }

                        if (!advertisementDetailVOJsonObject.isNull("lastProcessBisDataIsValidForDoNext")) {
                            show = advertisementDetailVOJsonObject.getBoolean("lastProcessBisDataIsValidForDoNext");
                        }

                        if (!advertisementDetailVOJsonObject.isNull("car")) {
                            JSONObject carJsonObject = advertisementDetailVOJsonObject.getJSONObject("car");

                            if (!carJsonObject.isNull("plateText")) {
                                txt_car.setText(carJsonObject.getString("plateText"));
                            }

                            if (!carJsonObject.isNull("propertyCode")) {
                                propertyCode = carJsonObject.getString("propertyCode");
                                SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                                editor.putString("propertyCode", propertyCode);
                                editor.apply();
                            }
                        }

                        if (!advertisementDetailVOJsonObject.isNull("advert")) {
                            JSONObject advertJsonObject = advertisementDetailVOJsonObject.getJSONObject("advert");
                            if (!advertJsonObject.isNull("name")) {
                                txt_advertName.setText(advertJsonObject.getString("name"));
                            }
                        }

                        if (!advertisementDetailVOJsonObject.isNull("lineCompany")) {
                            JSONObject lineCompanyJsonObject = advertisementDetailVOJsonObject.getJSONObject("lineCompany");
                            if (!lineCompanyJsonObject.isNull("lineCode")) {
                                txt_line.setText(CommonUtil.latinNumberToPersian(lineCompanyJsonObject.getString("lineCode")));
                            }
                        }

                        if (!advertisementDetailVOJsonObject.isNull("processStatus_text")) {
                            txt_status.setText(advertisementDetailVOJsonObject.getString("processStatus_text"));
                        }


                        if (!advertisementDetailVOJsonObject.isNull("advertisement")) {
                            JSONObject advertisement = advertisementDetailVOJsonObject.getJSONObject("advertisement");
                            if (!advertisement.isNull("requestDate")) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                String date = advertisement.getString("requestDate");
                                Date requestDate = simpleDateFormat.parse(date);
                                txt_advert_date.setText(CommonUtil.latinNumberToPersian(HejriUtil.chrisToHejri(requestDate)));
                                requestDateStr = advertisement.getString("requestDate");
                            }
                        }

                        if (!advertisementDetailVOJsonObject.isNull("processBisSettingVOList")) {
                            JSONArray processBisSettingVOListJsonObject = advertisementDetailVOJsonObject.getJSONArray("processBisSettingVOList");
                            processBisSettingVOList = new ArrayList<>();
                            for (int i = 0; i < processBisSettingVOListJsonObject.length(); i++) {
                                JSONObject processBisDataVOJsonObject = (JSONObject) processBisSettingVOListJsonObject.get(i);
                                processBisSettingVOList.add(processBisDataVOJsonObject);
                            }

                            if (show) {
                                buttonRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                buttonRecyclerView.setVisibility(View.GONE);
                            }
                            buttonRecyclerView.setAdapter(new AdvertButtonStatusAdapter(processBisSettingVOList,application));
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void actionSave(String id, String description) {
        class SaveAd extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;
            private ProgressDialog progressDialog = null;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
                progressDialog = new ProgressDialog(SearchAdvertActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SaveAd.this.cancel(true);
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();

                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!jsonObject.isNull("processBisDataVO")) {
                            JSONObject processBisDataVO = jsonObject.getJSONObject("processBisDataVO");
                            if (!processBisDataVO.isNull("id")) {
                                ProcessBisDataVOId = processBisDataVO.getString("id");
                                showDialogAttach();
                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(SearchAdvertActivity.this, "result is Null", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,SearchAdvertActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (isDeviceDateTimeValid()) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("username", application.getCurrentUser().getUsername());
                        jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                        jsonObject.put("id", advertisementId);
                        jsonObject.put("ProcessBisSettingId", id);
                        jsonObject.put("advertDescription", description);
                        //jsonObject.put("carInfoType", carInfoType);
                        MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, SearchAdvertActivity.this);
                        try {
                            result = postJsonService.sendData("saveAdvert", jsonObject, true);
                        } catch (SocketTimeoutException | SocketException e) {
                            errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                        } catch (WebServiceException e) {
                            Log.d("RegistrationFragment", e.getMessage());
                        }

                    } catch (JSONException e) {
                        Log.d("RegistrationFragment", e.getMessage());
                    }
                }
                return null;
            }


            ////******getServerDateTime*******////

            private boolean isDeviceDateTimeValid() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                try {
                    JSONObject jsonObjectParam = new JSONObject();
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, SearchAdvertActivity.this);
                    result = postJsonService.sendData("getServerDateTime", jsonObjectParam, true);

                    if (result != null) {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            Date serverDateTime = simpleDateFormat.parse(jsonObject.getString("serverDateTime"));
                            if (DateUtils.isValidDateDiff(new Date(), serverDateTime, Constants.VALID_SERVER_AND_DEVICE_TIME_DIFF)) {
                                DeviceSetting deviceSetting = coreService.getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
                                if (deviceSetting == null) {
                                    deviceSetting = new DeviceSetting();
                                    deviceSetting.setKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
                                }
                                deviceSetting.setValue(simpleDateFormat.format(new Date()));
                                deviceSetting.setDateLastChange(new Date());
                                coreService.saveOrUpdateDeviceSetting(deviceSetting);
                                return true;
                            } else {
                                errorMsg = getResources().getString(R.string.Invalid_Device_Date_Time);
                            }
                        }
                    }
                } catch (SocketTimeoutException | SocketException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (JSONException | ParseException | WebServiceException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    Log.d("SyncActivity", e.getMessage());
                }
                return false;
            }
        }
        new SaveAd().execute();
    }


    private void showAttachDialog() {
        final Dialog dialog = new Dialog(SearchAdvertActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_attach_checklist);
        TextView camera = (TextView) dialog.findViewById(R.id.camera_VT);
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery_VT);
        gallery.setVisibility(View.GONE);
        RelativeLayout closeIcon = (RelativeLayout) dialog.findViewById(R.id.closeIcon);
        dialog.show();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (SearchAdvertActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale
                                    (SearchAdvertActivity.this, Manifest.permission.CAMERA)) {
                        finish();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                    new String[]{Manifest.permission
                                            .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST);
                        }
                    }

                } else {
                    cameraIntent();
                }
                dialog.dismiss();
            }
        });
    }

    //========= cameraIntent for attachment
    public void cameraIntent() {
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String path = null;
            path = getPathCamera();
            saveAttachImageFile(path);
            refreshAttachAdapter();
        }
    }

    /*getPathCamera */
    private String getPathCamera() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index_data);
    }


    public void saveAttachImageFile(String filePath) {
        File file = new File(String.valueOf(filePath));
        file = saveBitmapToFile(file);
        AttachFile attachFile = new AttachFile();
        String userFileName = file.getName();
        long length = file.length();
        length = length / 1024;
        System.out.println("File Path : " + file.getPath() + ", File size : " + length + " KB");
        String filePostfix = userFileName.substring(userFileName.indexOf("."), userFileName.length());
        String path = Environment.getExternalStorageDirectory().toString() + Constants.DEFAULT_OUT_PUT_DIR + Constants.DEFAULT_IMG_OUT_PUT_DIR;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        attachFile.setAttachFileLocalPath(filePath);
        attachFile.setAttachFileUserFileName(userFileName);
        attachFile.setSendingStatusDate(new Date());
        attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        attachFile.setEntityNameEn(EntityNameEn.ProcessBisData.ordinal());
        attachFile.setServerAttachFileSettingId((long) 103);
        attachFile.setEntityId(Long.valueOf(ProcessBisDataVOId));
        attachFile.setServerEntityId(Long.valueOf(ProcessBisDataVOId));
        attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        coreService.insertAttachFile(attachFile);

        System.out.println("attachFile.getId====" + attachFile.getId());

        String newFilePath = path + "/" + attachFile.getId() + filePostfix;
        try {
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(newFilePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //try to decrease decoded image
            options.inPurgeable = true; //purgeable to disk
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream); //compressed bitmap to file

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            Long fileSize = new File(newFilePath).length();
            attachFile.setAttachFileSize(fileSize.intValue() / 1024);
            coreService.updateAttachFile(attachFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SaveAttachFile implements Runnable {
        @Override
        public void run() {
            System.out.println("SaveAttachFile=====");
            services.resumeAttachFileList("");
        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    public void showDialog(String name, String id, boolean haveAttachment) {

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        final Dialog dialog = new Dialog(SearchAdvertActivity.this);
        dialog.getWindow().setLayout(width, height);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.advert_dialog);

        RelativeLayout closeIcon = dialog.findViewById(R.id.closeIcon);
        TextView txt_processName = dialog.findViewById(R.id.txt_processName);
        TextView txt_date = dialog.findViewById(R.id.txt_date);
        EditText edt_description = dialog.findViewById(R.id.edt_description);
        Button btn_action = dialog.findViewById(R.id.btn_action);

        txt_processName.setText(name);
        String strDate = CalendarUtil.convertPersianDateTime(new Date(), "yyyy/MM/dd");
        txt_date.setText(CommonUtil.latinNumberToPersian(strDate));

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (edt_description.getText() == null || edt_description.getText().length() == 0) {
                    Toast toast = Toast.makeText(SearchAdvertActivity.this, "تعداد محدود", Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast);
                    toast.show();
                    return;
                }*/

                if (haveAttachment) {
                    actionSave(id, edt_description.getText().toString());
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    public void showDialogAttach() {
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

        dialog = new Dialog(SearchAdvertActivity.this);
        dialog.getWindow().setLayout(width, height);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.attach_dialog);
        TextView txt_processName = dialog.findViewById(R.id.txt_processName);
        btn_attach_action = dialog.findViewById(R.id.btn_attach_action);
        ImageView img_add = dialog.findViewById(R.id.img_add);
        RelativeLayout closeIcon = dialog.findViewById(R.id.closeIcon);
        recyclerView_dialog = dialog.findViewById(R.id.recyclerView_dialog);
        recyclerView_dialog.setLayoutManager(new GridLayoutManager(this, 2));
        dialog.show();

        refreshAttachAdapter();

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        recyclerView_dialog.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AttachFile attachFile = (AttachFile) attachFileList.get(position);
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                if (attachFile != null) {
                    String result = "checkList";
                    intent.putExtra("imagePath", attachFile.getAttachFileLocalPath());
                    intent.putExtra("result", result);
                }
                startActivity(intent);
            }
        }));

        recyclerView_dialog.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView_dialog, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                //PopupMenu popup = new PopupMenu(SearchAdvertActivity.this, view);
                // popup.getMenuInflater().inflate(R.menu.attachment_menu, popup.getMenu());
                //popup.show();

                //creating a popup menu
                PopupMenu popup = new PopupMenu(SearchAdvertActivity.this, view);
                //inflating menu from xml resource
                popup.inflate(R.menu.attachment_menu);
                //adding click listener


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete) {
                            attachFileList = coreService.getAttachFileListById(Long.valueOf(ProcessBisDataVOId));
                            AttachFile attachFile = (AttachFile) attachFileList.get(position);
                            coreService.deleteAttachFile(attachFile);
                            refreshAttachAdapter();
                        }
                        return false;
                    }
                });

                popup.show();
            }
        }));

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachFileList.size() < 4) {
                    showAttachDialog();
                } else {
                    Toast toast = Toast.makeText(SearchAdvertActivity.this, "تعداد محدود", Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,SearchAdvertActivity.this);
                    toast.show();
                }

            }
        });

        btn_attach_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                services.resumeAttachFileList("");
            }
        });
    }


    public void refreshAttachAdapter() {
        attachFileList = coreService.getAttachFileListById(Long.valueOf(ProcessBisDataVOId));
        AdvertAddAttachAdapter adapter = new AdvertAddAttachAdapter(attachFileList);
        recyclerView_dialog.setAdapter(adapter);
        if (attachFileList.size() > 0) {
            recyclerView_dialog.setVisibility(View.VISIBLE);
            btn_attach_action.setVisibility(View.VISIBLE);
        } else {
            recyclerView_dialog.setVisibility(View.GONE);
            btn_attach_action.setVisibility(View.GONE);
        }
        System.out.println("===ProcessBisDataVOId===" + ProcessBisDataVOId);
        System.out.println("refreshAttachAdapter=====" + attachFileList.size());
    }


    @Subscribe
    public void alertForCloseDialog(EventBusModel event) {
        if (event.isComplete()) {
            dialog.dismiss();
            Toast.makeText(SearchAdvertActivity.this, "ارسال شد", Toast.LENGTH_LONG).show();
            new GetAd().execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private class GetAd extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(SearchAdvertActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    SearchAdvertActivity.GetAd.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                System.out.println("result======" + result);
                if (result != null) {
                    jsonObj = new JSONObject(result);
                    updateData();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                System.out.println("doInBackground====" + AppController.getInstance().getSharedPreferences().getString("propertyCode", ""));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                if (propertyCode != null) {
                    jsonObject.put("propertyCode", propertyCode);
                } else {
                    propertyCode = AppController.getInstance().getSharedPreferences().getString("propertyCode", "");
                    jsonObject.put("propertyCode", propertyCode);
                }

                //jsonObject.put("carInfoType", carInfoType);
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, SearchAdvertActivity.this);
                try {
                    result = postJsonService.sendData("getCarAdvertisement", jsonObject, true);
                } catch (SocketTimeoutException | SocketException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (WebServiceException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                }

            } catch (JSONException e) {
                Log.d("RegistrationFragment", e.getMessage());
            }
            return null;
        }
    }


    private class GetAdvertisementTimeSeriesVO extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(SearchAdvertActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetAdvertisementTimeSeriesVO.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                System.out.println("result======" + result);
                if (result != null) {
                    jsonObj = new JSONObject(result);
                    updateData();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                //jsonObject.put("carInfoType", carInfoType);
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, SearchAdvertActivity.this);
                try {
                    result = postJsonService.sendData("getAdvertisementTimeSeriesVO", jsonObject, true);
                } catch (SocketTimeoutException | SocketException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (WebServiceException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                }

            } catch (JSONException e) {
                Log.d("RegistrationFragment", e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (globalDomain.isOnRestart()) {
            new GetAd().execute();
            globalDomain.setOnRestart(false);
        }

    }

}