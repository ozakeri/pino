package com.gap.bis_inspection.activity.form;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.adapter.form.AttachFileImageList;
import com.gap.bis_inspection.adapter.form.SurveyAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.enumtype.SurveyFormStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroup;
import com.gap.bis_inspection.db.objectmodel.SurveyForm;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestion;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestionTemp;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.AppLocationService;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.util.RecyclerTouchListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class SurveyActivity extends AppCompatActivity {
    private Context context = this;
    private AppController application;
    private RecyclerView surveyListView, attachFileRecyclerView;
    private CoreService coreService;
    private SurveyForm surveyForm;
    private Double latitude;
    private Double longitude;
    private RelativeLayout layoutBottom;
    private LinearLayout layoutSendDate;
    private LinearLayout linearLayout;
    private Button completeButton, recordButton;
    private TextView formNameTV;
    private TextView formDateTV;
    private TextView creditDateTV;
    private TextView sendDateTV;
    private RelativeLayout backIcon;
    private List<SurveyFormQuestion> surveyFormQuestionList;
    private List<SurveyFormQuestionTemp> formTempList;
    private ImageView attach_Icon;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    Uri mCapturedImageURI;
    List<AttachFile> attachFileList;
    private AttachFile attachFile;
    private String path;
    private Services services;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);

        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        services = new Services(getApplicationContext());
        services.sendSurveyFormList();
        init();


        ////******get gps*******////
        AppLocationService appLocationService = new AppLocationService(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Location gpsLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
            } else {
                showSettingsAlert("NETWORK");
            }
        }

        application = (AppController) getApplication();
        Bundle bundle = getIntent().getExtras();
        long surveyFormId = bundle.getLong("surveyFormId");
        System.out.println("surveyFormId=" + surveyFormId);
        surveyForm = coreService.getSurveyFormById(surveyFormId);
        SurveyFormQuestion tmpSurveyFormQuestionFS = new SurveyFormQuestion();
        tmpSurveyFormQuestionFS.setSurveyFormId(surveyFormId);
        surveyFormQuestionList = coreService.getSurveyFormQuestionListByParam(tmpSurveyFormQuestionFS);
        List<FormQuestionGroup> formQuestionGroupList = coreService.getFormQuestionGroupListById(surveyFormId);
        formTempList = coreService.getSurveyFormQuestionTempListById(surveyFormId);

        //==== setting recycler view
        surveyListView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        surveyListView.setLayoutManager(mLayoutManager);

        //==== setting recyclerAttachFile view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attachFileRecyclerView.setHasFixedSize(true);
        attachFileRecyclerView.setLayoutManager(layoutManager);

        SurveyAdapter surveyAdapter = new SurveyAdapter(getApplicationContext(), surveyFormQuestionList, formQuestionGroupList, formTempList, linearLayout, surveyForm);
        surveyListView.setAdapter(surveyAdapter);


        if (surveyForm != null) {
            formNameTV.setText(surveyForm.getName());
            System.out.println("surveyForm====" + surveyForm.getId());
            attachFileList = coreService.getAttachFileListById(surveyForm.getId());
            System.out.println("surveyForm====" + attachFileList.size());

            Date date = surveyForm.getStartDate();
            formDateTV.setText(CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd"));

            date = surveyForm.getEndDate();
            creditDateTV.setText(CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd"));

            //date = surveyForm.getSendingStatusDate();
            //completeDateTV.setText(CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd"));

            date = surveyForm.getSendingStatusDate();
            sendDateTV.setText(CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd"));

            refreshAttachAdapter();
        }


        if (surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
            layoutBottom.setVisibility(View.GONE);
            layoutSendDate.setVisibility(View.VISIBLE);
            // layoutCompleteDate.setVisibility(View.VISIBLE);
        }

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                    saveSurveyFormQuestion();
                    surveyForm.setStatusEn(SurveyFormStatusEn.Incomplete.ordinal());
                    surveyForm.setStatusDate(new Date());
                    surveyForm.setXLatitude(latitude != null ? latitude.toString() : null);
                    surveyForm.setYLongitude(longitude != null ? longitude.toString() : null);
                    coreService.updateSurveyForm(surveyForm);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_record_form, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,SurveyActivity.this);
                    toast.show();
                }
            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                    saveSurveyFormQuestion();

                    final Dialog dialog = new Dialog(SurveyActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_chek_list_sent_layout);
                    Button actionYes = (Button) dialog.findViewById(R.id.action_YES);
                    Button actionNo = (Button) dialog.findViewById(R.id.action_NO);
                    RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                    dialog.show();

                    actionYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            SurveyFormQuestion tmpSurveyFormQuestionFS = new SurveyFormQuestion();
                            tmpSurveyFormQuestionFS.setSurveyFormId(surveyForm.getId());
                            surveyFormQuestionList = coreService.getSurveyFormQuestionListByParam(tmpSurveyFormQuestionFS);
                            surveyForm = coreService.getSurveyFormById(surveyForm.getId());
                            surveyForm.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
                            surveyForm.setSendingStatusDate(new Date());
                            surveyForm.setStatusEn(SurveyFormStatusEn.Complete.ordinal());
                            surveyForm.setStatusDate(new Date());
                            surveyForm.setXLatitude(latitude != null ? latitude.toString() : null);
                            surveyForm.setYLongitude(longitude != null ? longitude.toString() : null);
                            coreService.updateSurveyForm(surveyForm);
                            new Thread(new SaveSurveyFormTask()).start();
                            // TODO: go to surveyListActivity
                            Intent intent = new Intent(getApplicationContext(), SurveyListActivity.class);
                            startActivity(intent);
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.label_complete_form, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,SurveyActivity.this);
                            toast.show();
                            dialog.dismiss();
                        }
                    });

                    actionNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });


        ////******Attach File*******////
        attach_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attachFileRecyclerView.getAdapter().getItemCount() == 4) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_fullAttach_checkList, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,SurveyActivity.this);
                    toast.show();
                } else {

                    final Dialog dialog = new Dialog(SurveyActivity.this);
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
                                        (SurveyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                        ActivityCompat.shouldShowRequestPermissionRationale
                                                (SurveyActivity.this, Manifest.permission.CAMERA)) {
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
            }
        });


        attachFileRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AttachFile attachFile = (AttachFile) attachFileList.get(position);
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                if (attachFile != null) {
                    String result = "surveyForm";
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
                surveyForm = coreService.getSurveyFormById(surveyForm.getId());
                if (surveyForm != null) {
                    if (!surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                        PopupMenu popup = new PopupMenu(SurveyActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                /*if (id == R.id.delete) {
                                    attachFileList = coreService.getAttachFileListById(surveyForm.getId());
                                    AttachFile attachFile = (AttachFile) attachFileList.get(position);
                                    coreService.deleteAttachFile(attachFile);
                                    refreshAttachAdapter();
                                }*/
                                return false;
                            }
                        });
                    }
                }
            }
        }));

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
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


    //========= galleryIntent for attachment
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), REQUEST_GALLERY);
    }

    /*getPathCamera */
    private String getPathCamera() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index_data);
    }

    //========= result for attachment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            path = null;
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

    public void refreshAttachAdapter() {
        attachFileList = coreService.getAttachFileListById(surveyForm.getId());
        AttachFileImageList attachFileImageList = new AttachFileImageList(context, attachFileList);
        attachFileRecyclerView.setAdapter(attachFileImageList);
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

    /*
       saveAttachFile */

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
            attachFile.setEntityNameEn(EntityNameEn.SurveyFormAnswerInfo.ordinal());
            attachFile.setServerAttachFileSettingId((long) 1015);
            if (surveyForm != null) {
                attachFile.setEntityId(surveyForm.getId());
            }
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

    private void init() {
        surveyListView = (RecyclerView) findViewById(R.id.surveyListView_LS);
        recordButton = (Button) findViewById(R.id.record_Button);
        completeButton = (Button) findViewById(R.id.complete_Button);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        layoutBottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        layoutSendDate = (LinearLayout) findViewById(R.id.layout_sendDate);
        LinearLayout layoutCompleteDate = (LinearLayout) findViewById(R.id.layout_completeDate);
        formNameTV = (TextView) findViewById(R.id.formName_TV);
        formDateTV = (TextView) findViewById(R.id.formDate_TV);
        TextView completeDateTV = (TextView) findViewById(R.id.completeDate_TV);
        creditDateTV = (TextView) findViewById(R.id.creditDate_TV);
        sendDateTV = (TextView) findViewById(R.id.sendDate_TV);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        attach_Icon = (ImageView) findViewById(R.id.attach_Icon);
        attachFileRecyclerView = (RecyclerView) findViewById(R.id.attachRecyclerView);
    }

    private void saveSurveyFormQuestion() {
        formTempList = coreService.getSurveyFormQuestionTempListById(surveyForm.getId());
        System.out.println("formTempList.size()===" + formTempList.size());
        int count = formTempList.size();
        for (int i = 0; i < count; i++) {
            SurveyFormQuestionTemp surveyFormQuestionTemp = (SurveyFormQuestionTemp) formTempList.get(i);
            SurveyFormQuestion surveyFormQuestion = (SurveyFormQuestion) surveyFormQuestionList.get(i);
            if (surveyFormQuestionTemp != null) {
                surveyFormQuestion.setSurveyFormId(surveyForm.getId());
                surveyFormQuestion.setQuestion(surveyFormQuestionTemp.getQuestion());
                //surveyFormQuestion.setFormQuestionId(formTemp.getId());
                surveyFormQuestion.setAnswerInt(surveyFormQuestionTemp.getAnswerInt());
                surveyFormQuestion.setAnswerStr(surveyFormQuestionTemp.getAnswerStr());
                System.out.println("formTemp.getAnswerInt()===" + surveyFormQuestionTemp.getAnswerInt());
                System.out.println("formItemAnswer.getAnswerInt===" + surveyFormQuestion.getAnswerInt());
            }
            coreService.updateSurveyFormQuestion(surveyFormQuestion);
        }
    }

    private class SaveSurveyFormTask implements Runnable {
        @Override
        public void run() {
            // BackgroundService backgroundService = new BackgroundService();
            services.sendSurveyForm(coreService, surveyForm);
            // backgroundService.sendSurveyForm(context, coreService, surveyForm, application.getCurrentUser());
        }
    }

    ////******gps alert*******////
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        SurveyActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
