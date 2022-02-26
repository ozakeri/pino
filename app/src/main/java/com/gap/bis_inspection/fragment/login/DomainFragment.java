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
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.GlobalDomain;
import com.gap.bis_inspection.util.FontCache;
import com.gap.bis_inspection.util.volly.VollyService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.text.ParseException;

public class DomainFragment extends Fragment {

    AutoCompleteTextView domainEditText;
    private IDatabaseManager databaseManager;
    private GlobalDomain globalDomain = GlobalDomain.getInstance();
    private String url = "http://bis.isfahanptc.ir/rfServices/getServerDateTime";
    private String[] city = {"tehran", "isfahan", "qazvin", "اصفهان", "قزوین", "تهران"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_domain, container, false);

        domainEditText = (AutoCompleteTextView) view.findViewById(R.id.domain_txt);
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);
        databaseManager = new DatabaseManager(getActivity());

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, null);
        editor.apply();

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, city);

        Typeface customFont = FontCache.getTypeface("IRANSansMobile(FaNum)_Bold.ttf", getActivity());
        domainEditText.setTypeface(customFont);
        domainEditText.setAdapter(adapter);
        domainEditText.setThreshold(3);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CommonUtil.isConnect(getActivity())) {
                    domainEditText = CommonUtil.AutoCompleteFarsiNumberReplacement(domainEditText);
                    globalDomain.setDomain(domainEditText.getText().toString());

                    VollyService.getInstance().getServerDateTime(new Response.Listener<Object>() {
                        @Override
                        public void onResponse(Object s) {
                            try {
                                if (!CommonUtil.isDeviceDateTimeValid(String.valueOf(s))) {
                                    Snackbar.make(view, R.string.Invalid_Device_Date_Time, Snackbar.LENGTH_SHORT)
                                            .show();
                                } else {
                                    showRegistrationFragmentPage();
                                }


                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Snackbar.make(view, volleyError.toString(), Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    });

                } else {
                    Snackbar.make(view, R.string.label_check_network, Snackbar.LENGTH_SHORT)
                            .show();
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

                final Handler handler = new Handler();
                if (domainEditText.getText().toString().equals("tehran") || domainEditText.getText().toString().equals("تهران")) {
                    String str = "https://bis.tehran.ir";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("isfahan") || domainEditText.getText().toString().equals("اصفهان")) {
                    String str = "http://bis.isfahanptc.ir";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("qazvin") || domainEditText.getText().toString().equals("قزوین")) {
                    String str = "http://78.38.56.19";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("31.24.233.169")) {
                    String str = "https://bis.tehran.ir";
                    detectDomain(handler, str);

                } else if (domainEditText.getText().toString().equals("78.38.56.19")) {
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

    public void showRegistrationFragmentPage() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, new RegistrationFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
