package com.gap.bis_inspection.adapter.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.objectmodel.ChatGroupMember;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 10/17/2016.
 */
public class ChatGroupDetailAdapter extends ArrayAdapter<ChatGroupMember> {
    Context m_context;
    LayoutInflater inflater;
    ChatGroupMember chatGroupMember;

    public ChatGroupDetailAdapter(Context context, int resource, List<ChatGroupMember> object) {
        super(context, resource, object);
        inflater = LayoutInflater.from(context);
        m_context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.chat_group_member_list, null);
        TextView memberName, memberFamily;
        memberName = (TextView) view.findViewById(R.id.memberName_VT);
        memberFamily = (TextView) view.findViewById(R.id.memberFamily_VT);
        chatGroupMember = getItem(position);

        if (chatGroupMember != null && chatGroupMember.getAppUser() != null) {
            memberName.setText(chatGroupMember.getAppUser().getName());
            memberFamily.setText(chatGroupMember.getAppUser().getFamily());

            System.out.println("============" +chatGroupMember.getAppUser().getName() );
            System.out.println("===========" + chatGroupMember.getAppUser().getFamily());
        }
        return view;
    }
}
