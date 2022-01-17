package com.gap.bis_inspection.util.volly.chatgrpupemember;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatGroupMember {

    @SerializedName("processStatus")
    @Expose
    public Integer processStatus;
    @SerializedName("privilegeTypeEn_text")
    @Expose
    public String privilegeTypeEnText;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("adminIs")
    @Expose
    public Boolean adminIs;
    @SerializedName("userId")
    @Expose
    public Long userId;
    @SerializedName("processStatus_text")
    @Expose
    public String processStatusText;
    @SerializedName("privilegeTypeEn")
    @Expose
    public Integer privilegeTypeEn;
    @SerializedName("expireDate")
    @Expose
    public String expireDate;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("status_text")
    @Expose
    public String statusText;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("userCreationId")
    @Expose
    public Integer userCreationId;

}
