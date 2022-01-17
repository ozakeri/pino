package com.gap.bis_inspection.util.volly;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mahdi on 12/21/16.
 */
public class ErrorResponseBean implements Parcelable {

    private String ERROR;

    public ErrorResponseBean() {
    }

    public ErrorResponseBean(String ERROR) {
        this.ERROR = ERROR;
    }

    public String getERROR() {
        return ERROR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ERROR);
    }

    protected ErrorResponseBean(Parcel in) {
        this.ERROR = in.readString();
    }

    public static final Creator<ErrorResponseBean> CREATOR = new Creator<ErrorResponseBean>() {
        @Override
        public ErrorResponseBean createFromParcel(Parcel source) {
            return new ErrorResponseBean(source);
        }

        @Override
        public ErrorResponseBean[] newArray(int size) {
            return new ErrorResponseBean[size];
        }
    };
}
