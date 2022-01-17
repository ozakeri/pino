package com.gap.bis_inspection.activity.advert;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gap.bis_inspection.BuildConfig;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.activity.report.ReportNoneEntityActivity;
import com.gap.bis_inspection.adapter.advert.AdvertGetAttachAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.GlobalDomain;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.CameraPreview;
import com.gap.bis_inspection.util.Config;
import com.gap.bis_inspection.util.CustomProgressDialog;
import com.gap.bis_inspection.util.CustomTextView;
import com.gap.bis_inspection.util.EventBusModel;
import com.gap.bis_inspection.util.ImageManager;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.util.RecyclerTouchListener;
import com.gap.bis_inspection.util.volly.ResponseBean;
import com.gap.bis_inspection.util.volly.VollyService;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.gap.bis_inspection.widget.GPSTracker;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class EditAdvertActivity extends AppCompatActivity {

    private CustomTextView txt_processName, txt_userCreation, txt_dateCreation;
    private TextView edt_description, txt_title, txt_attachCount, txt_car, txt_advertName, txt_requestDate, txt_description;
    private Button btn_edit, btn_confirm, btn_nonConfirm;
    private RelativeLayout closeIcon;
    private RecyclerView recyclerViewEditAttach;
    private String nameFamily, ProcessBisDataVOId = null, conf2Permission = null, permissionName = null, permissionId = null,
            advertisementDetailId = null, description = null, processBisSettingId = null, processBisSettingVO = null,
            advertisementId = null, processBisSettingName = null, car = null, advertName = null, requestDate = null, startDate = null;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private Bitmap bitmap = null;
    private List<String> attachFileIdList;
    private ImageView img_add;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final int REQUEST_CAMERA = 1;
    private Uri mCapturedImageURI;
    private String path;
    private AttachFile attachFile;
    private Services services;
    private ProgressDialog progressDialogSentData;
    private int processStatus, actionProcessStatus;
    private List<Boolean> sysParamCheck = new ArrayList<>();
    private List<String> sysParamListId;
    private List<String> sysParamListName;
    private String sysParamId, systemParameterStr, attachFileSettingId = null, attachFileSettingName = null;
    private ArrayList<Bitmap> bitmapArray;
    private JSONArray attachFileJsonArrayJsonObject;
    private JSONArray jsonArrayAttachJsonObject;
    private GlobalDomain globalDomain = GlobalDomain.getInstance();
    private boolean allowed = false, confirm = false, nonConfirm = false, conf2Req = false, isEdit = false, haveAttachment = false,
            processStatusIsValidForEdit = false;
    private AppController application;
    private List<String> attachFileSettingListId;
    private List<String> attachFileSettingListName;
    private List<String> attachFileSettingListNameCopy;
    private List<String> attachFileSettingMinRecord;
    private List<String> attachFileSettingMaxrecord;
    private List<Integer> attachFileSettingForceIsEn;
    private MaterialSpinner spinner;
    private int minRecord = 0, maxRecord = 0, maxRecordCopy = 0, forceIsEn = 0, loopCount, max = 0, min = 0;
    private boolean isMaxCorrect = true;
    private Dialog dialog;
    private LinearLayout layout_car, layout_advert, layout_requestDate, layout_description;

    private static final int REQUEST_CODE_CHANGE_SETTING = 1;
    private static final int TIME_STAMP_UPDATE_INTERVAL = 1000;
    private Camera camera;
    private CameraPreview preview;
    private TextView txtDate;
    private SimpleDateFormat sdf;
    private Handler handler = new Handler();
    private Runnable runnableSetDateText;
    private FrameLayout frameLayout;
    private ScrollView scrollView;

    // bunch of location related apis

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude, longitude;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    GPSTracker locationTrack;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_advert);

        services = new Services(getApplicationContext());
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        txt_processName = findViewById(R.id.txt_processName);
        txt_userCreation = findViewById(R.id.txt_userCreation);
        txt_dateCreation = findViewById(R.id.txt_dateCreation);
        edt_description = findViewById(R.id.edt_description);
        txt_car = findViewById(R.id.txt_car);
        txt_advertName = findViewById(R.id.txt_advertName);
        txt_requestDate = findViewById(R.id.txt_requestDate);
        txt_description = findViewById(R.id.txt_description);
        txt_title = findViewById(R.id.txt_title);
        txt_attachCount = findViewById(R.id.txt_attachCount);
        btn_edit = findViewById(R.id.btn_edit);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_nonConfirm = findViewById(R.id.btn_nonConfirm);
        closeIcon = findViewById(R.id.closeIcon);
        img_add = findViewById(R.id.img_add);
        spinner = findViewById(R.id.spinner);
        layout_car = findViewById(R.id.layout_car);
        layout_advert = findViewById(R.id.layout_advert);
        layout_description = findViewById(R.id.layout_description);
        layout_requestDate = findViewById(R.id.layout_requestDate);
        recyclerViewEditAttach = findViewById(R.id.recyclerViewEditAttach);
        frameLayout = findViewById(R.id.frameLayout);
        scrollView = findViewById(R.id.scrollView);
        recyclerViewEditAttach.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        application = (AppController) getApplication();

        txtDate = (TextView) findViewById(R.id.txtDate);
        sdf = new SimpleDateFormat(Config.TIME_STAMP_FORMAT, Locale.getDefault());

        if (application.getCurrentUser().getName() != null && application.getCurrentUser().getFamily() != null) {
            nameFamily = application.getCurrentUser().getName() + " " + application.getCurrentUser().getFamily();
            txt_userCreation.setText(nameFamily);
        }

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        locationTrack = new GPSTracker(EditAdvertActivity.this);

        if (locationTrack.canGetLocation()) {


            longitude = String.valueOf(locationTrack.getLongitude());
            latitude = String.valueOf(locationTrack.getLatitude());

        } else {

            locationTrack.showSettingsAlert();
        }

        handler.postDelayed(runnableSetDateText, TIME_STAMP_UPDATE_INTERVAL);

        if (getIntent().getExtras() != null) {
            isEdit = getIntent().getExtras().getBoolean("isEdit");
            ProcessBisDataVOId = getIntent().getExtras().getString("id");
            description = getIntent().getExtras().getString("description");
            processBisSettingVO = getIntent().getExtras().getString("processBisSettingVO");
            processBisSettingId = getIntent().getExtras().getString("processBisSettingId");
            haveAttachment = getIntent().getExtras().getBoolean("haveAttachment");
            advertisementId = getIntent().getExtras().getString("advertisementId");
            permissionId = getIntent().getExtras().getString("permissionId");
            permissionName = getIntent().getExtras().getString("permissionName");
            conf2Permission = getIntent().getExtras().getString("conf2Permission");
            processBisSettingName = getIntent().getExtras().getString("processBisSettingName");
            processStatus = getIntent().getExtras().getInt("processStatus");
            actionProcessStatus = getIntent().getExtras().getInt("actionProcessStatus");
            advertisementDetailId = getIntent().getExtras().getString("advertisementDetailId");
            conf2Req = getIntent().getExtras().getBoolean("conf2Req");
            processStatusIsValidForEdit = getIntent().getExtras().getBoolean("processStatusIsValidForEdit");

            car = getIntent().getExtras().getString("car");
            advertName = getIntent().getExtras().getString("advertName");
            requestDate = getIntent().getExtras().getString("requestDate");
            startDate = getIntent().getExtras().getString("startDate");

            img_add.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);

            if (description != null) {
                edt_description.setText(description);
                txt_description.setText(description);
            }


            if (haveAttachment) {
                img_add.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
            } else {
                img_add.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
            }

            System.out.println("processStatus====" + processStatus);

            if (isEdit) {
                if (processStatus == 1) {
                    btn_confirm.setVisibility(View.GONE);
                } else {
                    btn_confirm.setVisibility(View.VISIBLE);
                }

                btn_edit.setText("ویرایش");
                //btn_edit.setVisibility(View.GONE);
                txt_title.setText("ویرایش");
                txt_processName.setText(processBisSettingName);
                edt_description.setText(description);
                btn_nonConfirm.setVisibility(View.GONE);

                if (!processStatusIsValidForEdit) {
                    btn_edit.setVisibility(View.GONE);
                    btn_confirm.setVisibility(View.GONE);
                    btn_nonConfirm.setVisibility(View.GONE);
                    img_add.setVisibility(View.GONE);
                    edt_description.setEnabled(false);
                    txt_title.setText("جزییات");
                    txt_car.setText(car);
                    txt_advertName.setText(advertName);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    Date requestDate1 = null;
                    try {
                        requestDate1 = simpleDateFormat.parse(requestDate);
                        txt_requestDate.setText(CommonUtil.latinNumberToPersian(HejriUtil.chrisToHejriDateTime(requestDate1)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    layout_car.setVisibility(View.VISIBLE);
                    layout_advert.setVisibility(View.VISIBLE);
                    layout_requestDate.setVisibility(View.VISIBLE);
                    layout_description.setVisibility(View.VISIBLE);
                    edt_description.setVisibility(View.GONE);


                    System.out.println("====startDate=====" + startDate);

                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    Date dateCreation = null;
                    try {
                        dateCreation = simpleDateFormat1.parse(startDate);
                        txt_dateCreation.setText(CommonUtil.latinNumberToPersian(HejriUtil.chrisToHejriDateTime(dateCreation)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Date date = new Date();
                    txt_dateCreation.setText(CommonUtil.latinNumberToPersian(HejriUtil.chrisToHejri(date)));

                    layout_car.setVisibility(View.GONE);
                    layout_advert.setVisibility(View.GONE);
                    layout_requestDate.setVisibility(View.GONE);
                    layout_description.setVisibility(View.GONE);
                    edt_description.setVisibility(View.VISIBLE);
                }

            } else {
                btn_confirm.setVisibility(View.GONE);
                btn_nonConfirm.setVisibility(View.GONE);
                description = edt_description.getText().toString();
                txt_title.setText("مدیریت اجرای تبلیغات");
                txt_processName.setText(processBisSettingName);
                btn_edit.setText("ذخیره");
                img_add.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                layout_car.setVisibility(View.GONE);
                layout_advert.setVisibility(View.GONE);
                layout_requestDate.setVisibility(View.GONE);
                layout_description.setVisibility(View.GONE);
                Date date = new Date();
                txt_dateCreation.setText(CommonUtil.latinNumberToPersian(HejriUtil.chrisToHejri(date)));
            }

            new GetAttachFileSettingItem().execute();

            spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {


                    if (attachFileSettingId.equals("73") || attachFileSettingId.equals("75") || attachFileSettingId.equals("74")) {
                        minRecord = 4;
                    } else {
                        minRecord = Integer.parseInt(attachFileSettingMinRecord.get(position));
                    }

                    maxRecord = Integer.parseInt(attachFileSettingMaxrecord.get(position));
                    maxRecordCopy = Integer.parseInt(attachFileSettingMaxrecord.get(position));

                    attachFileSettingId = attachFileSettingListId.get(position);
                    attachFileSettingName = attachFileSettingListName.get(position);
                    if (attachFileSettingId != null) {
                        new GetAttachFileList().execute();
                    }

                }
            });
        }

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                System.out.println("isEdit====" + isEdit);
                if (isEdit) {
                    editAdvert(ProcessBisDataVOId, edt_description.getText().toString());
                } else {
                    actionSave(processBisSettingId, edt_description.getText().toString());
                }


            }
        });

      /*  if (application.getPermissionMap().containsKey(permissionName)) {
            btn_confirm.setVisibility(View.VISIBLE);
            btn_edit.setVisibility(View.VISIBLE);
        } else {
            btn_confirm.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
        }*/

        if (processStatus == 1) {
            btn_confirm.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
        }


        System.out.println("processStatusIsValidForEdit====" + processStatusIsValidForEdit);
        System.out.println("processStatus====" + processStatus);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // confirmAdvert();
                confirm = true;
                nonConfirm = false;
                isMaxCorrect = true;
                new GetParamAd().execute();
            }
        });


        System.out.println("conf2Req=====" + conf2Req);
        if (conf2Req) {
            btn_nonConfirm.setVisibility(View.VISIBLE);
        } else if (processStatus != 1) {
            btn_nonConfirm.setVisibility(View.GONE);
        }

        btn_nonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // confirmAdvert();
                confirm = false;
                nonConfirm = true;
                new GetParamAd().execute();
            }
        });

        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (attachFileIdList.size() == maxRecord) {
                    Toast toast = Toast.makeText(EditAdvertActivity.this, "تعداد ثبت پیوست محدود است", Toast.LENGTH_SHORT);
                    CommonUtil.showToast(toast, EditAdvertActivity.this);
                    toast.show();
                } else {
                    //showAttachDialog();


                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                            .checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale
                                (EditAdvertActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (EditAdvertActivity.this, Manifest.permission.CAMERA)) {
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

                        txtDate.setText(" " + sdf.format( new Date()) + "\n" + " " +  "X: " + latitude + "\n" + " " + "Y: " + longitude + "\n" + " ");
                        startPreview(null);
                      /*  if (getLocation()){
                            startPreview(null);
                        }else {
                            getLocation();
                        }

*/
                    }

                }

            }
        });

        recyclerViewEditAttach.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                JSONObject attachFileJsonArrayObject = null;
                try {
                    attachFileJsonArrayObject = jsonArrayAttachJsonObject.getJSONObject(position);
                    attachFileJsonArrayJsonObject = attachFileJsonArrayObject.getJSONArray("attachFileJsonArray");
                    Bundle b = new Bundle();
                    b.putString("attachFileJsonArrayJsonObject", attachFileJsonArrayJsonObject.toString());
                    b.putString("result", "EditAdvertActivity");
                    Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));

        recyclerViewEditAttach.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewEditAttach, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                PopupMenu popup = new PopupMenu(EditAdvertActivity.this, view);
                popup.inflate(R.menu.attachment_menu);


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.delete) {
                            String attachFileId = attachFileIdList.get(position);
                            deleteAttachFile(attachFileId);
                            refreshAttachAdapter();
                        }
                        return false;
                    }
                });

                popup.show();
            }
        }));

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void refreshAttachAdapter() {
        new GetAttachFileList().execute();
    }

    private class GetAttachFileList extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            if (progressDialogSentData != null) {
                progressDialogSentData.dismiss();
            }

            progressDialog = new ProgressDialog(EditAdvertActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetAttachFileList.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            if (result != null) {

                try {
                    JSONObject jsonObjRes = new JSONObject(result);

                    if (errorMsg == null && !jsonObjRes.isNull(Constants.SUCCESS_KEY)) {
                        if (!jsonObjRes.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObj = jsonObjRes.getJSONObject(Constants.RESULT_KEY);
                            progressDialog.dismiss();
                            if (!jsonObj.isNull("jsonArrayAttach")) {
                                jsonArrayAttachJsonObject = jsonObj.getJSONArray("jsonArrayAttach");
                                attachFileIdList = new ArrayList<>();
                                bitmapArray = new ArrayList<Bitmap>();
                                System.out.println("jsonArrayAttachJsonObject====" + jsonArrayAttachJsonObject.length());

                                for (int i = 0; i < jsonArrayAttachJsonObject.length(); i++) {
                                    JSONObject attachFileJsonArrayObject = jsonArrayAttachJsonObject.getJSONObject(i);
                                    attachFileJsonArrayJsonObject = attachFileJsonArrayObject.getJSONArray("attachFileJsonArray");
                                    byte[] bytes = new byte[0];
                                    bytes = new byte[attachFileJsonArrayJsonObject.length()];
                                    for (int j = 0; j < attachFileJsonArrayJsonObject.length(); j++) {
                                        bytes[j] = Integer.valueOf(attachFileJsonArrayJsonObject.getInt(j)).byteValue();
                                    }
                                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                    bitmapArray.add(bitmap);
                                    attachFileIdList.add(attachFileJsonArrayObject.getString("attachFileId"));
                                }

                                recyclerViewEditAttach.setAdapter(new AdvertGetAttachAdapter(bitmapArray,attachFileSettingId,attachFileSettingName));
                                txt_attachCount.setText(" تعداد پیوست :  " + CommonUtil.latinNumberToPersian(String.valueOf(bitmapArray.size())));
                            }
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("GetAttachFileList======" + result);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                jsonObject.put("id", ProcessBisDataVOId);
                jsonObject.put("attachFileSettingId", attachFileSettingId);

                System.out.println("ProcessBisDataVOId===" + ProcessBisDataVOId);
                System.out.println("attachFileSettingId===" + attachFileSettingId);

                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                try {
                    result = postJsonService.sendData("getAdvertAttachFileList", jsonObject, true);
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

    public void getMaxSize(int i) {

        @SuppressLint("StaticFieldLeak")
        class getMaxSizeAttachFileList extends AsyncTask<Void, Void, Boolean> {
            private String result;
            private String errorMsg;
            private ProgressDialog progressDialog = null;
            private int id;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
                if (progressDialogSentData != null) {
                    progressDialogSentData.dismiss();
                }

                progressDialog = new ProgressDialog(EditAdvertActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        getMaxSizeAttachFileList.this.cancel(true);
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();

                if (result != null) {
                    System.out.println("getMaxSizeAttachFileList======" + result);
                    try {
                        JSONObject jsonObjRes = new JSONObject(result);
                        if (errorMsg == null && !jsonObjRes.isNull(Constants.SUCCESS_KEY)) {
                            if (!jsonObjRes.isNull(Constants.RESULT_KEY)) {
                                JSONObject jsonObj = jsonObjRes.getJSONObject(Constants.RESULT_KEY);
                                if (!jsonObj.isNull("max")) {
                                    max += jsonObj.getInt("max");
                                    //maxRecord = Integer.parseInt(attachFileSettingMaxrecord.get(i));
                                    loopCount++;
                                    System.out.println("=======max=======" + max);
                                    System.out.println("attachFileSettingMaxrecord=======" + Integer.parseInt(attachFileSettingMaxrecord.get(i)));
                                    System.out.println("minRecord=======" + minRecord);
                                    System.out.println("loopCount=======" + loopCount);
                                    System.out.println("size=======" + attachFileSettingListName.size());

                                    if (max < minRecord) {
                                       // if (Integer.parseInt(attachFileSettingMinRecord.get(i)) != jsonObj.getInt("max")) {
                                            if (loopCount == attachFileSettingListName.size()) {
                                               // Toast toast = Toast.makeText(EditAdvertActivity.this, "افزودن پیوست " + " " + attachFileSettingListName.get(i) + " ضروری است ", Toast.LENGTH_LONG);
                                                Toast toast = Toast.makeText(EditAdvertActivity.this, " افزودن حداقل " + minRecord + " پیوست الزامیست ", Toast.LENGTH_LONG);
                                                CommonUtil.showToast(toast, EditAdvertActivity.this);
                                                toast.show();
                                            }
                                        //}
                                    } else {
                                        EventBus.getDefault().post(new EventBusModel(1));
                                    }

                                    /*System.out.println("isMaxCorrect===11====" + isMaxCorrect);
                                    System.out.println("maxRecord====" + maxRecord);
                                    System.out.println("max====" + max);
                                    if (maxRecord != jsonObj.getInt("max")) {
                                        isMaxCorrect = false;
                                        System.out.println("isMaxCorrect===33====" + isMaxCorrect);
                                        Toast toast = Toast.makeText(EditAdvertActivity.this, "افزودن پیوست " + " " + attachFileSettingListName.get(i) + " ضروری است ", Toast.LENGTH_LONG);
                                        CommonUtil.showToast(toast);
                                        toast.show();
                                        loopCount = 0;
                                        return;
                                    }

                                    if (maxRecord == jsonObj.getInt("max") && loopCount == attachFileSettingListName.size()) {
                                        if (isMaxCorrect) {
                                            System.out.println("confirmAdvert=====" + new DateTime());
                                            confirmAdvert(ProcessBisDataVOId, edt_description.getText().toString(), sysParamId, systemParameterStr, dialog);
                                        }
                                    }else {
                                        Toast toast = Toast.makeText(EditAdvertActivity.this, "افزودن پیوست " + " " + attachFileSettingListName.get(i) + " ضروری است ", Toast.LENGTH_LONG);
                                        CommonUtil.showToast(toast);
                                        toast.show();
                                        System.out.println("NoconfirmAdvert=====" + new DateTime());
                                    }

                                    System.out.println("isMaxCorrect===22====" + isMaxCorrect);
                                    System.out.println("loopCount======" + loopCount);
                                    System.out.println("size======" + attachFileSettingListName.size());*/
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put("id", ProcessBisDataVOId);
                    jsonObject.put("attachFileSettingId", attachFileSettingListId.get(i));

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                    try {
                        result = postJsonService.sendData("getMaxSizeAttachFileList", jsonObject, true);
                        if (result != null) {
                            System.out.println("result======" + result);
                        }
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
        new getMaxSizeAttachFileList().execute();
    }

    public void editAdvert(String id, String description) {
        class EditAdvert extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;
            private ProgressDialog progressDialog = null;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
                progressDialog = new ProgressDialog(EditAdvertActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        EditAdvert.this.cancel(true);
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
                                Toast toast = Toast.makeText(EditAdvertActivity.this, "درخواست انجام شد", Toast.LENGTH_LONG);
                                CommonUtil.showToast(toast, EditAdvertActivity.this);
                                toast.show();
                                globalDomain.setOnRestart(true);
                                // finish();
                            }
                        }
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
                    jsonObject.put("id", id);
                    jsonObject.put("advertDescription", description);
                    jsonObject.put("localIp", getLocalIpAddress());

                    //jsonObject.put("carInfoType", carInfoType);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                    try {
                        result = postJsonService.sendData("editAdvert", jsonObject, true);
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


            ////******getServerDateTime*******////
        }
        new EditAdvert().execute();
    }

    public void confirmAdvert(String id, String description, String sysId, String systemParameterStr, Dialog dialog) {
        System.out.println("id========" + id);
        System.out.println("description========" + description);
        System.out.println("sysId========" + sysId);
        System.out.println("actionProcessStatus========" + actionProcessStatus);

        class ConfirmAdvert extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;
            private ProgressDialog progressDialog = null;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
                progressDialog = new ProgressDialog(EditAdvertActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ConfirmAdvert.this.cancel(true);
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
                                dialog.dismiss();
                                globalDomain.setOnRestart(true);
                                finish();
                            }
                        }
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
                    jsonObject.put("id", id);
                    jsonObject.put("advertDescription", description);
                    jsonObject.put("sysParamId", sysId);
                    jsonObject.put("confirm", confirm);
                    jsonObject.put("advertId", advertisementId);
                    jsonObject.put("systemParameterStr", systemParameterStr);
                    jsonObject.put("localIp", getLocalIpAddress());

                    System.out.println("id=====" + id);
                    System.out.println("advertDescription=====" + description);
                    System.out.println("sysParamId=====" + sysId);
                    System.out.println("confirm=====" + confirm);
                    System.out.println("advertId=====" + advertisementId);
                    System.out.println("systemParameterStr=====" + systemParameterStr);

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                    try {
                        result = postJsonService.sendData("confirmProcessBisDataVOForAdvert", jsonObject, true);
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


            ////******getServerDateTime*******////
        }
        new ConfirmAdvert().execute();
    }

    public void deleteAttachFile(String attachFileId) {
        class DeleteAttachFile extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;
            private ProgressDialog progressDialog = null;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
                progressDialog = new ProgressDialog(EditAdvertActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        DeleteAttachFile.this.cancel(true);
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
                    new GetAttachFileList().execute();

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
                    jsonObject.put("id", attachFileId);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                    try {
                        result = postJsonService.sendData("deleteAttachFileAdvert", jsonObject, true);
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


            ////******getServerDateTime*******////
        }
        new DeleteAttachFile().execute();
    }

    private void showAttachDialog() {
        final Dialog dialog = new Dialog(EditAdvertActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_attach_advert);
        RelativeLayout camera = dialog.findViewById(R.id.layout_camera);
        TextView txt_title = dialog.findViewById(R.id.txt_title);

        RelativeLayout closeIcon = dialog.findViewById(R.id.closeIcon);
        dialog.show();


        System.out.println("attachFileSettingId=====" + attachFileSettingId);
        System.out.println("systemParameterStr==========" + attachFileSettingName);
        System.out.println("maxRecord=====" + maxRecord);

        if (attachFileSettingId.equals("73") || attachFileSettingId.equals("75") || attachFileSettingId.equals("74")) {
            switch (attachFileIdList.size()) {
                case 0:
                    txt_title.setText("تصویر جلوی خودرو");
                    break;

                case 1:
                    txt_title.setText("تصویر بدنه سمت راست خودرو");
                    break;

                case 2:
                    txt_title.setText("تصویر عقب خودرو");
                    break;

                case 3:
                    txt_title.setText("تصویر بدنه سمت چپ خودرو");
                    break;

                default:
                    txt_title.setText(attachFileSettingName);

            }
        }else {
            txt_title.setText(attachFileSettingName);
        }



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (EditAdvertActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale
                                    (EditAdvertActivity.this, Manifest.permission.CAMERA)) {
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

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    /*getPathCamera */
    private String getPathCamera() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index_data);
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            path = null;
            path = getPathCamera();
            saveAttachImageFile(getRightAngleImage(path));
            System.out.println("saveAttachImageFile====");
            refreshAttachAdapter();
        }
    }*/

    public void saveAttachImageFile(String filePath) {
        try {

            File file = new File(String.valueOf(filePath));
            file = saveBitmapToFile(file);
            attachFile = new AttachFile();
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
            if (attachFileSettingId.equals("73") || attachFileSettingId.equals("75") || attachFileSettingId.equals("74")) {
                switch (attachFileIdList.size()) {
                    case 0:
                        attachFile.setAttachFileUserFileName(" (جلوی خودرو) " + userFileName);
                        break;

                    case 1:
                        attachFile.setAttachFileUserFileName(" (سمت راست خودرو) " + userFileName);
                        break;

                    case 2:
                        attachFile.setAttachFileUserFileName(" (عقب خودرو) " + userFileName);
                        break;

                    case 3:
                        attachFile.setAttachFileUserFileName(" (سمت چپ خودرو) " + userFileName);
                        break;

                    default:
                        attachFile.setAttachFileUserFileName(userFileName);
                }
            }else {
                attachFile.setAttachFileUserFileName(userFileName);
            }


            //attachFile.setAttachFileUserFileName(userFileName);
            attachFile.setSendingStatusDate(new Date());
            attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
            attachFile.setEntityNameEn(EntityNameEn.ProcessBisData.ordinal());
            attachFile.setServerAttachFileSettingId(Long.valueOf(attachFileSettingId));
            attachFile.setEntityId(Long.valueOf(ProcessBisDataVOId));
            attachFile.setServerEntityId(Long.valueOf(ProcessBisDataVOId));
            attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
            coreService.insertAttachFile(attachFile);

            System.out.println("attachFile.getId====" + attachFile.getId());

            String newFilePath = path + "/" + attachFile.getId() + filePostfix;


            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(newFilePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //try to decrease decoded image
            options.inPurgeable = true; //purgeable to disk
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); //compressed bitmap to file
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

            services.resumeAttachFileList(attachFileSettingId);

            progressDialogSentData = new ProgressDialog(EditAdvertActivity.this);
            progressDialogSentData.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialogSentData.setIndeterminate(true);
            progressDialogSentData.setCancelable(true);
            progressDialogSentData.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GetParamAd extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(EditAdvertActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetParamAd.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                JSONObject jsonObjRes = new JSONObject(result);

                if (errorMsg == null && !jsonObjRes.isNull(Constants.SUCCESS_KEY)) {
                    if (!jsonObjRes.isNull(Constants.RESULT_KEY)) {
                        JSONObject jsonObj = jsonObjRes.getJSONObject(Constants.RESULT_KEY);
                        if (!jsonObj.isNull("systemParameterVO")) {
                            JSONArray systemParameterVOJsonObject = jsonObj.getJSONArray("systemParameterVO");
                            System.out.println("length======" + systemParameterVOJsonObject.length());
                            sysParamListName = new ArrayList<>();
                            sysParamListId = new ArrayList<>();

                            for (int i = 0; i < systemParameterVOJsonObject.length(); i++) {
                                JSONObject systemParameterVOObject = systemParameterVOJsonObject.getJSONObject(i);

                                if (!systemParameterVOObject.isNull("allowed")) {
                                    allowed = systemParameterVOObject.getBoolean("allowed");

                                    if (confirm) {

                                        if (allowed) {
                                            if (!systemParameterVOObject.isNull("id")) {
                                                sysParamListId.add(systemParameterVOObject.getString("id"));
                                            }

                                            if (!systemParameterVOObject.isNull("nameFv")) {
                                                sysParamListName.add(systemParameterVOObject.getString("nameFv"));
                                            }
                                        }
                                    }

                                    if (nonConfirm) {

                                        if (!allowed) {
                                            if (!systemParameterVOObject.isNull("id")) {
                                                sysParamListId.add(systemParameterVOObject.getString("id"));
                                            }

                                            if (!systemParameterVOObject.isNull("nameFv")) {
                                                sysParamListName.add(systemParameterVOObject.getString("nameFv"));
                                            }
                                        }
                                    }
                                }
                            }
                            showConfirmDialog();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                System.out.println("processBisSettingId====" + processBisSettingId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                jsonObject.put("id", processBisSettingId);
                //jsonObject.put("carInfoType", carInfoType);
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                try {
                    result = postJsonService.sendData("getParamAdvertisement", jsonObject, true);
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


    private class GetAttachFileSettingItem extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(EditAdvertActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetAttachFileSettingItem.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                JSONObject jsonObjRes = new JSONObject(result);
                System.out.println("GetAttachFileSettingItem====" + result);
                if (errorMsg == null && !jsonObjRes.isNull(Constants.SUCCESS_KEY)) {
                    if (!jsonObjRes.isNull(Constants.RESULT_KEY)) {
                        JSONObject jsonObj = jsonObjRes.getJSONObject(Constants.RESULT_KEY);
                        if (!jsonObj.isNull("attachFileSettingListJsonArray")) {
                            JSONArray attachFileSettingListJsonArray = jsonObj.getJSONArray("attachFileSettingListJsonArray");
                            attachFileSettingListId = new ArrayList<>();
                            attachFileSettingListName = new ArrayList<>();
                            attachFileSettingListNameCopy = new ArrayList<>();
                            attachFileSettingMinRecord = new ArrayList<>();
                            attachFileSettingMaxrecord = new ArrayList<>();
                            attachFileSettingForceIsEn = new ArrayList<>();
                            for (int i = 0; i < attachFileSettingListJsonArray.length(); i++) {
                                JSONObject attachFileSettingJSONObject = attachFileSettingListJsonArray.getJSONObject(i);

                                if (!attachFileSettingJSONObject.isNull("id")) {
                                    attachFileSettingListId.add(attachFileSettingJSONObject.getString("id"));
                                }
                                if (!attachFileSettingJSONObject.isNull("attachName") && !attachFileSettingJSONObject.isNull("minRecord")) {

                                    attachFileSettingId = attachFileSettingListId.get(i);

                                    attachFileSettingListNameCopy.add(attachFileSettingJSONObject.getString("attachName"));
                                    if (!processStatusIsValidForEdit) {
                                        attachFileSettingListName.add(attachFileSettingJSONObject.getString("attachName"));
                                    } else {
                                        if (attachFileSettingId.equals("73") || attachFileSettingId.equals("75") || attachFileSettingId.equals("74")) {
                                            attachFileSettingListName.add(attachFileSettingJSONObject.getString("attachName") + " - " + " حداقل 4 پیوست الزامیست ");
                                        } else {
                                            attachFileSettingListName.add(attachFileSettingJSONObject.getString("attachName") + " - " + " حداقل " + attachFileSettingJSONObject.getString("minRecord") + " پیوست الزامیست ");
                                        }
                                    }

                                    spinner.setItems(attachFileSettingListName);

                                }

                                if (!attachFileSettingJSONObject.isNull("maxRecord")) {
                                    attachFileSettingMaxrecord.add(attachFileSettingJSONObject.getString("maxRecord"));
                                }

                                if (!attachFileSettingJSONObject.isNull("minRecord")) {
                                    attachFileSettingMinRecord.add(attachFileSettingJSONObject.getString("minRecord"));
                                }

                                if (!attachFileSettingJSONObject.isNull("forceIsEn")) {
                                    attachFileSettingForceIsEn.add(attachFileSettingJSONObject.getInt("forceIsEn"));
                                }

                                new GetAttachFileList().execute();
                                attachFileSettingId = attachFileSettingListId.get(0);
                                attachFileSettingName = attachFileSettingListNameCopy.get(0);


                                if (attachFileSettingId.equals("73") || attachFileSettingId.equals("75") || attachFileSettingId.equals("74")) {
                                    minRecord = 4;
                                } else {
                                    minRecord = Integer.parseInt(attachFileSettingMinRecord.get(0));
                                }

                                maxRecord = Integer.parseInt(attachFileSettingMaxrecord.get(0));

                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                System.out.println("processBisSettingId====" + processBisSettingId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                jsonObject.put("ProcessBisSettingId", processBisSettingId);
                //jsonObject.put("id", ProcessBisDataVOId);
                //jsonObject.put("carInfoType", carInfoType);
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                try {
                    result = postJsonService.sendData("getAttachFileSettingItemList", jsonObject, true);
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

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            return file;
        } catch (Exception e) {
            return null;
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
                progressDialog = new ProgressDialog(EditAdvertActivity.this);
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
                    System.out.println("=====result=====" + result);
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!jsonObject.isNull("processBisDataVO")) {
                            JSONObject processBisDataVO = jsonObject.getJSONObject("processBisDataVO");
                            if (!processBisDataVO.isNull("id")) {
                                ProcessBisDataVOId = processBisDataVO.getString("id");
                                System.out.println("SaveAd=====" + processBisDataVO.getString("id"));
                                img_add.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.VISIBLE);
                                btn_edit.setText("ویرایش");
                                globalDomain.setOnRestart(true);
                                isEdit = true;
                                //showDialogAttach();
                                new GetAttachFileList().execute();

                                getProcessBisData(processBisDataVO.getString("id"));
                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(EditAdvertActivity.this, "خطا در عملیات", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, EditAdvertActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    System.out.println("advertisementId=====" + advertisementId);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put("id", advertisementId);
                    jsonObject.put("ProcessBisSettingId", id);
                    jsonObject.put("advertDescription", description);
                    jsonObject.put("localIp", getLocalIpAddress());
                    //jsonObject.put("carInfoType", carInfoType);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
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
                return null;
            }
        }


        new SaveAd().execute();
    }

    public void getProcessBisData(String id) {
        class GetProcessBisData extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;
            private ProgressDialog progressDialog = null;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //progressBar.setVisibility(View.VISIBLE);
                progressDialog = new ProgressDialog(EditAdvertActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();

                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        GetProcessBisData.this.cancel(true);
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
                    System.out.println("=====result=====" + result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("permissionName")) {
                                permissionName = jsonObject.getString("permissionName");
                                System.out.println("=====permissionName=====" + permissionName);
                            }

                            if (!jsonObject.isNull("processBisData")) {
                                JSONObject processBisDataJSONObject = jsonObject.getJSONObject("processBisData");
                                processStatus = processBisDataJSONObject.getInt("processStatus");
                                System.out.println("=====processStatus=====" + processStatus);
                            }
                            if (!jsonObject.isNull("processBisSetting")) {
                                JSONObject processBisSettingVOJSONObject = jsonObject.getJSONObject("processBisSetting");
                                if (!processBisSettingVOJSONObject.isNull("conf2Req")) {
                                    conf2Req = processBisSettingVOJSONObject.getBoolean("conf2Req");
                                }

                                if (!processBisSettingVOJSONObject.isNull("haveAttachment")) {
                                    haveAttachment = processBisSettingVOJSONObject.getBoolean("haveAttachment");
                                    System.out.println("=====haveAttachment=====" + haveAttachment);
                                }

                                if (!processBisSettingVOJSONObject.isNull("id")) {
                                    processBisSettingId = processBisSettingVOJSONObject.getString("id");
                                }
                            }

                            if (conf2Req) {
                                btn_nonConfirm.setVisibility(View.VISIBLE);
                            } else {
                                btn_nonConfirm.setVisibility(View.GONE);
                            }

                            if (processStatus == 1) {
                                btn_confirm.setVisibility(View.GONE);
                            } else {
                                btn_confirm.setVisibility(View.VISIBLE);
                            }

                        }
                    } else {
                        Toast toast = Toast.makeText(EditAdvertActivity.this, "خطا در عملیات", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, EditAdvertActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    System.out.println("=====id=====" + id);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put("id", id);

                    //jsonObject.put("carInfoType", carInfoType);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, EditAdvertActivity.this);
                    try {
                        result = postJsonService.sendData("getProcessBisDataByParam", jsonObject, true);
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

        new GetProcessBisData().execute();
    }

    private void showConfirmDialog() {
        dialog = new Dialog(EditAdvertActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.show();

        Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
        EditText edt_description = dialog.findViewById(R.id.edt_description);
        TextView txt_processName = dialog.findViewById(R.id.txt_processName);
        RelativeLayout closeIcon = dialog.findViewById(R.id.closeIcon);
        MaterialSpinner spinner = dialog.findViewById(R.id.spinner);
        txt_processName.setText(processBisSettingName);

        System.out.println("sysParamListName====" + sysParamListName.size());
        if (sysParamListName != null) {
            if (sysParamListName.size() > 0) {
                spinner.setItems(sysParamListName);
                sysParamId = sysParamListId.get(0);
            }
        }

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sysParamId = sysParamListId.get(position);
                systemParameterStr = sysParamListName.get(position);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                max = 0;
                // minRecord = 0;
                maxRecordCopy = 0;
                loopCount = 0;
                EventBus.getDefault().post(new EventBusModel(0));
/*
                if (haveAttachment) {
                    if (attachFileIdList.size() == 0) {
                        Toast toast = Toast.makeText(EditAdvertActivity.this, "ثبت پیوست ضروری است.", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast);
                        toast.show();
                        return;
                    }
                }*/

                for (int i = 0; i < attachFileSettingListName.size(); i++) {
                    System.out.println("attachFileSettingListName.size()===" + attachFileSettingListName.size());


                    String id = attachFileSettingListId.get(i);
                    System.out.println("id=====" + id);
                    if (id.equals("73") || id.equals("75") || id.equals("74")) {
                        minRecord=4;
                        System.out.println("minRecord11111=====" + minRecord);
                    }else {
                        minRecord += Integer.parseInt(attachFileSettingMinRecord.get(i));
                        System.out.println("minRecord22222=====" + minRecord);
                    }

                    //maxRecord = Integer.parseInt(attachFileSettingMaxrecord.get(i));
                    forceIsEn = attachFileSettingForceIsEn.get(i);
                    maxRecordCopy += Integer.parseInt(attachFileSettingMaxrecord.get(i));
                    if (forceIsEn == 1) {
                        getMaxSize(i);
                    }
                }
            }
        });

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Subscribe
    public void alertForCloseDialog(EventBusModel event) {
        if (event.isComplete()) {
            System.out.println("========isComplete=========");
            Toast.makeText(EditAdvertActivity.this, "ارسال شد", Toast.LENGTH_LONG).show();
            if (progressDialogSentData != null) {
                progressDialogSentData.dismiss();
            }
            //new GetAttachFileList().execute();
            refreshAttachAdapter();
        }

        if (event.getIsMaxCorrect() == 1) {

            System.out.println("========confirmAdvert=========");
            confirmAdvert(ProcessBisDataVOId, edt_description.getText().toString(), sysParamId, systemParameterStr, dialog);
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

    public void getMaxSizeAttachFileList(int count) {
        VollyService.getInstance().getMaxSizeAttachFileList(application, ProcessBisDataVOId, attachFileSettingListId.get(count), new Response.Listener<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                int max = response.rESULT.max;
                maxRecord = Integer.parseInt(attachFileSettingMaxrecord.get(count));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("onErrorResponse===" + volleyError);
            }
        });
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void startPreview(String pictureSizeStr) {
        frameLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        if (camera != null) {
            camera.release();
            camera = null;
        }

        camera = getCameraInstance();

        if (camera == null) {
            Log.e("jisunLog", "Failed camera open");
        } else {

            FrameLayout layoutPreview = (FrameLayout) findViewById(R.id.layoutPreview);
            if (preview != null) {
                layoutPreview.removeView(preview);
                preview = null;
            }

            preview = new CameraPreview(this, camera);
            preview.setKeepScreenOn(true);

            // 저장 사진과 preview의 사이즈 등을 설정
            ImageManager.adjustCameraParameters(this, camera, pictureSizeStr);

            // preview가 보여지는 화면의 비율 설정
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) layoutPreview.getLayoutParams();
            p.height = displayMetrics.widthPixels / 3 * 4;
            layoutPreview.setLayoutParams(p);

            // preview를 layout에 추가하고, 날짜 영역을 화면 상위로 올림
            layoutPreview.addView(preview);
            findViewById(R.id.txtDate).bringToFront();
        }
    }

    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    public Camera getCameraInstance() {
        Camera camera = null;

        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                // Camera.CameraInfo.CAMERA_FACING_FRONT or Camera.CameraInfo.CAMERA_FACING_BACK
                int cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                camera = Camera.open(cameraFacing);
                // camera orientation
                camera.setDisplayOrientation(setCameraDisplayOrientation(this, cameraFacing, camera));
                // get Camera parameters
                Camera.Parameters params = camera.getParameters();
                // picture image orientation
                params.setRotation(setCameraDisplayOrientation(this, cameraFacing, camera));
                camera.startPreview();

            } catch (RuntimeException ex) {
                Toast.makeText(this, "camera_not_found ] " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                Log.d(Config.TAG, "camera_not_found ] " + ex.getMessage().toString());
            }
        }

        return camera;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHANGE_SETTING:
                if (resultCode == RESULT_OK) {


                    // TODO jisun : 사진 저장 사이즈 변경하기
                    String str = data.getStringExtra(Config.PREF_KEY_PICTURE_SIZE);

                    ((FrameLayout) findViewById(R.id.layoutPreview)).removeView(preview);
                    preview = null;

                    startPreview(str);
                }
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickCapture(View v) {
        camera.takePicture(null, null, new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                new AsyncTask<byte[], Void, File>() {
                    public float textSize;
                    private CustomProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new CustomProgressDialog(EditAdvertActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        textSize = txtDate.getTextSize();
                        textSize = txtDate.getTextSize();
                    }

                    @Override
                    protected File doInBackground(byte[]... params) {
                        byte[] data = params[0];
                        Bitmap bitmap = ImageManager.saveImageWithTimeStamp(EditAdvertActivity.this, data, 0, data.length, textSize,txtDate.getText().toString());
                        File file = ImageManager.saveFile(bitmap);
                        refreshGallery(file);
                        return file;
                    }

                    @Override
                    protected void onPostExecute(File file) {
                        progressDialog.dismiss();

                        if (file == null) {
                            Log.e(Config.TAG, "Error creating media file, check storage permissions");
                        } else {
                           /* ImageDialog dialog = new ImageDialog(EditAdvertActivity.this, file);
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    EditAdvertActivity.this.camera.startPreview();
                                }
                            });
                            dialog.show();*/
                            saveAttachImageFile(file.getPath());
                            frameLayout.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            progressDialogSentData.dismiss();
                        }

                        super.onPostExecute(file);
                    }
                }.execute(data);
            }
        });
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditAdvertActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopUsingGPS();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @SuppressLint("SetTextI18n")
    private boolean getLocation() {

        txtDate.setText(sdf.format(new Date()) + "\n" + "X: " + "35." +
                "760640" + "\n" + "Y: " + "51.421878");

        if (ActivityCompat.checkSelfPermission(
                EditAdvertActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                EditAdvertActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return false;
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                System.out.println("Your Location: " + "\n" + "X: " + latitude + "\n" + "Y: " + longitude);
                return true;
            } else {
                Toast.makeText(this, "موقعیت مکانی یافت نشد !!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
    }
}