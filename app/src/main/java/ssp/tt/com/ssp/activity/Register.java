package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

/*******************************************************************************
 * Class Name   :   Register
 * Description  :   Show Registration Screen
 * Created at   :   2018-03-23
 * Updated at   :   2018-03-23
 *******************************************************************************/
public class Register extends BaseActivity implements View.OnClickListener, ServiceConnector.ServiceCallBack {
    Util util;
    ServiceConnector serviceConnector;
    LinearLayout linearLayout;
    ProgressDialog dialog;
    WebServiceUtil userRegisterRequest;
    EditText emailAddress, mobileNumber;
    TextInputLayout emailAddressLayout, mobileNumberLayout;
    TelephonyManager telephonyManager;
    String imeiNumber, userEmail, userMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerBaseActivityReceiver();
        getWidgetConfig();

    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call widget configure
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        userRegisterRequest = new WebServiceUtil();
        linearLayout = findViewById(R.id.linearLayout);
        findViewById(R.id.btn_register).setOnClickListener(this);
        emailAddress = findViewById(R.id.et_email_address);
        mobileNumber = findViewById(R.id.et_mobile_number);
        emailAddressLayout = (TextInputLayout) findViewById(R.id.tl_email_address);
        mobileNumberLayout = (TextInputLayout) findViewById(R.id.tl_mobile_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_register));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call onclick function
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                util.keyboardDisable(this, view);
                boolean validateFlag = validateForm();
                if (validateFlag) {
                    serviceRequest();
                }
                break;
        }
    }


    /************************************************************************************
     * Class      : Register
     * Use        : Method call validate the form
     * Created on : 2018-04-23
     * Updated on : 2018-04-23
     **************************************************************************************/
    private boolean validateForm() {
        if (!util.validateRegister(this, emailAddressLayout, emailAddress, mobileNumberLayout, mobileNumber)) {
            return false;
        } else if (!util.validateEmail(this, emailAddressLayout, emailAddress)) {
            return false;
        } else if (!util.validateMobileNumber(this, mobileNumberLayout, mobileNumber)) {
            return false;
        }
        return true;
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
            new UserRegisterAsync().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(Register.this, R.color.colorPrimary));

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
    private class UserRegisterAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Register.this, getString(R.string.loading_message));
        }

        @SuppressLint("MissingPermission")
        protected Void doInBackground(String... params) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String macId = telephonyManager.getDeviceId();
            try {
                imeiNumber = macId;
                userEmail = emailAddress.getText().toString().trim();
                userMobile = mobileNumber.getText().toString().trim();
                serviceConnector.userRegister(userEmail, userMobile, util.convertMd5(imeiNumber), getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
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
    public void callbackReturn(String response) {
        dialog.dismiss();
        WebServiceUtil webServiceUtil = new WebServiceUtil();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(userRegisterRequest.statusCode);
            if (mCode == 200) {
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                //PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_ID, (responseJSONObject.getString(userRegisterRequest.PIN_USER_ID)));
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, util.convertMd5(imeiNumber));
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_EMAIL, userEmail);
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_MOBILE, userMobile);
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alert_dialog_success);
                dialog.setCancelable(false);
                TextView tvTitle = dialog.findViewById(R.id.tv_title);
                TextView tvMessage = dialog.findViewById(R.id.tv_message);

                tvMessage.setText(description);
                tvTitle.setText(title);
                TextView noBtn = dialog.findViewById(R.id.no_btn);
                noBtn.setText("Verify Now");
                noBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        closeAllActivities();
                        Intent intent = new Intent(Register.this, PINConfirmation.class);
                        intent.putExtra("pageRequestFlag", userRegisterRequest.RESET_PASSWORD);
                        startActivity(intent);
                        finish();

                    }
                });
                dialog.show();


            } else {

                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call move to previous page
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}