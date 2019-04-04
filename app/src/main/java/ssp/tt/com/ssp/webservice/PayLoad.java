package ssp.tt.com.ssp.webservice;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;

public class PayLoad {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static WebServiceUtil serviceUtil = new WebServiceUtil();

    public static JSONObject getErrorJSON() {
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put(serviceUtil.code, serviceUtil.codeSuccess);
            jsonResponse.put(serviceUtil.message, serviceUtil.messageSuccess);
        } catch (JSONException mJSONException) {
            Log.i("getSetting JSON", mJSONException.getMessage());
        }
        return jsonResponse;
    }

}