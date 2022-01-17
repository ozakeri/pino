package com.gap.bis_inspection.db.objectmodel.advert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Advert {

    @SerializedName("processBisDataVOList")
    @Expose
    public List<ProcessBisDataVO> processBisDataVOList = null;

    @SerializedName("processBisSettingVOList")
    @Expose
    public List<ProcessBisSettingVO> processBisSettingVOList = null;

}
