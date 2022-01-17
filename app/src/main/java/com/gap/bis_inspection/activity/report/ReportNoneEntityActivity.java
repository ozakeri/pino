package com.gap.bis_inspection.activity.report;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.BuildConfig;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.adapter.form.AttachFileImageList;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.FarsiLetter;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.InputFilterMinMax;
import com.gap.bis_inspection.util.PlateUtils;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.util.RecyclerTouchListener;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.gap.bis_inspection.widget.VpnCheck;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportNoneEntityActivity extends AppCompatActivity {
    RadioGroup selectOptionReport, selectOptionPlateText;
    RadioButton selectOptionReportCar, selectOptionDriverPlateText;
    LinearLayout layoutEnterCode, layoutSelectedPlateText, layoutPlateTextIR, layoutPlateTextTemporary;
    EditText plateTextP1ET, plateTextP2ET, plateTextP3ET, plateTextP4ET, codeET, plateTextET, reportCodeET, reportStrET;
    TextView codeTV;
    Spinner plateTextSp;
    Button sendReportButton;
    AppController application;
    ComplaintReport complaintReport;
    Double latitude;
    Double longitude;
    CoreService coreService;
    IDatabaseManager databaseManager;
    Context context = this;
    Map<String, FarsiLetter> letterMap;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private Integer entityNameEn;
    //protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private String TAG;
    private Uri mCapturedImageURI;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private String path;
    private RecyclerView attachFileRecyclerView;
    private AttachFile attachFile;
    private ProgressDialog progressBar;
    private ASyncGetDriverInfo myTaskGetDriver = null;
    private ASyncGetLineInfo myTaskGetLine = null;
    private ASyncGetCarInfo myTaskGetCar = null;
    private int plate;
    private VpnCheck vpnCheck = new VpnCheck();
    private String carInfoType;
    private ImageView attach_Icon;
    private List<AttachFile> attachFileList;
    private String identifier;

    //***********************************location***********
    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_report_none_entity);
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        initLocation();
        init();
        startLocationButtonClick();
        setChecked();

        entityNameEn = EntityNameEn.Car.ordinal();

        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            carInfoType = String.valueOf(1);
            //txt_title.setText("کد خودرو را وارد کنید : ");
        } else {
            carInfoType = String.valueOf(0);
            //txt_title.setText("پلاک خودرو را وارد کنید : ");
        }

        application = (AppController) getApplication();
        complaintReport = new ComplaintReport();
        complaintReport.setId(Long.valueOf(new Date().getTime() + application.getCurrentUser().getServerUserId().toString()));
        coreService.insertComplaintReport(complaintReport);
        ////******select Option Report base radio button*******////

        selectOptionReport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                ////******report_type_driver*******////
                if (i == R.id.selected_report_type_driver) {

                    codeTV.setVisibility(View.VISIBLE);
                    codeTV.setText(R.string.label_report_enterCode_driver);
                    layoutEnterCode.setVisibility(View.VISIBLE);
                    layoutSelectedPlateText.setVisibility(View.GONE);
                    codeET.setText("");
                    codeET.requestFocus();

                    if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
                        codeET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99999)});
                    } else {
                        codeET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99999)});
                    }
                    entityNameEn = EntityNameEn.DriverProfile.ordinal();

                    ////******report_type_line*******////
                } else if (i == R.id.selected_report_type_line) {
                    codeTV.setVisibility(View.VISIBLE);
                    codeTV.setText(R.string.label_report_enterCode_line);
                    layoutEnterCode.setVisibility(View.VISIBLE);
                    layoutSelectedPlateText.setVisibility(View.GONE);
                    codeET.setText("");
                    codeET.requestFocus();
                    codeET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
                    entityNameEn = EntityNameEn.Line.ordinal();

                    ////******report_type_others*******////
                } else if (i == R.id.selected_report_type_others) {
                    codeTV.setVisibility(View.GONE);
                    layoutEnterCode.setVisibility(View.GONE);
                    layoutSelectedPlateText.setVisibility(View.GONE);
                    entityNameEn = EntityNameEn.Document.ordinal();

                    ////******report_type_car*******////
                } else if (i == R.id.selected_report_type_car) {

                    if (carInfoType.equals("1")) {
                        carInfoType = String.valueOf(1);
                        codeTV.setVisibility(View.VISIBLE);
                        codeTV.setText(R.string.label_report_enterCode_car);
                        layoutEnterCode.setVisibility(View.VISIBLE);
                        layoutSelectedPlateText.setVisibility(View.GONE);
                        codeET.setText("");
                        codeET.requestFocus();
                        entityNameEn = EntityNameEn.Car.ordinal();

                    } else if (carInfoType.equals("0")) {
                        carInfoType = String.valueOf(0);
                        layoutEnterCode.setVisibility(View.GONE);
                        layoutSelectedPlateText.setVisibility(View.VISIBLE);
                        plateTextP1ET.requestFocus();
                        entityNameEn = EntityNameEn.Car.ordinal();
                        plateTextP1ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
                        plateTextP2ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
                        plateTextP3ET.setEnabled(false);
                        plateTextP4ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
                        plateTextET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99999)});

                    }
                }
            }
        });

        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            carInfoType = String.valueOf(1);
            codeTV.setVisibility(View.VISIBLE);
            codeTV.setText(R.string.label_report_enterCode_car);
            layoutEnterCode.setVisibility(View.VISIBLE);
            layoutSelectedPlateText.setVisibility(View.GONE);
            codeET.setText("");
            codeET.requestFocus();
            entityNameEn = EntityNameEn.Car.ordinal();

        } else {
            carInfoType = String.valueOf(0);
            layoutEnterCode.setVisibility(View.GONE);
            layoutSelectedPlateText.setVisibility(View.VISIBLE);
            plateTextP1ET.requestFocus();
            entityNameEn = EntityNameEn.Car.ordinal();
            plateTextP1ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
            plateTextP2ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
            plateTextP3ET.setEnabled(false);
            plateTextP4ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
            plateTextET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99999)});
        }

        selectOptionPlateText.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                ////******report_type_plateTextIR*******////

                if (i == R.id.selected_report_type_plateTextIR) {
                    layoutPlateTextIR.setVisibility(View.VISIBLE);
                    layoutPlateTextTemporary.setVisibility(View.GONE);
                    plateTextP1ET.requestFocus();

                    ////******report_type_plateTextTemporary*******////

                } else if (i == R.id.selected_report_type_plateTextTemporary) {
                    layoutPlateTextTemporary.setVisibility(View.VISIBLE);
                    layoutPlateTextIR.setVisibility(View.GONE);
                    plateTextET.requestFocus();
                }
            }
        });


        ////******change alphabet plate text to number*******////

        letterMap = new HashMap<String, FarsiLetter>();
        letterMap.put(getResources().getString(R.string.fa_char_ein), FarsiLetter.TwoOne);
        letterMap.put(getResources().getString(R.string.fa_char_dal), FarsiLetter.OneZero);
        letterMap.put(getResources().getString(R.string.fa_char_sin), FarsiLetter.OneFive);
        letterMap.put(getResources().getString(R.string.fa_char_ba), FarsiLetter.Two);
        letterMap.put(getResources().getString(R.string.fa_char_ghaf), FarsiLetter.TwoFour);
        letterMap.put(getResources().getString(R.string.fa_char_ya), FarsiLetter.ThreeTwo);

        ArrayList<String> letterArrayList = new ArrayList<String>();
        letterArrayList.add(getResources().getString(R.string.fa_char_ein));
        letterArrayList.add(getResources().getString(R.string.fa_char_dal));
        letterArrayList.add(getResources().getString(R.string.fa_char_sin));
        letterArrayList.add(getResources().getString(R.string.fa_char_ba));
        letterArrayList.add(getResources().getString(R.string.fa_char_ghaf));
        letterArrayList.add(getResources().getString(R.string.fa_char_ya));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.text_spinner, letterArrayList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        plateTextSp.setAdapter(spinnerArrayAdapter);

        reportCodeET = CommonUtil.farsiNumberReplacement(reportCodeET);


        ////****** send Report*******////
        //new InsertDataTask().execute();
       /* new FetchCordinates().execute();
        if (!startService()) {
            CreateAlert("Error!", "Service Cannot be started");
        } else {
            Toast.makeText(ReportNoneEntityActivity.this, "Service Started",
                    Toast.LENGTH_LONG).show();
        }*/


        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (isOnline()) {
                //complaintReport = new ComplaintReport();
                long id = complaintReport.getId();
                System.out.println("id=====" + id);
                ComplaintReport complaintReport = coreService.getComplaintReportById(id);
                complaintReport.setEntityNameEn(entityNameEn);
                if (selectOptionReport.getCheckedRadioButtonId() == R.id.selected_report_type_line) {
                    complaintReport.setEntityNameEn(EntityNameEn.Line.ordinal());
                } else if (selectOptionReport.getCheckedRadioButtonId() == R.id.selected_report_type_driver) {
                    complaintReport.setEntityNameEn(EntityNameEn.DriverProfile.ordinal());
                } else if (selectOptionReport.getCheckedRadioButtonId() == R.id.selected_report_type_car) {
                    complaintReport.setEntityNameEn(EntityNameEn.Car.ordinal());
                } else {
                    complaintReport.setEntityNameEn(EntityNameEn.Document.ordinal());
                }

                ////****** check is null edit text*******////
                if (complaintReport.getEntityNameEn() != null && complaintReport.getEntityNameEn().equals(EntityNameEn.Car.ordinal())) {

                    if (carInfoType.equals("1")) {

                        String strIsEmptyLineCode = codeET.getText().toString();
                        if (TextUtils.isEmpty(strIsEmptyLineCode) || TextUtils.getTrimmedLength(strIsEmptyLineCode) < 3) {
                            codeET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                        } else {
                            identifier = codeET.getText().toString();
                            if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                                Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                            } else {
                                myTaskGetCar = new ASyncGetCarInfo();
                                myTaskGetCar.execute();
                            }


                        }

                    } else if (carInfoType.equals("0")) {
                        if (selectOptionPlateText.getCheckedRadioButtonId() == R.id.selected_report_type_plateTextTemporary) {

                            String strIsEmptyPlateText = plateTextET.getText().toString();
                            if (TextUtils.isEmpty(strIsEmptyPlateText) || TextUtils.getTrimmedLength(strIsEmptyPlateText) < 5) {
                                plateTextET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                            } else {
                                Integer plateP1 = Integer.valueOf(plateTextET.getText().toString().substring(0, 2));
                                Integer plateP2 = Integer.valueOf(plateTextET.getText().toString().substring(2, 5));
                                plate = Integer.parseInt(plateP1 + "" + plateP2);
                                identifier = PlateUtils.encode(2, plateP1, null, plateP2, null, null);

                                if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                                    Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                                } else {
                                    myTaskGetCar = new ASyncGetCarInfo();
                                    myTaskGetCar.execute();
                                }

                            }

                        } else {
                            String strIsEmptyPlateTextP1 = plateTextP1ET.getText().toString();
                            String strIsEmptyPlateTextP2 = plateTextP2ET.getText().toString();
                            String strIsEmptyPlateTextP4 = plateTextP4ET.getText().toString();
                            if (TextUtils.isEmpty(strIsEmptyPlateTextP1) || TextUtils.getTrimmedLength(strIsEmptyPlateTextP1) < 2) {
                                plateTextP1ET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                            } else if (TextUtils.isEmpty(strIsEmptyPlateTextP2) || TextUtils.getTrimmedLength(strIsEmptyPlateTextP2) < 3) {
                                plateTextP2ET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                            } else if (TextUtils.isEmpty(strIsEmptyPlateTextP4) || TextUtils.getTrimmedLength(strIsEmptyPlateTextP4) < 2) {
                                plateTextP4ET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                            } else {
                                Integer plateP1 = Integer.valueOf(plateTextP1ET.getText().toString());
                                Integer plateP2 = Integer.valueOf(plateTextP2ET.getText().toString());
                                Integer plateP4 = Integer.valueOf(plateTextP4ET.getText().toString());
                                String selectedLetter = (String) plateTextSp.getSelectedItem();
                                identifier = PlateUtils.encode(1, plateP1, letterMap.get(selectedLetter), plateP2, (long) 9999, plateP4);
                                plate = Integer.parseInt(plateP1 + "" + plateP2 + "" + plateP4);

                                if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                                    Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                                } else {
                                    myTaskGetCar = new ASyncGetCarInfo();
                                    myTaskGetCar.execute();
                                }

                            }
                        }
                    }
                } else if (complaintReport.getEntityNameEn() != null && complaintReport.getEntityNameEn().equals(EntityNameEn.DriverProfile.ordinal())) {
                    String strIsEmptyDriverCode = codeET.getText().toString();
                    if (TextUtils.isEmpty(strIsEmptyDriverCode)) {
                        codeET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                    } else {
                        identifier = codeET.getText().toString();
                        if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                            Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                        } else {
                            myTaskGetDriver = new ASyncGetDriverInfo();
                            myTaskGetDriver.execute();
                        }

                    }

                } else if (complaintReport.getEntityNameEn() != null && complaintReport.getEntityNameEn().equals(EntityNameEn.Line.ordinal())) {
                    String strIsEmptyLineCode = codeET.getText().toString();
                    if (TextUtils.isEmpty(strIsEmptyLineCode) || TextUtils.getTrimmedLength(strIsEmptyLineCode) < 3) {
                        codeET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                    } else {
                        identifier = codeET.getText().toString();
                        if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                            Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                        } else {
                            myTaskGetLine = new ASyncGetLineInfo();
                            myTaskGetLine.execute();
                        }


                    }
                } else {
                    complaintReport.setDisplayName(getResources().getString(R.string.label_report_opOthers));
                    //complaintReport.setIdentifier(String.valueOf(1));
                    identifier = String.valueOf(1);
                    sendingReport();

                }
            } /*else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_check_network, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast);
                    toast.show();
                }*/
        });

        plateTextP1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (plateTextP1ET.length() == 2) {
                    plateTextSp.setFocusable(true);
                    plateTextSp.requestFocus();
                    plateTextSp.performClick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        plateTextSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (plateTextP1ET.length() == 2) {
                    plateTextP2ET.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        plateTextP2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (plateTextP2ET.length() == 3) {
                    plateTextP4ET.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        plateTextP4ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (plateTextP4ET.length() == 2) {
                    reportCodeET.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        reportStrET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reportStrET.setGravity(Gravity.NO_GRAVITY);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        attach_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ReportNoneEntityActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_attach_checklist);
                TextView camera = (TextView) dialog.findViewById(R.id.camera_VT);
                TextView gallery = (TextView) dialog.findViewById(R.id.gallery_VT);
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
                                    (ReportNoneEntityActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale
                                            (ReportNoneEntityActivity.this, Manifest.permission.CAMERA)) {
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

                gallery.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View view) {
                        galleryIntent();
                        dialog.dismiss();
                    }
                });

                closeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        RelativeLayout close_icon = (RelativeLayout) findViewById(R.id.backIcon);
        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attachFileRecyclerView.setHasFixedSize(true);
        attachFileRecyclerView.setLayoutManager(layoutManager);

        //getGps();

        attachFileRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
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

        attachFileRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), attachFileRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                complaintReport = coreService.getComplaintReportById(complaintReport.getId());
                PopupMenu popup = new PopupMenu(ReportNoneEntityActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                    /*    if (id == R.id.delete) {
                            attachFileList = coreService.getAttachFileListById(complaintReport.getId());
                            AttachFile attachFile = (AttachFile) attachFileList.get(position);
                            coreService.deleteAttachFile(attachFile);
                            refreshAttachAdapter();
                        }*/
                        return false;
                    }
                });
            }
        }));

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


    //========= galleryIntent for attachment
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), REQUEST_GALLERY);
    }

    //========= result for attachment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri outputFileUri;
            if (requestCode == REQUEST_CAMERA) {
                path = getPathCamera();
                saveAttachImageFile(path);
                refreshAttachAdapter();

            } else if (requestCode == REQUEST_GALLERY) {
                outputFileUri = data.getData();
                path = getRealPathFromURI(outputFileUri);
                saveAttachImageFile(path);
                refreshAttachAdapter();
            }
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

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] pro = {MediaStore.MediaColumns.DATA};
        try {
            Cursor cursor = getContentResolver().query(contentUri, pro, null, null, null);
            assert cursor != null;
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(column_index);
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("Err" + e.getMessage());
        }
        return path;
    }


    public void saveAttachImageFile(String filePath) {
        File file = new File(String.valueOf(filePath));
        if (file.exists() && Long.valueOf(file.length()).compareTo((long) 1e+7) <= 0) {
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
            attachFile.setAttachFileUserFileName(userFileName);
            attachFile.setSendingStatusDate(new Date());
            attachFile.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            attachFile.setEntityNameEn(EntityNameEn.ComplaintReport.ordinal());
            attachFile.setServerAttachFileSettingId((long) 101);
            attachFile.setEntityId(complaintReport.getId());
            coreService.insertAttachFile(attachFile);

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
    }

    private void setChecked() {
        selectOptionReportCar.setChecked(true);
        plateTextP1ET.requestFocus();
        entityNameEn = EntityNameEn.Car.ordinal();
        plateTextP1ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        plateTextP2ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
        plateTextP3ET.setEnabled(false);
        plateTextP4ET.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});

        layoutEnterCode.setVisibility(View.GONE);
        layoutSelectedPlateText.setVisibility(View.VISIBLE);
        plateTextP1ET.requestFocus();

        selectOptionDriverPlateText.setChecked(true);
        layoutPlateTextIR.setVisibility(View.VISIBLE);
        layoutPlateTextTemporary.setVisibility(View.GONE);
        plateTextP1ET.requestFocus();
    }

    private void init() {
        selectOptionReport = (RadioGroup) findViewById(R.id.selected_report_type);
        selectOptionPlateText = (RadioGroup) findViewById(R.id.report_type_plateText);
        layoutEnterCode = (LinearLayout) findViewById(R.id.layout_enterCode);
        layoutSelectedPlateText = (LinearLayout) findViewById(R.id.layout_selectedPlateText);
        layoutPlateTextIR = (LinearLayout) findViewById(R.id.layout_plateTextIR);
        layoutPlateTextTemporary = (LinearLayout) findViewById(R.id.layout_plateTextTemporary);
        codeTV = (TextView) findViewById(R.id.code_TV);
        plateTextP1ET = (EditText) findViewById(R.id.plateTextP1_ET);
        plateTextP2ET = (EditText) findViewById(R.id.plateTextP2_ET);
        plateTextP3ET = (EditText) findViewById(R.id.plateTextP3_ET);
        plateTextP4ET = (EditText) findViewById(R.id.plateTextP4_ET);
        codeET = (EditText) findViewById(R.id.code_ET);
        plateTextET = (EditText) findViewById(R.id.plateText_ET);
        reportCodeET = (EditText) findViewById(R.id.reportCode_ET);
        reportStrET = (EditText) findViewById(R.id.reportStr_ET);
        plateTextSp = (Spinner) findViewById(R.id.plateText_SP);
        selectOptionReportCar = (RadioButton) findViewById(R.id.selected_report_type_car);
        selectOptionDriverPlateText = (RadioButton) findViewById(R.id.selected_report_type_plateTextIR);
        sendReportButton = (Button) findViewById(R.id.sendReport_btn);
        attach_Icon = (ImageView) findViewById(R.id.attach_Icon);
        attachFileRecyclerView = (RecyclerView) findViewById(R.id.attachRecyclerView);
        mSettingsClient = LocationServices.getSettingsClient(this);
    }


    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    //========= refreshAttachAdapter
    public void refreshAttachAdapter() {
        if (complaintReport != null) {
            System.out.println("complaintReport===" + complaintReport.getId());
            attachFileList = coreService.getAttachFileListById(complaintReport.getId());
            AttachFileImageList attachFileImageList = new AttachFileImageList(context, attachFileList);
            attachFileRecyclerView.setAdapter(attachFileImageList);
        }
    }

    //********************************

    private void sendingReport() {
        //getGpsLocation();
        String strIsEmptyReportStr = reportStrET.getText().toString();
        if (TextUtils.isEmpty(strIsEmptyReportStr)) {
            reportStrET.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
        } else {
            //complaintReport.setId(Long.valueOf(new Date().getTime() + application.getCurrentUser().getServerUserId().toString()));
            //complaintReport.setEntityId(entityId);
            //complaintReport.setEntityNameEn(entityNameEn);
            complaintReport.setReportCode(reportCodeET.getText().toString());
            complaintReport.setReportStr(reportStrET.getText().toString());
            complaintReport.setUserReportId(application.getCurrentUser().getServerUserId());
            complaintReport.setReportDate(new Date());
            complaintReport.setDeliverIs(Boolean.FALSE);
            complaintReport.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            complaintReport.setXLatitude(latitude != null ? latitude.toString() : null);
            complaintReport.setYLongitude(longitude != null ? longitude.toString() : null);
            complaintReport.setIdentifier(identifier);
            complaintReport.setEntityNameEn(entityNameEn);
            coreService.updateComplaintReport(complaintReport);
            new Thread(new SaveComplaintReportTask()).start();
            finish();
            Toast toast = Toast.makeText(getApplicationContext(), R.string.label_report_sendMsg, Toast.LENGTH_LONG);
            CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
            toast.show();
        }
    }

    private class SaveComplaintReportTask implements Runnable {
        @Override
        public void run() {
            Services services = new Services(getApplicationContext());
            services.sendComplaintReport(coreService, complaintReport);
        }
    }


    ////****** auth driver *******////

    private class ASyncGetDriverInfo extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = ProgressDialog.show(context, null, context.getResources().getString(R.string.label_progress_dialog, true), true);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
            if (result != null) {
                String displayName = null;
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);

                            if (!jsonObject.isNull("driverProfile")) {
                                String name = jsonObject.getString("driverProfile");
                                complaintReport.setDisplayName(name);
                            }
                            sendingReport();
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                toast.show();
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put("driverCode", codeET.getText().toString());
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getDriverNameByCode", jsonObject, true);
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

        private boolean isDeviceDateTimeValid() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            try {
                JSONObject jsonObjectParam = new JSONObject();
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
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
            } catch (JSONException | WebServiceException | ParseException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }


    ////****** auth line *******////

    private class ASyncGetLineInfo extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = ProgressDialog.show(context, null, context.getResources().getString(R.string.label_progress_dialog, true), true);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
            if (result != null) {
                String displayName;
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("line")) {
                                String name = jsonObject.getString("line");
                                complaintReport.setDisplayName(name);
                            }
                            sendingReport();
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                toast.show();
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put("lineCode", codeET.getText().toString());
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getLineNameByCode", jsonObject, true);
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

        private boolean isDeviceDateTimeValid() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            try {
                JSONObject jsonObjectParam = new JSONObject();
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
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
            } catch (JSONException | WebServiceException | ParseException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }


    ////****** auth car *******////

    private class ASyncGetCarInfo extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = ProgressDialog.show(context, null, context.getResources().getString(R.string.label_progress_dialog, true), true);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
            if (result != null) {
                String displayName;
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("car")) {
                                String plateText = jsonObject.getString("car");
                                complaintReport.setDisplayName(plateText);
                            }
                            sendingReport();
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,ReportNoneEntityActivity.this);
                toast.show();
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());


                    if (carInfoType.equals("0")) {
                        jsonObject.put("plateText", plate);
                    } else if (carInfoType.equals("1")) {
                        jsonObject.put("propertyCode", codeET.getText().toString());
                    }

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getCarNameByPlate", jsonObject, true);
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

        private boolean isDeviceDateTimeValid() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            try {
                JSONObject jsonObjectParam = new JSONObject();
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
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
            } catch (JSONException | WebServiceException | ParseException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }


    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ReportNoneEntityActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(ReportNoneEntityActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            System.out.println("Lat: " + mCurrentLocation.getLatitude() + ", " +
                    "Lng: " + mCurrentLocation.getLongitude());

            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

}
