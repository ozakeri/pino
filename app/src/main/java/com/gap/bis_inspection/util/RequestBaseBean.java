package com.gap.bis_inspection.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.util.volly.DeviceBean;

public class RequestBaseBean implements Parcelable {

    private DeviceBean device = Util.getDevice();
    private String documentUsername = "inspection";
    private String documentPassword = "inspect!gap@1395";
    private String clientId = "2";
    private String version = AppController.getInstance().getVersionName();

    public RequestBaseBean() {
    }

    protected RequestBaseBean(Parcel in) {
        device = in.readParcelable(DeviceBean.class.getClassLoader());
        documentUsername = in.readString();
        documentPassword = in.readString();
        clientId = in.readString();
        version = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeString(documentUsername);
        dest.writeString(documentPassword);
        dest.writeString(clientId);
        dest.writeString(version);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RequestBaseBean> CREATOR = new Creator<RequestBaseBean>() {
        @Override
        public RequestBaseBean createFromParcel(Parcel in) {
            return new RequestBaseBean(in);
        }

        @Override
        public RequestBaseBean[] newArray(int size) {
            return new RequestBaseBean[size];
        }
    };

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public String getDocumentUsername() {
        return documentUsername;
    }

    public void setDocumentUsername(String documentUsername) {
        this.documentUsername = documentUsername;
    }

    public String getDocumentPassword() {
        return documentPassword;
    }

    public void setDocumentPassword(String documentPassword) {
        this.documentPassword = documentPassword;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
