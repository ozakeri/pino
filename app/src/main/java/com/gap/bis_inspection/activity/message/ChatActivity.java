

package com.gap.bis_inspection.activity.message;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.checklist.FullScreenActivity;
import com.gap.bis_inspection.adapter.message.ChatMessageArrayAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;
import com.gap.bis_inspection.db.objectmodel.ChatGroupMember;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.EventBusModel;
import com.jaiselrahman.filepicker.activity.DirSelectActivity;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.activity.PickFile;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Context context = this;
    private CoreService coreService;
    private ListView listView;
    private EditText messageET;
    private ChatMessageArrayAdapter chatArrayAdapter;
    private long chatGroupId = 0;
    private AppController application;
    private ChatMessage chatMessage;
    private Long selectedChatMessageId;
    private Handler handler;
    private int REQUEST_CAMERA = 1;
    private int SELECT_IMAGE = 2;
    private int SELECT_FILE = 3;
    private Uri mCapturedImageURI;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private List<Boolean> integerList = new ArrayList<>();
    private Services services;
    private PackageManager packageManager;
    private ActivityManager am;
    private boolean checkCurrentActivity = true;
    private Bundle bundle;
    private TextView groupNameTV, countMemberTV;

    private boolean isPrivateChatMessage = false;
    private Long receiverUserId = null;
    private String receiverUserIdStr = "";
    private String memberName = "";
    private List<ChatMessage> listByParam = null;
    private LinearLayout LinearLayout_group;
    private Boolean groupIsPrivate = false;
    private final static int FILE_REQUEST_CODE = 1;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private int fileType = 0;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString("action", null);
        editor.putString("groupId", null);
        editor.apply();

        application = (AppController) getApplication();
        listView = findViewById(R.id.messages_view);
        ImageView sendButton = findViewById(R.id.send_Button);
        messageET = findViewById(R.id.message_ET);
        groupNameTV = findViewById(R.id.groupName_TV);

        countMemberTV = findViewById(R.id.countMember_TV);
        LinearLayout_group = findViewById(R.id.LinearLayout_group);
        RelativeLayout backIcon = findViewById(R.id.back_Icon);
        final RelativeLayout attachIcon = findViewById(R.id.attach_Icon);
        //messageET.requestFocus();
        messageET.setSingleLine(false);
        messageET.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        messageET.setVerticalScrollBarEnabled(true);
        messageET.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        services = new Services(getApplicationContext());
        handler = new Handler();

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (messageET.length() >= 1000) {
                    Toast.makeText(ChatActivity.this, "حداکثر کاراکتر مجاز", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                attachIcon.setVisibility(View.GONE);
                String message = messageET.getText().toString();
                if (message.matches("")) {
                    attachIcon.setVisibility(View.VISIBLE);
                }
            }
        });


        DatabaseManager databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            chatGroupId = bundle.getLong("chatGroupId");
            String chatGroupName = bundle.getString("chatGroupName");
            groupIsPrivate = bundle.getBoolean("groupIsPrivate");
            ChatGroupMember tmpChatGroupMember = new ChatGroupMember();
            tmpChatGroupMember.setChatGroupId(chatGroupId);
            List<ChatGroupMember> chatGroupMemberList = coreService.getChatGroupMemberListByParam(tmpChatGroupMember);

            groupNameTV.setText(String.valueOf(chatGroupName));
            countMemberTV.setText(String.valueOf(chatGroupMemberList.size()));


            LinearLayout_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ChatGroupDetailActivity.class);
                    intent.putExtra("chatGroupId", chatGroupId);
                    startActivityForResult(intent, 111);
                }
            });
        }

        refreshChatMessageList(false, false);

        //updateList();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChatMessage("");

            }
        });


        final ActivityResultLauncher<Configurations> pickImage = registerForActivityResult(new PickFile().throughDir(true), new ActivityResultCallback<List<MediaFile>>() {
            @Override
            public void onActivityResult(List<MediaFile> result) {

                System.out.println("=====result====" + result);
                if (result != null){
                    setMediaFiles(result);
                }

                else
                    Toast.makeText(ChatActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
        });

        ////******attach file*******////
        attachIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create custom dialog object
                final Dialog dialog = new Dialog(ChatActivity.this);
                // Include dialog.xml file
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog);
                // set values for custom dialog components - text, image and button
                TextView cameraVT = (TextView) dialog.findViewById(R.id.camera_VT);
                TextView galleryVT = (TextView) dialog.findViewById(R.id.gallery_VT);
                TextView fileVT = (TextView) dialog.findViewById(R.id.file_VT);
                RelativeLayout closeIcon = (RelativeLayout) dialog.findViewById(R.id.closeIcon);
                dialog.show();
                cameraVT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        pickImage.launch(new Configurations.Builder()
                                .setCheckPermission(true)
                                .setSelectedMediaFiles(mediaFiles)
                                .enableImageCapture(true)
                                .setShowVideos(false)
                                .setSkipZeroSizeFiles(true)
                                .setSingleChoiceMode(true)
                                .build());

                    /*    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                WRITE_EXTERNAL_STORAGE) + ContextCompat
                                .checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale
                                    (ChatActivity.this, WRITE_EXTERNAL_STORAGE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale
                                            (ChatActivity.this, Manifest.permission.CAMERA)) {
                                finish();
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                            new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                            MY_PERMISSIONS_REQUEST);
                                }
                            }

                        } else {
                            // write your logic code if permission already granted
                            cameraIntent();
                        }*/

                        dialog.dismiss();
                    }
                });

                galleryVT.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View view) {
                       // galleryIntent();
                        dialog.dismiss();
                    }
                });

                fileVT.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void onClick(View view) {
                       // fileIntent();

                        Intent intent = new Intent(ChatActivity.this, DirSelectActivity.class);
                        intent.putExtra(DirSelectActivity.CONFIGS, new Configurations.Builder()
                                .setCheckPermission(true)
                                .setSelectedMediaFiles(mediaFiles)
                                .setShowFiles(true)
                                .setShowImages(true)
                                .setShowAudios(false)
                                .setShowVideos(false)
                                .setIgnoreNoMedia(false)
                                .enableVideoCapture(false)
                                .enableImageCapture(true)
                                .setIgnoreHiddenFile(false)
                                .setSingleChoiceMode(true)
                                .setTitle("Select a file")
                                .build());
                        startActivityForResult(intent, FILE_REQUEST_CODE);

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

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handler.removeCallbacksAndMessages(null);
                finish();
                overridePendingTransition(R.anim.motion, R.anim.motion2);
                if (AppController.getInstance().isNewMessage()) {
                    startActivity(new Intent(ChatActivity.this, ChatGroupListActivity.class));
                    AppController.getInstance().setNewMessage(false);
                }

            }
        });


        ////******listView onClick*******////
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                final ChatMessage chatMessage = (ChatMessage) parent.getItemAtPosition(position);
                selectedChatMessageId = chatMessage.getId();

                if (chatMessage.isLocalAttachFileExist()) {
                    Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                    intent.putExtra("imagePath", chatMessage.getAttachFileLocalPath());
                    startActivity(intent);

                } else {
                    ImageView img_SendFile = (ImageView) view.findViewById(R.id.img_SendFile);
                    ProgressBar downloadProgressBar = (ProgressBar) view.findViewById(R.id.circularProgressbar);
                    //TextView downloadProgressBarTV = (TextView) view.findViewById(R.id.tv);
                    //ImageView downloadProgressBarImage = (ImageView) view.findViewById(R.id.circularProgressbarImg);
                    img_SendFile.setVisibility(View.INVISIBLE);
                    downloadProgressBar.setVisibility(View.VISIBLE);
                    // downloadProgressBarTV.setVisibility(View.VISIBLE);
                    //downloadProgressBarImage.setVisibility(View.VISIBLE);

                    new Thread(new DownloadAttachFile()).start();
                }

           /*     if (chatMessage.getSendingStatusEn() != null && chatMessage.getSendingStatusEn().equals(SendingStatusEn.Fail.ordinal())) {
                    ImageView img_deliver = (ImageView) view.findViewById(R.id.img_deliver);
                    img_deliver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (chatMessage.getMessage() != null) {
                                sendChatMessage(chatMessage.getMessage());
                            }
                        }
                    });
                }*/
            }
        });
        registerForContextMenu(listView);
    }

    ////******cameraIntent*******////
    public void cameraIntent() {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, REQUEST_CAMERA);
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    ////******galleryIntent*******////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), SELECT_IMAGE);
    }

    ////******fileIntent*******////
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void fileIntent() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        // startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        startActivityForResult(chooseFile, SELECT_FILE);
    }


    ////******update chat List*******////
    private void refreshChatMessageList(boolean scrollToEnd, boolean isSend) {
        Integer firstVisiblePosition = null;
        Integer lastVisiblePosition = null;
        boolean scrollToEndForNewMessage = false;
        listByParam = new ArrayList<>();


        if (listView.getAdapter() != null) {
            firstVisiblePosition = listView.getFirstVisiblePosition();
            lastVisiblePosition = listView.getLastVisiblePosition();
            if (((ChatMessageArrayAdapter) listView.getAdapter()).getChatMessageList().size() - 1 == lastVisiblePosition) {
                scrollToEndForNewMessage = true;
            }
        }
        ChatMessage tmpChatMessageFS = new ChatMessage();

        tmpChatMessageFS.setCreateNewPvChatGroup(false);

        Integer loadMessageCount = 80;

        tmpChatMessageFS.setChatGroupId(chatGroupId);
        tmpChatMessageFS.setCreateNewPvChatGroup(isPrivateChatMessage);

        if (receiverUserId != null) {

            ChatMessage chatMessage = new ChatMessage();
            System.out.println("receiverUserId==========" + receiverUserId);
            chatMessage.setReceiverAppUserId(receiverUserId);
            listByParam = coreService.getChatMessageListByParam(chatMessage);

            System.out.println("listByParam==========" + listByParam.size());
            if (groupIsPrivate || isPrivateChatMessage) {
                LinearLayout_group.setEnabled(false);
            }

            if (listByParam.size() > 1) {
                for (ChatMessage m : listByParam) {
                    if (m.getReceiverAppUserId() != null) {
                        if (m.getReceiverAppUserId().equals(receiverUserId)) {
                            if (m.getChatGroupId() != null) {
                                ChatGroup chatGroup = coreService.getChatGroupById(m.getChatGroupId());
                                if (chatGroup != null) {
                                    tmpChatMessageFS.setChatGroupId(chatGroup.getId());
                                    groupNameTV.setText(String.valueOf(chatGroup.getName()));
                                    countMemberTV.setText("2");
                                    LinearLayout_group.setEnabled(false);
                                    //tmpChatMessageFS.setReceiverAppUserId(receiverUserId);
                                }
                            }
                        }
                    } else {
                        tmpChatMessageFS.setReceiverAppUserId(receiverUserId);
                        groupNameTV.setText(memberName);
                        countMemberTV.setText("2");
                        LinearLayout_group.setEnabled(false);
                    }
                }
            }
        }

        List<ChatMessage> chatMessageList = coreService.getChatMessageListByParamLimit(tmpChatMessageFS, loadMessageCount);


        if (isSend) {
            chatArrayAdapter = new ChatMessageArrayAdapter(getApplicationContext(), R.layout.right_message, chatMessageList, application.getCurrentUser());
            listView.setAdapter(chatArrayAdapter);
            listView.setSelection(listView.getAdapter().getCount());
            return;
        }


        if (chatMessageList != null) {
            for (ChatMessage chatMessage : chatMessageList) {
                chatMessage.setSenderAppUser(coreService.getAppUserById(chatMessage.getSenderAppUserId()));
                if (!chatMessage.getReadIs()) {
                    integerList.add(chatMessage.getReadIs());
                }
            }
        }


        if (firstVisiblePosition == null || scrollToEnd || scrollToEndForNewMessage) {
            // firstVisiblePosition = chatMessageList.size() - 1;
        }

        if (listView.getAdapter() == null && chatMessageList != null) {
            chatArrayAdapter = new ChatMessageArrayAdapter(getApplicationContext(), R.layout.right_message, chatMessageList, application.getCurrentUser());
            listView.setAdapter(chatArrayAdapter);
            listView.requestFocusFromTouch();
            listView.setSelection(listView.getCount() - integerList.size());

        } else {
            if (chatMessageList != null) {
                ((ChatMessageArrayAdapter) listView.getAdapter()).refill(chatMessageList);
            }
        }

        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        ChatMessage tmpChatMessageFS2 = new ChatMessage();
        tmpChatMessageFS2.setChatGroupId(chatGroupId);
        tmpChatMessageFS2.setReadIs(Boolean.FALSE);
        tmpChatMessageFS2.setSenderAppUserIdNot(application.getCurrentUser().getServerUserId());
        coreService.updateChatMessageAsReadByParam(tmpChatMessageFS2);

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

    }


    ////******send method chat message*******////

    private void sendChatMessage(String message_str) {

        String message = messageET.getText().toString();
        if (message.equals("")) {
            message = message_str;
        }
        message = message.trim();
        if (!message.isEmpty()) {
            chatMessage = new ChatMessage();
            chatMessage.setChatGroupId(chatGroupId);
            chatMessage.setDateCreation(new Date());
            chatMessage.setMessage(message);
            chatMessage.setSenderAppUserId(application.getCurrentUser().getServerUserId());
            chatMessage.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            chatMessage.setSendingStatusDate(new Date());
            chatMessage.setDeliverIs(Boolean.FALSE);
            chatMessage.setReadIs(Boolean.FALSE);
            chatMessage.setReceiverAppUserId(null);
            chatMessage.setCreateNewPvChatGroup(isPrivateChatMessage);
            if (receiverUserId != null) {
                chatMessage.setReceiverAppUserId(receiverUserId);
            }
            if (groupIsPrivate) {
                chatMessage.setCreateNewPvChatGroup(true);
            }

            chatMessage = coreService.insertChatMessage(chatMessage);
            new Thread(new SaveChatMessageTask()).start();
            refreshChatMessageList(false, false);
        }
        messageET.setText("");
    }

    ////******send method Attach File*******////

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendChatMessageAttachFile(String filePath) {
        System.out.println("FirstfilePath===" + filePath);
        File file = new File(filePath);
        chatMessage = new ChatMessage();
        chatMessage.setChatGroupId(chatGroupId);
        chatMessage.setDateCreation(new Date());
        chatMessage.setSenderAppUserId(application.getCurrentUser().getServerUserId());
        chatMessage.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
        chatMessage.setSendingStatusDate(new Date());
        chatMessage.setDeliverIs(Boolean.FALSE);
        chatMessage.setReadIs(Boolean.FALSE);
        chatMessage.setReceiverAppUserId(null);
        chatMessage.setCreateNewPvChatGroup(false);
        chatMessage.setReceiverAppUserId(receiverUserId);
        chatMessage.setCreateNewPvChatGroup(isPrivateChatMessage);
        chatMessage.setCreateNewPvChatGroup(isPrivateChatMessage);
        chatMessage.setFileType(fileType);

        chatMessage = coreService.insertChatMessage(chatMessage);

        if (file != null) {
            String userFileName = file.getName();
            System.out.println("userFileName=====" + filePath);
            String filePostfix = userFileName.substring(userFileName.indexOf("."), userFileName.length());
            String path = Environment.getExternalStorageDirectory().toString() + Constants.DEFAULT_OUT_PUT_DIR + Constants.DEFAULT_IMG_OUT_PUT_DIR;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            String newFilePath = path + "/" + chatMessage.getId() + filePostfix;
            System.out.println("path===" + path);
            System.out.println("filePostfix===" + filePostfix);
            System.out.println("newFilePath===" + newFilePath);


            try {
                InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(newFilePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; //try to decrease decoded image
                options.inPurgeable = true; //purgeable to disk
               // Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
               // bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream); //compressed bitmap to file

                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                inputStream.close();
                outputStream.close();
                Long fileSize = new File(newFilePath).length();
                chatMessage.setAttachFileLocalPath(newFilePath);
                chatMessage.setAttachFileUserFileName(userFileName);
                chatMessage.setAttachFileSize(fileSize.intValue());
                chatMessage.setAttachFileSentSize(0);
                coreService.updateChatMessage(chatMessage);
                new Thread(new SaveChatMessageTask()).start();
                refreshChatMessageList(true, true);

                messageET.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        /*} else {
            Toast.makeText(getApplicationContext(), R.string.sendChatMessageAttachFileToast, Toast.LENGTH_LONG).show();
        }*/

    }


    ////******Save Chat Message call background service*******////
    private class SaveChatMessageTask implements Runnable {
        @Override
        public void run() {
            if (!Services.SEND_MESSAGE_IN_PROGRESS) {
                Services.SEND_MESSAGE_IN_PROGRESS = true;
                boolean b = services.sendChatMessage(coreService, chatMessage);
                EventBus.getDefault().post(new EventBusModel(b));
                services.sendChatMessageReadReport();
                Services.SEND_MESSAGE_IN_PROGRESS = false;
            }
        }
    }

    private class DownloadAttachFile implements Runnable {
        @Override
        public void run() {
            DownloadAttachFileASync downloadAttachFileASync = new DownloadAttachFileASync();
            downloadAttachFileASync.execute();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            List<MediaFile> mediaFiles = data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            if(mediaFiles != null) {

                setMediaFiles(mediaFiles);

            } else {
                Toast.makeText(ChatActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setMediaFiles(List<MediaFile> mediaFilesParam) {
        this.mediaFiles.clear();
        this.mediaFiles.addAll(mediaFilesParam);

        for (MediaFile mediaFile : this.mediaFiles) {
            System.out.println("getName=====" + mediaFile.getName());
            System.out.println("getPath=====" + mediaFile.getPath());
            System.out.println("getSize=====" + mediaFile.getSize());
            System.out.println("getUri=====" + mediaFile.getUri());
            System.out.println("getMediaType=====" + mediaFile.getMediaType());
            fileType = mediaFile.getMediaType();

            sendChatMessageAttachFile(mediaFile.getPath());
        }

       // fileListAdapter.notifyDataSetChanged();
    }

    /*@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_FIRST_USER) {
            if (requestCode == 111) {

                System.out.println("resultCode====" + resultCode);
                System.out.println("requestCode====" + requestCode);

                if (data != null) {
                    isPrivateChatMessage = data.getBooleanExtra("isPrivateChatMessage", false);
                    receiverUserId = data.getLongExtra("receiverUserId", 0);
                    memberName = data.getStringExtra("memberName");
                    refreshChatMessageList(true, true);
                    System.out.println("isPrivateChat====" + isPrivateChatMessage);
                    System.out.println("receiverUserId====" + receiverUserId);

                    if (isPrivateChatMessage) {
                        groupNameTV.setText(memberName);
                        countMemberTV.setText("");
                        LinearLayout_group.setEnabled(false);
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_OK) {

            Uri outputFileUri;
            if (requestCode == SELECT_FILE) {
                outputFileUri = data.getData();

                sendChatMessageAttachFile(getRealPathFromURI(outputFileUri));

            } else if (requestCode == SELECT_IMAGE) {
                outputFileUri = data.getData();
                // sendChatMessageAttachFile(getRealPathFromURI(outputFileUri));
            } else if (requestCode == REQUEST_CAMERA) {
                //outputFileUri = data.getData();
                //sendChatMessageAttachFile(getRealPathFromURI(outputFileUri));
                String capturedImageFilePath = getPathCamera();
                // sendChatMessageAttachFile(capturedImageFilePath);

            }
        }
    }*/

    private String getPathCamera() {
        String[] projection = {MediaStore.Images.Media.DATA};
        System.out.println("----------mCapturedImageURI=" + mCapturedImageURI);
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

        System.out.println("path====" + path);
        return path;
    }


    ////******Download Attach File*******////
    @SuppressLint("StaticFieldLeak")
    private class DownloadAttachFileASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ChatMessage chatMessage = coreService.getChatMessageById(selectedChatMessageId);
            if (chatMessage != null) {
                try {
                    services.downloadAttachFile(ChatActivity.this, coreService, chatMessage);
                } catch (Exception e) {
                    e.getMessage();
                }

            }

            return null;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup, menu);
    }


    @SuppressLint("ObsoleteSdkInt")
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.copy) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            String textTocopy = chatArrayAdapter.getItem(index).getMessage();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", textTocopy);
                clipboard.setPrimaryClip(clip);
            } else {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(textTocopy);
            }
            Toast.makeText(getApplicationContext(), "Copy", Toast.LENGTH_LONG).show();

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && writeExternalFile) {
                        // write your logic here
                        cameraIntent();
                    } else {
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //handler.removeCallbacksAndMessages(null);
        finish();
        if (AppController.getInstance().isNewMessage()) {
            startActivity(new Intent(this, ChatGroupListActivity.class));
            AppController.getInstance().setNewMessage(false);
        }
    }

    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkNewMessage(EventBusModel event) {

        if (event.getResult() != null) {
            System.out.println("=======chatGroupId======" + event.getResult());
            ChatGroup chatGroup = new ChatGroup();
            chatGroup.setServerGroupId(Long.valueOf(event.getResult()));
            chatGroup = coreService.getChatGroupByServerGroupId(chatGroup);
            chatGroupId = chatGroup.getId();
            services.getChatGroupList();
            refreshChatMessageList(false, false);
        }

        if (event.isNewMessage() || event.isMessage() || event.isDownloadAttachFile()) {
            refreshChatMessageList(false, false);
        }

        System.out.println("===invalidateViews==");
        listView.invalidateViews();
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
        application.setCurrentActivity(null);
    }

    protected void onResume() {
        super.onResume();
        application.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        application.setCurrentActivity(null);
    }


    private void clearReferences() {
        Activity currActivity = application.getCurrentActivity();
        if (this.equals(currActivity))
            application.setCurrentActivity(null);
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


    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}

