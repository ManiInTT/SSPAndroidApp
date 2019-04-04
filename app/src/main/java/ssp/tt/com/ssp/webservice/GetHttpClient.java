package ssp.tt.com.ssp.webservice;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.Field;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class GetHttpClient {


    @SuppressLint("TrustAllX509TrustManager")
    public static okhttp3.OkHttpClient getClient() {

        okhttp3.OkHttpClient mOkHttpClient = new okhttp3.OkHttpClient();
        SSLContext mSSLContext = null;
        try {
            mSSLContext = SSLContext.getInstance("SSL");
            mSSLContext.init(null, new TrustManager[]{new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception mException) {
            Log.i("Exception", mException.getMessage());
        }

        HostnameVerifier mHostnameVerifier = new HostnameVerifier() {
            @SuppressLint("BadHostnameVerifier")
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        String workerClassName = "okhttp3.OkHttpClient";
        try {
            Class workerClass = Class.forName(workerClassName);
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(mOkHttpClient, mHostnameVerifier);

            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            if (mSSLContext != null) {
                sslSocketFactory.set(mOkHttpClient, mSSLContext.getSocketFactory());
            }
        } catch (Exception mException) {
            Log.i("Exception", mException.getMessage());
        }
        return mOkHttpClient;
    }
}
