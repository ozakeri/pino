package com.gap.bis_inspection.util.volly.chatmessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavedChatMessage {
    @SerializedName("dateCreation")
    @Expose
    public String dateCreation;
    @SerializedName("clientId")
    @Expose
    public Integer clientId;
    @SerializedName("id")
    @Expose
    public Long id;
    @SerializedName("totalReceivedBytes")
    @Expose
    public Integer totalReceivedBytes;
}
