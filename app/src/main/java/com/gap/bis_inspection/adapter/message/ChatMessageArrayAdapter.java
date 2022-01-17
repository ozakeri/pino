package com.gap.bis_inspection.adapter.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.objectmodel.AppUser;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.db.objectmodel.User;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageArrayAdapter extends ArrayAdapter<ChatMessage> {
    private Context context;
    private List<ChatMessage> chatMessageList;
    private User currentUser;
    private boolean getValue = false;
    private int pos;
    private List<Integer> integerList = new ArrayList<>();

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatMessageArrayAdapter(Context context, int textViewResourceId, List<ChatMessage> chatMessageList, User currentUser) {
        super(context, textViewResourceId);
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.currentUser = currentUser;
        // this.copyText = copyText;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    @NonNull
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ImageView img_deliver, img_show;
        RelativeLayout layout_progress;
        final TextView chatTextVT, nameVT, timeVT, dateVT;
        LinearLayout layout_chat_item;
        ChatMessage chatMessage = getItem(position);

        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessage != null) {

            if (inflater != null) {
                if (chatMessage.getSenderAppUserId().equals(currentUser.getServerUserId())) {
                    row = inflater.inflate(R.layout.right_message, parent, false);
                } else {
                    row = inflater.inflate(R.layout.left_message, parent, false);
                }
            }

            chatTextVT = (TextView) row.findViewById(R.id.msgr);
            nameVT = (TextView) row.findViewById(R.id.name_VT);
            timeVT = (TextView) row.findViewById(R.id.time_VT);
            dateVT = (TextView) row.findViewById(R.id.date_VT);
            img_deliver = (ImageView) row.findViewById(R.id.img_deliver);
            ImageView img_SendFile = (ImageView) row.findViewById(R.id.img_SendFile);
            img_show = (ImageView) row.findViewById(R.id.img_show);
            layout_progress = (RelativeLayout) row.findViewById(R.id.layout_progress);
            layout_chat_item = (LinearLayout) row.findViewById(R.id.layout_chat_item);
            chatTextVT.setText(chatMessage.getMessage());

            String senderName = "";
            if (chatMessage.getSenderAppUser() != null) {
                if (chatMessage.getSenderAppUser().getName() != null) {
                    senderName = chatMessage.getSenderAppUser().getName();
                }
                if (chatMessage.getSenderAppUser().getFamily() != null) {
                    senderName += " " + chatMessage.getSenderAppUser().getFamily();
                }
                nameVT.setText(senderName);
            }


            String displayDate = "";
            String displayTime = "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if (chatMessage.getSendDate() != null) {
                displayDate = HejriUtil.chrisToHejri(chatMessage.getSendDate());
                displayTime = simpleDateFormat.format(chatMessage.getSendDate());
            } else if (chatMessage.getDateCreation() != null) {
                displayDate = HejriUtil.chrisToHejri(chatMessage.getDateCreation());
                displayTime = simpleDateFormat.format(chatMessage.getDateCreation());
            }
            timeVT.setText(displayTime);
            dateVT.setText(displayDate);

            ProgressBar downloadProgressBar = (ProgressBar) row.findViewById(R.id.circularProgressbar);
            //TextView downloadProgressBarTV = (TextView) row.findViewById(R.id.tv);
            //ImageView downloadProgressBarImage = (ImageView) row.findViewById(R.id.circularProgressbarImg);

            Resources res = context.getResources();
            Drawable drawable = res.getDrawable(R.drawable.circular);
            int state = 0;
            //downloadProgressBar.setProgress(state);
           // downloadProgressBar.setSecondaryProgress(100);
          //  downloadProgressBar.setMax(100);
          //  downloadProgressBar.setProgressDrawable(drawable);
           // downloadProgressBarTV.setText(state + "%");

            Bitmap bm;
            if (chatMessage.getReadIs()) {
                img_deliver.setBackgroundResource(R.mipmap.seen);
                downloadProgressBar.setVisibility(View.INVISIBLE);
               // downloadProgressBarTV.setVisibility(View.INVISIBLE);
              //  downloadProgressBarImage.setVisibility(View.INVISIBLE);

            } else if (chatMessage.getDeliverIs()) {
                img_deliver.setBackgroundResource(R.mipmap.deliverd);
                downloadProgressBar.setVisibility(View.INVISIBLE);
              //  downloadProgressBarTV.setVisibility(View.INVISIBLE);
              //  downloadProgressBarImage.setVisibility(View.INVISIBLE);

            } else if (chatMessage.getSendDate() != null) {
                img_deliver.setBackgroundResource(R.mipmap.sent);
                downloadProgressBar.setVisibility(View.INVISIBLE);
              //  downloadProgressBarTV.setVisibility(View.INVISIBLE);
              //  downloadProgressBarImage.setVisibility(View.INVISIBLE);

            } else if (chatMessage.getSendingStatusEn().equals(SendingStatusEn.Fail.ordinal())) {
                if (chatMessage.isLocalAttachFileExist()) {
                    img_deliver.setBackgroundResource(R.mipmap.sent);
                }

                downloadProgressBar.setVisibility(View.INVISIBLE);
              //  downloadProgressBarTV.setVisibility(View.INVISIBLE);
              //  downloadProgressBarImage.setVisibility(View.INVISIBLE);
                //bm = null;
                //img_show.setImageBitmap(bm);
                //img_show.getLayoutParams().height = 100;
                //img_show.getLayoutParams().width = 100;
                //img_show.setBackgroundResource(R.mipmap.faild);
            } else {
                img_deliver.setBackgroundResource(R.mipmap.pending);
            }

            if (chatMessage.getSenderAppUserId().equals(currentUser.getServerUserId())) {

            } else {
                if (!chatMessage.getReadIs()) {
                    integerList.add(position);
                    layout_chat_item.setBackgroundResource(R.color.new_message);
                }
            }

            if (integerList != null) {
                //EventBus.getDefault().post(new EventBusModel(integerList));
            }

            if (chatMessage.getAttachFileUserFileName() != null) {
                layout_progress.setVisibility(View.VISIBLE);
                //chatTextVT.setText(chatMessage.getAttachFileUserFileName() + "(" + getHumanReadableFileSize(chatMessage.getAttachFileSize()) + ")");
                if (chatMessage.isLocalAttachFileExist()) {
                    if (chatMessage.getSenderAppUserId().equals(currentUser.getServerUserId())) {
                        img_SendFile.setVisibility(View.VISIBLE);
                        img_SendFile.setBackgroundResource(R.mipmap.sendfile);

                        File file = new File(chatMessage.getAttachFileLocalPath());
                        System.out.println("getAttachFileLocalPath0===" + chatMessage.getAttachFileLocalPath());

                        if (isFileValidImage(file)) {
                            bm = resizeBitmap(chatMessage.getAttachFileLocalPath(), 170, 150);
                            System.out.println("getAttachFileLocalPath1===" + chatMessage.getAttachFileLocalPath() + "====" + position);
                            img_show.setImageBitmap(bm);
                            img_show.getLayoutParams().height = 200;
                            img_show.getLayoutParams().width = 220;
                            img_SendFile.setVisibility(View.INVISIBLE);
                        }

                        if (!chatMessage.getAttachFileSize().equals(chatMessage.getAttachFileSentSize())) {
                            if (!chatMessage.getAttachFileSentSize().equals(0) && !chatMessage.getAttachFileSize().equals(0)) {
                                state = (chatMessage.getAttachFileSentSize() * 100) / chatMessage.getAttachFileSize();
                            }
                         //   downloadProgressBar.setProgress(state);
                         //   downloadProgressBarTV.setText(state + "%");
                        } else {
                            downloadProgressBar.setVisibility(View.INVISIBLE);
                          //  downloadProgressBarTV.setVisibility(View.INVISIBLE);
                          //  downloadProgressBarImage.setVisibility(View.INVISIBLE);
                        }

                    } else if (chatMessage.getAttachFileSize() == null || chatMessage.getAttachFileReceivedSize() == null || chatMessage.getAttachFileSize().equals(chatMessage.getAttachFileReceivedSize())) {
                        img_SendFile.setVisibility(View.VISIBLE);
                        img_SendFile.setBackgroundResource(R.mipmap.sendfile);

                        File file = new File(chatMessage.getAttachFileLocalPath());
                        if (isFileValidImage(file)) {

                            System.out.println("getAttachFileLocalPath2===" + chatMessage.getAttachFileLocalPath() + "====" + position);
                            bm = resizeBitmap(chatMessage.getAttachFileLocalPath(), 170, 150);
                            img_show.setImageBitmap(bm);

                            img_show.getLayoutParams().height = 200;
                            img_show.getLayoutParams().width = 220;
                            img_SendFile.setVisibility(View.INVISIBLE);
                            downloadProgressBar.setVisibility(View.INVISIBLE);
                          //  downloadProgressBarTV.setVisibility(View.INVISIBLE);
                          //  downloadProgressBarImage.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        img_SendFile.setVisibility(View.INVISIBLE);

                        if (chatMessage.getAttachFileReceivedSize() != null && !chatMessage.getAttachFileReceivedSize().equals(0) &&
                                chatMessage.getAttachFileSize() != null && !chatMessage.getAttachFileSize().equals(0)) {
                            state = (chatMessage.getAttachFileReceivedSize() * 100) / chatMessage.getAttachFileSize();
                        }
                       // downloadProgressBar.setProgress(state);
                      //  downloadProgressBarTV.setText(state + "%");
                    }
                } else {
                    downloadProgressBar.setVisibility(View.INVISIBLE);
                  //  downloadProgressBarTV.setVisibility(View.INVISIBLE);
                  //  downloadProgressBarImage.setVisibility(View.INVISIBLE);

                    img_SendFile.setVisibility(View.VISIBLE);
                    img_SendFile.setBackgroundResource(R.mipmap.receivedfile);
                }
            } else {
                img_SendFile.setVisibility(View.INVISIBLE);
                downloadProgressBar.setVisibility(View.INVISIBLE);
                //downloadProgressBarTV.setVisibility(View.INVISIBLE);
               // downloadProgressBarImage.setVisibility(View.INVISIBLE);
            }
        }

        return row;
    }

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    private boolean isFileValidImage(File file) {
        String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String getHumanReadableFileSize(Integer fileSize) {
        String result = "";
        if (fileSize != null) {
            Integer kilobyteLength = 1024;
            Integer megabyteLength = 1024 * 1024;

            if (fileSize.compareTo(kilobyteLength) < 0) {
                result = fileSize + "B";
            } else if (fileSize.compareTo(kilobyteLength) >= 0 && fileSize.compareTo(megabyteLength) < 0) {
                Double div = fileSize.doubleValue() / kilobyteLength.doubleValue();
                NumberFormat numberFormat = new DecimalFormat();
                numberFormat.setMaximumFractionDigits(2);
                result = numberFormat.format(div) + "K";
            } else {
                Double div = fileSize.doubleValue() / megabyteLength.doubleValue();
                NumberFormat numberFormat = new DecimalFormat();
                numberFormat.setMaximumFractionDigits(2);
                result = numberFormat.format(div) + "M";
            }

        }
        return result;
    }


    public void refill(List<ChatMessage> chatMessages) {
        chatMessageList.clear();
        chatMessageList.addAll(chatMessages);
        //notifyDataSetChanged();
    }
}
