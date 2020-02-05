package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
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
 * Class Name   :   PINConfirmation
 * Description  :   PINConfirmation  Screen
 * Created at   :   2018-03-28
 * Updated at   :   2018-03-28
 *******************************************************************************/
public class PINConfirmation extends BaseActivity implements View.OnClickListener {
    Util util;
    TextInputLayout pinLayout;
    ServiceConnector serviceConnector;
    EditText pin;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    String userPin, userEmail, userImeiNumber, pageRequestFlag;
    WebServiceUtil userPinConfirmationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_confirmation);
        getWidgetConfig();
        registerBaseActivityReceiver();
    }

    /************************************************************************************
     * Class      : PINConfirmation
     * Use        : Method call widget configure
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        userPinConfirmationRequest = new WebServiceUtil();
        linearLayout = findViewById(R.id.linearLayout);
        findViewById(R.id.btn_next).setOnClickListener(this);
        pinLayout = (TextInputLayout) findViewById(R.id.tl_pin);
        pin = (EditText) findViewById(R.id.et_pin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_pin_confirmation));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
        userImeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
        Bundle extras = getIntent().getExtras();
        pageRequestFlag = extras.getString("pageRequestFlag");
        userEmail = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_EMAIL, "");
        pin.setText(userPin);

    }

    /************************************************************************************
     * Class      : PINConfirmation
     * Use        : Method call onclick function
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (validateForm()) {
                    serviceRequest();
                }
                break;
        }
    }

    /************************************************************************************
     * Class      : PINConfirmation
     * Use        : Method call validate the form
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    private boolean validateForm() {
        if (!util.validatePin(this, pinLayout, pin)) {
            return false;
        }
        return true;
    }

    /************************************************************************************
     * Class      : serviceRequest
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new PINConfirmation.pinConfirmationAsync().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(PINConfirmation.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }

    @Override
    public void callbackReturn(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int mCode = jsonObject.getInt(userPinConfirmationRequest.statusCode);
            if (mCode == userPinConfirmationRequest.codeSuccess) {
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.REGISTER_STATUS, "1");
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alert_dialog_success);
                TextView tvTitle = dialog.findViewById(R.id.tv_title);
                TextView tvMessage = dialog.findViewById(R.id.tv_message);
                tvMessage.setText(description);
                tvTitle.setText(title);
                TextView noBtn = dialog.findViewById(R.id.no_btn);
                noBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        closeAllActivities();
                        Intent intent = new Intent(PINConfirmation.this, ResetPassword.class);
                        intent.putExtra("pageRequestFlag", pageRequestFlag);
                        startActivity(intent);
                        finish();

                    }
                });
                dialog.show();
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class pinConfirmationAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(PINConfirmation.this, getString(R.string.loading_message));
        }

        @SuppressLint("MissingPermission")
        protected Void doInBackground(String... params) {

            try {
                userPin = pin.getText().toString().trim();
                if (pageRequestFlag.equals(userPinConfirmationRequest.FORGET_PASSWORD) || pageRequestFlag.equals(userPinConfirmationRequest.CHANGE_PASSWORD)) {
                    serviceConnector.userPinConfirmation(userEmail, userPin, userImeiNumber, "1", getApplicationContext());
                } else {
                    serviceConnector.userPinConfirmation(userEmail, userPin, userImeiNumber, "", getApplicationContext());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }
}
