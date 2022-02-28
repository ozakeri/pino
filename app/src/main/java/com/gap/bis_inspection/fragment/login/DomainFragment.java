package com.gap.bis_inspection.fragment.login;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.GlobalDomain;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.FontCache;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DomainFragment extends Fragment {

    ProgressDialog progressBar;
    AutoCompleteTextView domainEditText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private GlobalDomain globalDomain = GlobalDomain.getInstance();


    private static final String TAG = DomainFragment.class.getName();
    private Button btnRequest;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "http://bis.isfahanptc.ir/rfServices/getServerDateTime";
    private String[] city = {"tehran", "isfahan","qazvin", "اصفهان", "قزوین","تهران"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_domain, container, false);

        // progressBar = (ProgressBar) view.findViewById(R.id.progress);
        domainEditText = (AutoCompleteTextView) view.findViewById(R.id.domain_txt);
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        //domainEditText.setText("https://");
        //Selection.setSelection(domainEditText.getText(), domainEditText.getText().length());

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, null);
        editor.apply();

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, city);

        Typeface customFont = FontCache.getTypeface("IRANSansMobile(FaNum)_Bold.ttf", getActivity());
        domainEditText.setTypeface(customFont);
        domainEditText.setAdapter(adapter);
        domainEditText.setThreshold(3);

        //domainEditText.setAdapter(adapter);
        //domainEditText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        //domainEditText.setThreshold(3);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOnline(getActivity())) {
                    domainEditText = CommonUtil.AutoCompleteFarsiNumberReplacement(domainEditText);
                    globalDomain.setDomain(domainEditText.getText().toString());

                    new ASync().execute();
                } else {
                    Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.label_check_network), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,getActivity());
                    toast.show();
                }


            }
        });


        domainEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
              /*  if (!s.toString().startsWith("https://")) {
                    domainEditText.setText("https://");
                    Selection.setSelection(domainEditText.getText(), domainEditText.getText().length());

                }*/

                final Handler handler = new Handler();
                if (domainEditText.getText().toString().equals("tehran") || domainEditText.getText().toString().equals("تهران")) {
                    String str = "https://bis.tehran.ir";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("isfahan") || domainEditText.getText().toString().equals("اصفهان")) {
                    String str = "http://bis.isfahanptc.ir";
                    detectDomain(handler, str);

                }else if (domainEditText.getText().toString().equals("qazvin") || domainEditText.getText().toString().equals("قزوین")) {
                    String str = "http://78.38.56.19";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("31.24.233.169")) {
                    String str = "https://bis.tehran.ir";
                    detectDomain(handler, str);

                }else if (domainEditText.getText().toString().equals("78.38.56.19")) {
                    String str = "http://78.38.56.19";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("172.22.226.28")) {
                    String str = "http://bis.isfahanptc.ir";
                    detectDomain(handler, str);
                }

                domainEditText.setSelection(domainEditText.getText().length());

            }
        });


        return view;
    }

    private void detectDomain(Handler handler, final String s) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, getActivity().getResources().getString(R.string.label_progress_dialog), true);
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                domainEditText.setText(s);
                dialog.dismiss();
            }
        }, 2000);
    }

    private void sendAndRequestResponse() {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(getActivity());

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place, new RegistrationFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG, "Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressBar = ProgressDialog.show(getActivity(), null, getActivity().getResources().getString(R.string.label_progress_dialog), true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);

                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {

                        System.out.println("result===" + result);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Date serverDateTime = null;
                        try {
                            serverDateTime = simpleDateFormat.parse(jsonObject.getString("serverDateTime"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (DateUtils.isValidDateDiff(new Date(), serverDateTime, Constants.VALID_SERVER_AND_DEVICE_TIME_DIFF)) {
                            DeviceSetting deviceSetting = coreService.getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
                            if (deviceSetting == null) {
                                deviceSetting = new DeviceSetting();
                                deviceSetting.setKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
                            }
                            deviceSetting.setValue(simpleDateFormat.format(new Date()));
                            deviceSetting.setDateLastChange(new Date());
                            coreService.saveOrUpdateDeviceSetting(deviceSetting);

                            SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                            editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, domainEditText.getText().toString());
                            editor.apply();

                            Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.label_ActivationFragment_phoneNumber), Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
                            showRegistrationFragmentPage();

                        } else {
                            errorMsg = getResources().getString(R.string.Invalid_Device_Date_Time);
                            Toast toast = Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
                        }

                    }

                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_enter_domain), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,getActivity());
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getActivity(), (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,getActivity());
                toast.show();
            }
            // progressBar.setVisibility(View.GONE);
            progressBar.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONObject jsonObjectParam = new JSONObject();
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
            try {
                result = postJsonService.sendData("getServerDateTime", jsonObjectParam, true);
            } catch (WebServiceException e) {
                errorMsg = getResources().getString(R.string.Some_error_enter_domain);
            } catch (SocketException e) {
                errorMsg = getResources().getString(R.string.Some_error_enter_domain);
            } catch (SocketTimeoutException e) {
                errorMsg = getResources().getString(R.string.Some_error_enter_domain);
            }
            return null;
        }

        public void showRegistrationFragmentPage() {
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_place, new RegistrationFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    public boolean isOnline(Context context) {
        boolean isOnline = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            isOnline = (netInfo != null && netInfo.isConnected());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isOnline;
    }

   /* public static boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) MbridgeApp.getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }*/
}
