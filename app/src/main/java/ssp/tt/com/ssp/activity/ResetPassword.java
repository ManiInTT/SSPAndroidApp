package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

/*******************************************************************************
 * Class Name   :   ResetPassword
 * Description  :   Show ResetPassword Screen
 * Created at   :   2018-03-28
 * Updated at   :   2018-03-28
 *******************************************************************************/
public class ResetPassword extends BaseActivity implements View.OnClickListener {
    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    String userPin, userEmail, userImeiNumber, pageRequestFlag;
    TextInputLayout passwordLayout, confirmPasswordLayout;
    EditText password, confirmPassword;
    WebServiceUtil resetPasswordRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        registerBaseActivityReceiver();
        getWidgetConfig();
    }

    /************************************************************************************
     * Class      : ResetPassword
     * Use        : Method call widget configure
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        resetPasswordRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_rest_password));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.btn_send).setOnClickListener(this);
        passwordLayout = (TextInputLayout) findViewById(R.id.tl_password);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.tl_confirm_password);
        password = (EditText) findViewById(R.id.et_password);
        confirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        userImeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
        userEmail = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_EMAIL, "");

    }

    /************************************************************************************
     * Class      : ResetPassword
     * Use        : Method call onclick function
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (validateForm()) {
                    serviceRequest();
                }
                break;
        }
    }

    /************************************************************************************
     * Class      : ResetPassword
     * Use        : Method call validate the form
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    private boolean validateForm() {

        if (!util.validateResetPassword(this, passwordLayout, password, confirmPasswordLayout, confirmPassword)) {
            passwordLayout.setError(getString(R.string.err_msg_password));
        }

        if (!util.validatePassword(this, passwordLayout, password)) {
            passwordLayout.setError(getString(R.string.err_msg_password));
            return false;
        } else if (!util.validateConfirmPassword(this, confirmPasswordLayout, password, confirmPassword)) {
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
            new ResetPassword.resetPasswordAsync().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(ResetPassword.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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
    private class resetPasswordAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(ResetPassword.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userPassword = password.getText().toString().trim();
                serviceConnector.userResetPassword(userEmail, userImeiNumber, userPassword, getApplicationContext());
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
        final WebServiceUtil resetPasswordRequest = new WebServiceUtil();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(resetPasswordRequest.statusCode);
            String mMessage = mJSONObject.getString(resetPasswordRequest.desc);

            if (mCode == resetPasswordRequest.codeSuccess) {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
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
                        Intent intent = new Intent(ResetPassword.this, Login.class);
                        Bundle extras = getIntent().getExtras();
                        pageRequestFlag = extras.getString("pageRequestFlag");
                        if (pageRequestFlag.equals(resetPasswordRequest.FORGET_PASSWORD) || pageRequestFlag.equals(resetPasswordRequest.CHANGE_PASSWORD)) {
                            intent.putExtra("pageRequestFlag", pageRequestFlag);
                        } else {
                            intent.putExtra("pageRequestFlag", resetPasswordRequest.REGISTER);
                        }
                        startActivity(intent);
                        finish();

                    }
                });
                dialog.show();
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
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
     * Class      : Create Password
     * Use        : Method call move to previous page
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
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
