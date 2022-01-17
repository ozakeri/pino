package com.gap.bis_inspection.activity.report;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.adapter.form.AttachFileImageList;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
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

public class ReportActivity extends AppCompatActivity {
    Context context = this;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    AppController application;
    ComplaintReport complaintReport;
    Long entityId;
    Integer entityNameEn;
    String displayName, addIcon;
    Double latitude;
    Double longitude;
    private String TAG;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private AttachFile attachFile;
    private Uri mCapturedImageURI;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private String path;
    private ImageView attach_Icon;
    private RecyclerView attachFileRecyclerView;
    private List<AttachFile> attachFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_report);
        final TextView reportStrVT = (TextView) findViewById(R.id.reportStr_TV);
        RelativeLayout layout_toolbar = (RelativeLayout) findViewById(R.id.layout_toolbar);
        Button sendReportButton = (Button) findViewById(R.id.sendReport_Button);
        attach_Icon = (ImageView) findViewById(R.id.attach_Icon);
        attachFileRecyclerView = (RecyclerView) findViewById(R.id.attachRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attachFileRecyclerView.setHasFixedSize(true);
        attachFileRecyclerView.setLayoutManager(layoutManager);

        addIcon = null;
        Intent intent = getIntent();

        entityId = intent.getLongExtra("entityId", 0);
        entityNameEn = intent.getIntExtra("entityNameEn", 0);
        displayName = intent.getStringExtra("displayName");
        addIcon = intent.getStringExtra("addIcon");

        if (addIcon.equals("line")) {
            layout_toolbar.setBackgroundColor(Color.parseColor("#91d8f7"));
            sendReportButton.setBackgroundResource(R.mipmap.btn_line);
        } else if (addIcon.equals("driver")) {
            layout_toolbar.setBackgroundColor(Color.parseColor("#3aaa35"));
            sendReportButton.setBackgroundResource(R.mipmap.btn_driver);
        } else if (addIcon.equals("car")) {
            layout_toolbar.setBackgroundColor(Color.parseColor("#ed3237"));
            sendReportButton.setBackgroundResource(R.mipmap.btn_car);
        }

        if (entityId.equals((long) 0) || entityNameEn.equals(0)) {
            sendReportButton.setVisibility(View.INVISIBLE);
        }

        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);

        application = (AppController) getApplication();
        complaintReport = new ComplaintReport();
        complaintReport.setId(Long.valueOf(new Date().getTime() + application.getCurrentUser().getServerUserId().toString()));
        coreService.insertComplaintReport(complaintReport);

        attach_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ReportActivity.this);
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
                                    (ReportActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale
                                            (ReportActivity.this, Manifest.permission.CAMERA)) {
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
                PopupMenu popup = new PopupMenu(ReportActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                       /* if (id == R.id.delete) {
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

        ////******send Report Button*******////
        sendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reportStrVT.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,ReportActivity.this);
                    toast.show();
                } else {
                    //complaintReport = new ComplaintReport();
                    //complaintReport.setId(Long.valueOf(new Date().getTime() + application.getCurrentUser().getServerUserId().toString()));
                    complaintReport.setEntityId(entityId);
                    complaintReport.setEntityNameEn(entityNameEn);
                    complaintReport.setDisplayName(displayName);
                    complaintReport.setReportCode("");
                    complaintReport.setReportStr(reportStrVT.getText().toString());
                    complaintReport.setUserReportId(application.getCurrentUser().getServerUserId());
                    complaintReport.setReportDate(new Date());
                    complaintReport.setDeliverIs(Boolean.FALSE);
                    complaintReport.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
                    complaintReport.setXLatitude(latitude != null ? latitude.toString() : null);
                    complaintReport.setYLongitude(longitude != null ? longitude.toString() : null);
                    coreService.updateComplaintReport(complaintReport);
                    new Thread(new SaveComplaintReportTask()).start();
                    finish();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_report_sendMsg, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,ReportActivity.this);
                    toast.show();

                    System.out.println("getId====" + complaintReport.getId());
                }
            }
        });
        ImageView backIcon = (ImageView) findViewById(R.id.back_Icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    ////******call background service*******////
    private class SaveComplaintReportTask implements Runnable {
        @Override
        public void run() {
            Services services = new Services(getApplicationContext());
            services.sendComplaintReport(coreService, complaintReport);
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

    //========= refreshAttachAdapter
    public void refreshAttachAdapter() {
        if (complaintReport != null) {
            System.out.println("complaintReport===" + complaintReport.getId());
            attachFileList = coreService.getAttachFileListById(complaintReport.getId());
            AttachFileImageList attachFileImageList = new AttachFileImageList(context, attachFileList);
            attachFileRecyclerView.setAdapter(attachFileImageList);
        }
    }

}
