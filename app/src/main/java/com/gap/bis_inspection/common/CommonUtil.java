package com.gap.bis_inspection.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 8/28/16.
 */
public class CommonUtil<T> {

    private static String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};

    public static List<Data> getMonthDataList(Context context) {
        final List<Data> monthDataList = new ArrayList<Data>();
        String[] monthArray = context.getResources().getStringArray(R.array.year_months);
        for (int i = 0; i < monthArray.length; i++) {
            String month = monthArray[i];
            monthDataList.add(new Data(Integer.valueOf(i + 1).toString(), month));
        }
        return monthDataList;
    }

    public static List<Integer> getNYearBeforeList(Integer lastYear, int yearsBefore) {
        final List<Integer> yearList = new ArrayList<Integer>();
        for (int i = lastYear - yearsBefore + 1; i <= lastYear; i++) {
            yearList.add(i);
        }
        return yearList;
    }

    public static List<Integer> easyIntegerList(Integer... items) {
        List<Integer> itemList = new ArrayList<>();
        for (Integer item : items) {
            itemList.add(item);
        }
        return itemList;
    }

    public static List<String> easyStringList(String... items) {
        List<String> itemList = new ArrayList<>();
        for (String item : items) {
            itemList.add(item);
        }
        return itemList;
    }

    public static EditText farsiNumberReplacement(EditText editText) {
        if (editText.getText() != null) {
            String text = editText.getText().toString();
            text = text.replaceAll("۰", "0");
            text = text.replaceAll("۱", "1");
            text = text.replaceAll("۲", "2");
            text = text.replaceAll("۳", "3");
            text = text.replaceAll("۴", "4");
            text = text.replaceAll("۵", "5");
            text = text.replaceAll("۶", "6");
            text = text.replaceAll("۷", "7");
            text = text.replaceAll("۸", "8");
            text = text.replaceAll("۹", "9");
            editText.setText(text);
        }
        return editText;
    }

    public static String farsiNumberReplacement(String text) {
        text = text.replaceAll("۰", "0");
        text = text.replaceAll("۱", "1");
        text = text.replaceAll("۲", "2");
        text = text.replaceAll("۳", "3");
        text = text.replaceAll("۴", "4");
        text = text.replaceAll("۵", "5");
        text = text.replaceAll("۶", "6");
        text = text.replaceAll("۷", "7");
        text = text.replaceAll("۸", "8");
        text = text.replaceAll("۹", "9");

        return text;
    }

    public static AutoCompleteTextView AutoCompleteFarsiNumberReplacement(AutoCompleteTextView autoCompleteTextView) {
        if (autoCompleteTextView.getText() != null) {
            String text = autoCompleteTextView.getText().toString();
            text = text.replaceAll("۰", "0");
            text = text.replaceAll("۱", "1");
            text = text.replaceAll("۲", "2");
            text = text.replaceAll("۳", "3");
            text = text.replaceAll("۴", "4");
            text = text.replaceAll("۵", "5");
            text = text.replaceAll("۶", "6");
            text = text.replaceAll("۷", "7");
            text = text.replaceAll("۸", "8");
            text = text.replaceAll("۹", "9");
            autoCompleteTextView.setText(text);
        }
        return autoCompleteTextView;
    }

    @SuppressLint("ResourceAsColor")
    public static Toast showToast(Toast toast) {
        //Typeface typeface = Typeface.createFromAsset( , "Wnazanin.ttf");
        Typeface typeface = Typeface.create("BYekan.ttf", Typeface.BOLD);
        toast.setGravity(Gravity.CENTER, 0, 0);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(15);
        messageTextView.setTextColor(R.color.mdtp_light_gray);
        messageTextView.setTypeface(typeface);
        //messageTextView.getResources().getColor(R.color.mdtp_transparent_black);
        group.setBackgroundResource(R.drawable.textview_toast_style);
        return toast;
    }

    @SuppressLint("ResourceAsColor")
    public static Toast showToast(Toast toast, Context context) {
        Typeface typeface = Typeface.create("BYekan.ttf", Typeface.BOLD);
        toast.setGravity(Gravity.CENTER, 0, 0);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.toast_layout, null);

        TextView messageTextView = view.findViewById(R.id.tvMessage);
        messageTextView.setTextSize(15);
        messageTextView.setTextColor(R.color.mdtp_light_gray);
        messageTextView.setTypeface(typeface);
        return toast;
    }

    public static Snackbar snackbar(Snackbar snackbar) {
        snackbar.setActionTextColor(Color.RED);
        View view = snackbar.getView();
        view.setBackgroundColor(Color.RED);
        TextView textView = (TextView) view.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.GRAY);
        snackbar.show();
        return snackbar;
    }


    public static boolean checkInternetConnection(Context context) {
        // get Connectivity Manager object to check connection

        ConnectivityManager
                connec = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {
            Toast.makeText(context, " اتصال خود را بررسی کنید ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    public static void printLogs(String str) {

        if (str != null) {
            int split = str.length() / 4000 + 1;
            int mod = str.length() % 4000;
            for (int i = 0; i < split; i++) {
                try {
                    Log.e("post", str.substring(i * 4000, (i + 1) * 4000));
                } catch (Exception e) {
                    Log.e("post", str.substring(i * 4000, i * 4000 + mod));
                }
            }
        }
    }


    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        ;
        formatter.applyPattern("#,###");
        return formatter.format(d);
    }


    public static String PerisanNumber(String text) {
        if (text.length() == 0) {
            return "";
        }
        String out = "";
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if ('0' <= c && c <= '9') {
                int number = Integer.parseInt(String.valueOf(c));
                out += persianNumbers[number];
            } else if (c == '٫') {
                out += '،';
            } else {
                out += c;
            }
        }
        return out;
    }


    private static final ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public static int compareDates(Date date1, Date date2) {
        DateFormat dateFormat = dateFormatThreadLocal.get();
        System.out.println("compareDates=" + dateFormat.format(date1).compareTo(dateFormat.format(date2)));
        return dateFormat.format(date1).compareTo(dateFormat.format(date2));
    }

    public static boolean isConnected(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            }
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public static String latinNumberToPersian(String input) {
        String output = input.replaceAll("0", "٠");
        output = output.replaceAll("1", "١");
        output = output.replaceAll("2", "٢");
        output = output.replaceAll("3", "٣");
        output = output.replaceAll("4", "۴");
        output = output.replaceAll("5", "۵");
        output = output.replaceAll("6", "۶");
        output = output.replaceAll("7", "٧");
        output = output.replaceAll("8", "٨");
        output = output.replaceAll("9", "٩");
        return output;
    }
}
