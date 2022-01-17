package com.gap.bis_inspection.util.volly.chatgrpupemember;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatGroupMemberList {
    @SerializedName("maxMember")
    @Expose
    public Integer maxMember;
    @SerializedName("notifyAct")
    @Expose
    public Boolean notifyAct;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("chatGroupMembers")
    @Expose
    public List<ChatGroupMember> chatGroupMembers = null;
    @SerializedName("id")
    @Expose
    public Long id;
    @SerializedName("status")
    @Expose
    public Integer status;
}
