package com.gap.bis_inspection.util.volly;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GsonRequest<T> extends Request<T> {

    private Gson gson = new GsonBuilder().serializeNulls().create();
    private Class<T> clazz;
    private Map<String, String> headers;
    private Response.Listener<T> listener;
    private boolean isAuth;
    private String mRequestBody;

    public GsonRequest(int method,
                       String url,
                       Class<T> clazz,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener,
                       boolean isAuth) {

        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.isAuth = isAuth;

        setRetryPolicy(new DefaultRetryPolicy(
                Constants.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
    }

    public void setRequestBody(String requestBody) {

        mRequestBody = requestBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};


        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        if (isAuth)
        {
            headers = new HashMap<>();
            String token = AppController.getInstance().getSharedPreferences().getString(Constants.PREF_TOKEN, "");
            headers.put("Authorization", "Bearer " + token);
        }
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        if(listener != null)
            listener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException uee) {
            return null;
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {


        try {
            NetworkResponse response = volleyError.networkResponse;
            if (response == null)
            {
                return new CustomError(null, new ErrorResponseBean());
            }


            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));


            ErrorResponseBean errorResponseBean = gson.fromJson(json, ErrorResponseBean.class);
            return new CustomError(response, errorResponseBean);

        } catch (Exception e1) {
        }
        return volleyError;

    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {


            if (clazz != null)
            {
                String json = new String(
                        response.data,
                        HttpHeaderParser.parseCharset(response.headers));

                try {
                    ErrorResponseBean errorResponseBean = gson.fromJson(json, ErrorResponseBean.class);
                    if (errorResponseBean != null && errorResponseBean.getERROR() != null)
                        return Response.error(new CustomError(response, errorResponseBean));
                }
                catch (Exception e2)
                {

                }

                try {
                    JSONArray jsonArray = new JSONArray(json);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("content", jsonArray);
                    json = jsonObject.toString();
                }
                catch (Exception ignored)
                {

                }

                return Response.success(
                        gson.fromJson(json, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
            else
            {
                return (Response<T>) Response.success(
                        response.data,
                        HttpHeaderParser.parseCacheHeaders(response));
            }

        } catch (Exception e) {

            return Response.error(new VolleyError(e));
        }

    }
}