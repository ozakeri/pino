package com.gap.bis_inspection.widget;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by D on 3/31/2018.
 */

public class VpnCheck {
//    public boolean VpnConnectionCheck()
//    {
//
//        List<String> networkList = new ArrayList<>();
//        try {
//            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
//                if (networkInterface.isUp())
//                    networkList.add(networkInterface.getName());
//            }
//        } catch (Exception ex)
//        {
//            Log.e("MAIN","isVpnUsing Network List didn't received");
//        }
//
//        return networkList.contains("tun0");
//
//
//    }
//    public boolean VpnConnectionCheck()
//    {
//
//
//        return false;
//
//
//    }


    public boolean VpnConnectionCheck(Context context)
    {


        if(VpnConnectionCheck2())
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    public boolean VpnConnectionCheck2()
    {

        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception ex)
        {
            Log.e("MAIN","isVpnUsing Network List didn't received");
        }

        return networkList.contains("tun0");

    }

}
