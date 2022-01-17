package com.gap.bis_inspection.activity.checklist;

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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.form.AttachFileImageList;
import com.gap.bis_inspection.adapter.form.CheckListFormAnswerAdapter;
import com.gap.bis_inspection.adapter.form.CheckListFormQuestionAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.enumtype.SurveyFormStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.Form;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.db.objectmodel.FormItemAnswer;
import com.gap.bis_inspection.db.objectmodel.FormQuestion;
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroup;
import com.gap.bis_inspection.db.objectmodel.FormTemp;
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

public class CheckListFormQuestionActivity extends AppCompatActivity {

    TextView plateText, lineCode, dateAndTime, formName, txt_title;
    ImageView attach_Icon;
    RelativeLayout back_Icon;
    Double latitude;
    Double longitude;
    Form form;
    FormAnswer formAnswer;
    Button record, complete;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private AppLocationService appLocationService;
    CoreService coreService;
    boolean recognize;
    RecyclerView recyclerView, attachFileRecyclerView;
    List<FormItemAnswer> formItemAnswerList;
    List<AttachFile> attachFileList;
    List<FormQuestion> formQuestionList;
    List<FormTemp> formTempList;
    List<FormQuestionGroup> formQuestionGroupList;
    List<FormItemAnswer> formItemAnswersList;
    FormItemAnswer formItemAnswer;
    FormTemp formTemp;
    AppController application;
    Context context;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    Uri mCapturedImageURI;
    String path;
    RelativeLayout layout_bottom;
    AttachFile attachFile;
    long formAnswerId;
    FormQuestion tmpFormQuestionFs;
    FormQuestionGroup formQuestionGroup;
    boolean onPause = true;
    LinearLayout linearLayout;
    CheckListFormAnswerAdapter adapter1;
    CheckListFormQuestionAdapter adapter;
    LinearLayoutManager mLayoutManager;
    long formId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_question);

        Bundle bundle = getIntent().getExtras();
        recognize = bundle.getBoolean("recognize");
        formAnswerId = bundle.getLong("formAnswerId");

        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        appLocationService = new AppLocationService(CheckListFormQuestionActivity.this);
        application = (AppController) getApplication();

        plateText = (TextView) findViewById(R.id.plateText);
        lineCode = (TextView) findViewById(R.id.lineCode);
        dateAndTime = (TextView) findViewById(R.id.dateAndTime);
        formName = (TextView) findViewById(R.id.formName_TV);
        txt_title = (TextView) findViewById(R.id.txt_title);
        back_Icon = (RelativeLayout) findViewById(R.id.back_Icon);
        record = (Button) findViewById(R.id.record_Button);
        complete = (Button) findViewById(R.id.complete_Button);
        attach_Icon = (ImageView) findViewById(R.id.attach_Icon);
        layout_bottom = (RelativeLayout) findViewById(R.id.layout_bottom);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        attachFileRecyclerView = (RecyclerView) findViewById(R.id.attachRecyclerView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout4);

        final String platText_str = (String) bundle.get("searchKey");
        final String lineCode_str = (String) bundle.get("lineCode");
        final String dateAndTime_str = (String) bundle.get("dateAndTime");
        formId = bundle.getLong("formId");

        plateText.setText(platText_str);
        lineCode.setText(lineCode_str);
        dateAndTime.setText(dateAndTime_str);
        form = coreService.getCheckListFormById(formId);
        formAnswer = coreService.getFormAnswerById(formAnswerId);
        formName.setText(form.getName());

        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            txt_title.setText("کد خودرو : ");
        } else {
            txt_title.setText("گلاک خودرو : ");
        }

        if (!recognize) {
            if (formAnswer != null) {
                plateText.setText(String.valueOf(formAnswer.getCarId()));
                lineCode.setText(String.valueOf(formAnswer.getLineId()));
                dateAndTime.setText(String.valueOf(HejriUtil.chrisToHejriDateTime(formAnswer.getStatusDate())));
                formName.setText(formAnswer.getName());
            }
        }

        tmpFormQuestionFs = new FormQuestion();
        tmpFormQuestionFs.setFormId(formId);
        formQuestionGroup = new FormQuestionGroup();
        formQuestionGroup.setFormId(formId);

        FormItemAnswer tmpFormItemAnswerFs = new FormItemAnswer();

        //========= add permission for android M
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location gpsLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
            if (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
            } else {
                showSettingsAlert("NETWORK");
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            }
        }

        if (formAnswer != null) {

            saveFormAnswer();

            tmpFormItemAnswerFs.setFormAnswerId(formAnswer.getId());
            formItemAnswer = coreService.getFormItemAnswerById(formAnswer.getId());

            formItemAnswerList = coreService.getFormItemAnswerListById(formAnswer.getId());
            attachFileList = coreService.getAttachFileListById(formAnswer.getId());
        }

        formQuestionList = coreService.getFormQuestionListByParam(tmpFormQuestionFs);
        formQuestionGroupList = coreService.getFormQuestionGroupListById(formId);
        System.out.println("formQuestionGroupList.Size=" + formQuestionGroupList.size());


        formItemAnswersList = coreService.getFormItemAnswerListByParam(tmpFormItemAnswerFs);
        formTempList = coreService.getFormTempListById(form.getId());
        System.out.println("---formTempList.size()====" + formTempList.size());


        //==== setting recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attachFileRecyclerView.setHasFixedSize(true);
        attachFileRecyclerView.setLayoutManager(layoutManager);

        attachFileRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), attachFileRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                formAnswer = coreService.getFormAnswerById(formAnswer.getId());
                if (formAnswer != null) {
                    if (!formAnswer.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                        PopupMenu popup = new PopupMenu(CheckListFormQuestionActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                             /*   if (id == R.id.delete) {
                                    attachFileList = coreService.getAttachFileListById(formAnswer.getId());
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

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        if (!recognize) {
            adapter1 = new CheckListFormAnswerAdapter(getApplicationContext(), formQuestionGroupList, formItemAnswerList, formTempList, formAnswer, linearLayout);
            recyclerView.setAdapter(adapter1);
            record.setText(" ثبت ");
            if (formAnswer != null) {
                if (formAnswer.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                    layout_bottom.setVisibility(View.GONE);
                }
            }
            refreshAttachAdapter();

        } else {
            adapter = new CheckListFormQuestionAdapter(getApplicationContext(), formQuestionGroupList, formTempList, linearLayout, formAnswer, form);
            recyclerView.setAdapter(adapter);
            complete.setText(getResources().getString(R.string.label_surveyActivity_complete));
            record.setText(" ثبت ");
            refreshAttachAdapter();
            //saveSurveyFormQuestion();
        }

        back_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        ////******click record button*******////
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recognize) {
                    //saveSurveyFormQuestion();
                    //updateSurveyFormQuestion();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_record_form, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,CheckListFormQuestionActivity.this);
                    toast.show();
                    finish();
                } else {
                    updateSurveyFormQuestion();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_record_form, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,CheckListFormQuestionActivity.this);
                    toast.show();
                }

            }
        });


        ////******click complete button*******////
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(CheckListFormQuestionActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_chek_list_sent_layout);
                Button actionYes = (Button) dialog.findViewById(R.id.action_YES);
                Button actionNo = (Button) dialog.findViewById(R.id.action_NO);
                RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                dialog.show();

                actionYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (recognize) {
                            formAnswer.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
                            formAnswer.setSendingStatusDate(new Date());
                            formAnswer.setStatusDate(new Date());
                            formAnswer.setXLatitude(latitude != null ? latitude.toString() : null);
                            formAnswer.setYLongitude(longitude != null ? longitude.toString() : null);
                            formAnswer.setStatusEn(SurveyFormStatusEn.Complete.ordinal());
                            coreService.updateFormAnswer(formAnswer);
                            new Thread(new SaveCheckFormTask()).start();
                            recognize = false;
                            finish();
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.label_record_form, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,CheckListFormQuestionActivity.this);
                            toast.show();

                        } else {
                            updateSurveyFormQuestion();
                            FormItemAnswer tmpFormItemAnswerFS = new FormItemAnswer();
                            tmpFormItemAnswerFS.setFormAnswerId(formAnswer.getId());
                            formItemAnswersList = coreService.getFormItemAnswerListByParam(tmpFormItemAnswerFS);
                            formAnswer = coreService.getFormAnswerById(formAnswer.getId());
                            formAnswer.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
                            formAnswer.setSendingStatusDate(new Date());
                            formAnswer.setStatusEn(SurveyFormStatusEn.Complete.ordinal());
                            formAnswer.setStatusDate(new Date());
                            formAnswer.setXLatitude(latitude != null ? latitude.toString() : null);
                            formAnswer.setYLongitude(longitude != null ? longitude.toString() : null);
                            coreService.updateFormAnswer(formAnswer);
                            new Thread(new SaveCheckFormTask()).start();
                            finish();
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.label_complete_form, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,CheckListFormQuestionActivity.this);
                            toast.show();
                        }
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
        });


        //========= add for for attachment
        attach_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (attachFileRecyclerView.getAdapter().getItemCount() == 4) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_fullAttach_checkList, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,CheckListFormQuestionActivity.this);
                    toast.show();
                } else {

                    final Dialog dialog = new Dialog(CheckListFormQuestionActivity.this);
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
                                        (CheckListFormQuestionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                        ActivityCompat.shouldShowRequestPermissionRationale
                                                (CheckListFormQuestionActivity.this, Manifest.permission.CAMERA)) {
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

    }

    private void setImageBitmap() {
        if (formAnswer != null) {
            plateText.setText(String.valueOf(formAnswer.getCarId()));
            lineCode.setText(String.valueOf(formAnswer.getLineId()));
            dateAndTime.setText(String.valueOf(HejriUtil.chrisToHejriDateTime(formAnswer.getStatusDate())));
        }
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
            path = null;
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


    //========= getRealPathFromURI
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

    /*getPathCamera */
    private String getPathCamera() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index_data);
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
            if (formAnswer != null) {
                attachFile.setEntityId(formAnswer.getId());
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

    /*
    saveSurveyFormQuestion*/
    private void saveSurveyFormQuestion() {
        formTempList = coreService.getFormTempListById(form.getId());
        int count = formTempList.size();
        System.out.println("formTempList.size=" + count);

        for (int i = 0; i < count; i++) {
            formAnswer = coreService.getFormAnswerById(formAnswer.getId());
            formTemp = (FormTemp) formTempList.get(i);
            if (formTemp != null) {
                System.out.println("formAnswer.getId==" + formAnswer.getId());
                formTemp.setFormAnswerId(formAnswer.getId());
                formItemAnswer = new FormItemAnswer();
                formItemAnswer.setQuestion(formTemp.getQuestion());
                formItemAnswer.setFormQuestionId(formTemp.getId());
                formItemAnswer.setFormAnswerId(formAnswer.getId());
                formItemAnswer.setAnswerTypeEn(formTemp.getAnswerTypeEn());
                formItemAnswer.setAnswerStr(formTemp.getAnswerStr());
                formItemAnswer.setFormQuestionGroupId(formTemp.getFormQuestionGroupId());
                formItemAnswer.setInputValuesDefault(formTemp.getInputValuesDefault());
                formItemAnswer.setAnswerInt(formTemp.getAnswerInt());
                coreService.insertNewFormAnswer(formItemAnswer);
            }
        }
    }

    /*
    updateSurveyFormQuestion*/
    private void updateSurveyFormQuestion() {
        formTempList = coreService.getFormTempListById(form.getId());
        int count = formTempList.size();

        for (int i = 0; i < count; i++) {
            formItemAnswer = (FormItemAnswer) formItemAnswerList.get(i);
            formTemp = (FormTemp) formTempList.get(i);
            if (formTemp != null) {
                formItemAnswer.setFormAnswerId(formAnswer.getId());
                formItemAnswer.setQuestion(formTemp.getQuestion());
                formItemAnswer.setFormQuestionId(formTemp.getId());
                formItemAnswer.setAnswerInt(formTemp.getAnswerInt());
                formItemAnswer.setAnswerStr(formTemp.getAnswerStr());
                coreService.updateFormItemAnswer(formItemAnswer);
            }
        }
    }

    //========= saveFormAnswer
    private void saveFormAnswer() {
        formTempList = coreService.getFormTempListById(formId);
        int count = formTempList.size();
        for (int i = 0; i < count; i++) {
            formTemp = (FormTemp) formTempList.get(i);
            if (formTemp != null) {
                formTemp.setFormAnswerId(formAnswer.getId());
            }
            coreService.updateFormTemp(formTemp);
        }
    }


    //========= showSettingsAlert
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
                        CheckListFormQuestionActivity.this.startActivity(intent);
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

    /*
    add permission for android M*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                    } else {
                        showSettingsAlert("GPS");
                    }
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recognize && onPause) {
            saveSurveyFormQuestion();
        }
        onPause = false;
    }


    //========= refreshAttachAdapter
    public void refreshAttachAdapter() {
        attachFileList = coreService.getAttachFileListById(formAnswer.getId());
        AttachFileImageList attachFileImageList = new AttachFileImageList(context, attachFileList);
        attachFileRecyclerView.setAdapter(attachFileImageList);
    }

    //========= SaveCheckFormTask
    private class SaveCheckFormTask implements Runnable {
        @Override
        public void run() {
            //BackgroundService backgroundService = new BackgroundService();
            Services services = new Services(getApplicationContext());
            services.sendCheckForm(coreService, formAnswer);
            //backgroundService.sendCheckForm(context, coreService, formAnswer, application.getCurrentUser());
        }
    }

}
