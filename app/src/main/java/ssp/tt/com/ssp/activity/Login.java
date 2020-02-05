package ssp.tt.com.ssp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.sheet.APIConfigureFragment;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.SimpleThreeFingerDoubleTapDetector;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

import static android.Manifest.permission.READ_PHONE_STATE;

/*******************************************************************************
 * Class Name   :   Login
 * Description  :   Login  Screen
 * Created at   :   2018-03-23
 * Updated at   :   2018-03-23
 *******************************************************************************/
public class Login extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_READ_PHONE_STATE = 11;
    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    String userImeiNumber;
    TextInputLayout userNameLayout;
    EditText userName;
    boolean isPermissionPhoneState;
    String pageRequestFlag;
    // Runtime Permission
    WebServiceUtil userLoginRequest;
    public static final int RequestPermissionCode = 1;
    String userId = "0";


    private String API_TYPE = "";
    private String API_LOGIN = "API_LOGIN";
    private String API_UPDATE_IMEI = "API_UPDATE_IMEI";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerBaseActivityReceiver();
        getWidgetConfig();
        getIMEINumber();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        } else {
            isPermissionPhoneState = true;
        }
        // Detect touched area

    }

    private void getIMEINumber() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String macId = telephonyManager.getDeviceId();
            userImeiNumber = util.convertMd5(macId);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    isPermissionPhoneState = true;
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    @SuppressLint("MissingPermission") String macId = telephonyManager.getDeviceId();
                    userImeiNumber = util.convertMd5(macId);
                } else {
                    if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {

                        showMessageOKCancel("Please allow to read the phone state",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{READ_PHONE_STATE},
                                                    REQUEST_READ_PHONE_STATE);
                                        }
                                    }
                                });
                        return;
                    }
                }
                break;

            default:
                break;
        }
    }


    /************************************************************************************
     * Class      : Login
     * Use        : Method call widget configure
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        userLoginRequest = new WebServiceUtil();

        serviceConnector.registerCallback(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);
        userNameLayout = findViewById(R.id.tl_user_name);
        userName = findViewById(R.id.et_user_name);
        String userEmail = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_EMAIL, "");
        linearLayout = findViewById(R.id.linearLayout);
        userName.setText(userEmail);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    /************************************************************************************
     * Class      : Login
     * Use        : Method call onclick function
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (validateForm()) {
                    util.keyboardDisable(this, view);
                    if (isPermissionPhoneState) {
                        serviceRequest();
                    } else {
                        checkAndRequestPermissions();
                        Toast.makeText(getApplicationContext(), R.string.permissions_not_granted, Toast.LENGTH_LONG).show();
                    }

                }
                break;
            case R.id.tv_register:
                util.keyboardDisable(this, view);
                if (isPermissionPhoneState) {
                    startActivity(new Intent(this, Register.class));
                    finish();
                } else {
                    checkAndRequestPermissions();
                    Toast.makeText(getApplicationContext(), R.string.permissions_not_granted, Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    /************************************************************************************
     * Class      : Login
     * Use        : Method call validate the form
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private boolean validateForm() {
        if (!util.validateEmail(this, userNameLayout, userName) && !util.validateMobileNumber(this, userNameLayout, userName)) {
            userNameLayout.setError(getString(R.string.err_msg_email_mobile));
            return false;
        }
        return true;
    }

    /************************************************************************************
     * Class      : Login
     * Use        : Method call check And RequestPermissions
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                READ_PHONE_STATE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
            if (ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                isPermissionPhoneState = true;
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), RequestPermissionCode);
        }
    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new userLoginAsync().execute();
        } else {
            Snackbar snackbar;
            snackbar = Snackbar
                    .make(linearLayout, getString(R.string.network_connection_error), Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);

            // Changing snackbar background color
            ViewGroup group = (ViewGroup) snackbar.getView();
            group.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class UpdateIMEINumber extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_UPDATE_IMEI;
            dialog = ProgressUtil.showDialog(Login.this, getString(R.string.loading_message));

        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.UpdateIMEILogin(userId, userImeiNumber, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class userLoginAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Login.this, getString(R.string.loading_message));
            API_TYPE = API_LOGIN;
        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.userLoginEmail(userName.getText().toString().trim(), userImeiNumber, getApplicationContext());
            } catch (Exception e) {
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        if (API_TYPE.equals(API_LOGIN)) {
            getLoginRepsonse(response);
        } else if (API_TYPE.equals(API_UPDATE_IMEI)) {
            getUpdateIMEIResponse(response);
        }
    }

    private void getUpdateIMEIResponse(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int mCode = jsonObject.getInt(userLoginRequest.statusCode);
            if (mCode == userLoginRequest.codeSuccess) {
                String data = jsonObject.getString(userLoginRequest.data);
                JSONObject responseJSONObject = new JSONObject(data);
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, userImeiNumber);
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_ID, (responseJSONObject.getString(userLoginRequest.USER_ID)));
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_EMAIL, userName.getText().toString());
                Intent intent = new Intent(this, LoginPassword.class);
                Bundle extras = getIntent().getExtras();
                pageRequestFlag = extras.getString("pageRequestFlag");
                intent.putExtra("pageRequestFlag", pageRequestFlag);
                startActivity(intent);
            } else {
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(userLoginRequest.desc);
                String type = jsonObjectDesc.getString(userLoginRequest.type);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
        }
    }

    private void getLoginRepsonse(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int mCode = jsonObject.getInt(userLoginRequest.statusCode);
            if (mCode == userLoginRequest.codeSuccess) {
                String data = jsonObject.getString(userLoginRequest.data);
                JSONObject responseJSONObject = new JSONObject(data);
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, userImeiNumber);
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_ID, (responseJSONObject.getString(userLoginRequest.USER_ID)));
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_EMAIL, responseJSONObject.getString("user_email"));


                String firstName = "", lastName = "";


                if (responseJSONObject.has("user_first_name")) {
                    firstName = responseJSONObject.getString("user_first_name");
                }
                if (responseJSONObject.has("user_last_name")) {
                    lastName = responseJSONObject.getString("user_last_name");
                }


                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_NAME, firstName + " " + lastName);
                Intent intent = new Intent(this, LoginPassword.class);
                Bundle extras = getIntent().getExtras();
                pageRequestFlag = extras.getString("pageRequestFlag");
                intent.putExtra("pageRequestFlag", pageRequestFlag);
                startActivity(intent);
            } else {
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(userLoginRequest.desc);
                String type = jsonObjectDesc.getString(userLoginRequest.type);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                if (description.contains("You registered device is different from login device")) {
                    String data = jsonObject.getString(userLoginRequest.data);
                    JSONObject responseJSONObject = new JSONObject(data);
                    userId = responseJSONObject.getString(userLoginRequest.USER_ID);
                    cancelAlertDialog(title, description);
                } else {
                    Util.warningAlertDialog(this, title, description, 0);
                }
            }
        } catch (JSONException mException) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
            Log.i("JSONException", mException.getMessage());
        }
    }


    private void cancelAlertDialog(String title, final String message) {
        final Dialog dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog_error);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        tvTitle.setText(title);
        TextView btnYes = dialog.findViewById(R.id.yes_btn);
        btnYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if (message.contains("You registered device is different from login device")) {
                    new UpdateIMEINumber().execute();
                }
            }
        });
        TextView btnNo = dialog.findViewById(R.id.no_btn);
        btnNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Login.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    SimpleThreeFingerDoubleTapDetector multiTouchListener = new SimpleThreeFingerDoubleTapDetector() {
        @Override
        public void onThreeFingerDoubleTap() {
            APIConfigureFragment fragment = new APIConfigureFragment();
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        multiTouchListener.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        multiTouchListener.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }



}
