package com.gap.bis_inspection.webservice;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.gap.bis_inspection.encrypt.MCrypt;
import com.gap.bis_inspection.exception.WebServiceException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class PostJsonService {

    public String PARAMETER_ATTRIBUTES = "INPUT_PARAM";
    public String PARAMETER_IS_ENCRYPED = "IS_ENCRYPED";
    public String baseServiceUrl;

    public PostJsonService() {
        // TODO Auto-generated constructor stub
    }

    public PostJsonService(String baseServiceUrl) {
        this.baseServiceUrl = baseServiceUrl;
    }

    String output = "";
    String key;

    @SuppressLint("NewApi")
    public String sendData(String serviceName, JSONObject json, boolean doEncryption) throws WebServiceException {
        String url = null;
        if (baseServiceUrl != null) {
            url = baseServiceUrl + serviceName;
        } else {
            url = serviceName;
        }

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        MCrypt mcrypt = new MCrypt();
        String sentParameters = "";
        try {
            if (doEncryption) {
                sentParameters = MCrypt.bytesToHex(mcrypt.encrypt(json.toString()));
                Log.e("JSON:", json.toString());
                Log.e("Decrpted JSON", sentParameters);
            } else {
                sentParameters = json.toString();
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nameValuePairs.add(new BasicNameValuePair(PARAMETER_ATTRIBUTES, sentParameters));
        nameValuePairs.add(new BasicNameValuePair(PARAMETER_IS_ENCRYPED, Boolean.valueOf(doEncryption).toString()));

        try {
            // post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));


        } catch (UnsupportedEncodingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        HttpResponse response;
        try {
            response = client.execute(post);

            if (response != null) {
                InputStream is = response.getEntity().getContent();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String i = "";
                i = sb.toString();

                try {
                    Log.e("JSON Respond:", sb.toString());
                    if (doEncryption) {
                        output = new String(mcrypt.decrypt(sb.toString()));
                        Log.e("JSON Decrpted:", output);
                    } else {
                        output = sb.toString();
                    }
                    // Security sec=new Security("D4:6E:AC:3F:F0:BE");
                    // decrypted = new String(sec.(sb.toString()));
                } catch (Exception e) {
                    Log.d("----111=" + e.getMessage(), "----222=" + e.getMessage());
                    throw new WebServiceException(e.getMessage(), e);
                }

            }

        } catch (ClientProtocolException e) {
            Log.d(e.getMessage(), e.getMessage());
            throw new WebServiceException(e.getMessage(), e);
        } catch (IOException e) {
            Log.d(e.getMessage(), e.getMessage());
            throw new WebServiceException(e.getMessage(), e);
        }

        return output;
    }

}
