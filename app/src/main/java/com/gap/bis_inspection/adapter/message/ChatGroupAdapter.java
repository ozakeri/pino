package com.gap.bis_inspection.adapter.message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 09/10/2016.
 */
public class ChatGroupAdapter extends ArrayAdapter<ChatGroup> {
    private LayoutInflater inflater;
    private Handler handler;

    public ChatGroupAdapter(Context context, int resource, List<ChatGroup> object) {
        super(context, resource, object);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @SuppressLint({"SetTextI18n", "InflateParams", "ViewHolder"})
    @Override
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        view = inflater.inflate(R.layout.user_chat_group_list, null);

        final TextView groupVT, counterVT, nameVT, messageVT, dateVT;
        groupVT = (TextView) view.findViewById(R.id.group_VT);
        counterVT = (TextView) view.findViewById(R.id.counter_VT);
        nameVT = (TextView) view.findViewById(R.id.name_VT);
        messageVT = (TextView) view.findViewById(R.id.message_VT);
        dateVT = (TextView) view.findViewById(R.id.date_VT);
        final ChatGroup userChatGroup = getItem(position);

        if (userChatGroup != null) {

            groupVT.setText(userChatGroup.getName());

            if (userChatGroup.getCountOfUnreadMessage() == 0) {
                counterVT.setVisibility(View.INVISIBLE);
            } else {
                counterVT.setText(userChatGroup.getCountOfUnreadMessage() + "");
            }

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (userChatGroup.getCountOfUnreadMessage() == 0) {
                        counterVT.setVisibility(View.INVISIBLE);
                    } else {
                        counterVT.setText(userChatGroup.getCountOfUnreadMessage() + "");
                    }
                    handler.postDelayed(this, 1000);
                }
            }, 1000);


            String messageSummary = "";
            String senderName = "";
            String displayDate = "";
            if (userChatGroup.getLastChatMessage() != null) {
                messageSummary = userChatGroup.getLastChatMessage().getMessage();


                if (messageSummary != null) {
                    if (messageSummary.contains("\n")) {
                        messageSummary = messageSummary.substring(0, messageSummary.indexOf("\n"));
                    }
                    messageSummary = messageSummary.length() > 50 ? messageSummary.substring(0, 50) : messageSummary;
                } else {
                    messageSummary = "sendingFile";
                }

                if (userChatGroup.getLastChatMessage().getSenderAppUser() != null) {
                    senderName = (userChatGroup.getLastChatMessage().getSenderAppUser().getName() + " " + userChatGroup.getLastChatMessage().getSenderAppUser().getFamily());
                } else {
                    senderName = userChatGroup.getLastChatMessage().getSenderAppUserId().toString();
                }
                if (userChatGroup.getLastChatMessage().getSendDate() != null) {
                    displayDate = HejriUtil.chrisToHejriDateTime(userChatGroup.getLastChatMessage().getSendDate());
                } else if (userChatGroup.getLastChatMessage().getDateCreation() != null) {
                    displayDate = HejriUtil.chrisToHejriDateTime(userChatGroup.getLastChatMessage().getDateCreation());

                }
                //messageSummary = messageSummary + ":  " + senderName;
                nameVT.setText(senderName + " " + ":");
                messageVT.setText(messageSummary);
                dateVT.setText(displayDate);
            }
        }
        //dateVT.setTextDirection(View.LAYOUT_DIRECTION_LTR);
        return view;
    }
}
