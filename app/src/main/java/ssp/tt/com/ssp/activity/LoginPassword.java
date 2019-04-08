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

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

/*******************************************************************************
 * Class Name   :   LoginPassword
 * Description  :   Login Password Screen
 * Created at   :   2018-03-23
 * Updated at   :   2018-03-23
 *******************************************************************************/
public class LoginPassword extends BaseActivity implements View.OnClickListener {
    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    TextInputLayout passwordLayout;
    EditText password;
    String userImeiNumber, userId, pageRequestFlag;
    WebServiceUtil userLoginPasswordRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        registerBaseActivityReceiver();
        getWidgetConfig();
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
        serviceConnector.registerCallback(this);
        userLoginPasswordRequest = new WebServiceUtil();
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_forgot_password).setOnClickListener(this);

        passwordLayout = (TextInputLayout) findViewById(R.id.tl_password);
        password = (EditText) findViewById(R.id.et_password);
        util = new Util();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_login));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userImeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
        userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
        Bundle extras = getIntent().getExtras();
        pageRequestFlag = extras.getString("pageRequestFlag");
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

    }

    /************************************************************************************
     * Class      : LoginPassword
     * Use        : Method call onclick function
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (validateForm()) {
                    serviceRequest();
                }
                break;
            case R.id.tv_forgot_password:
                util.keyboardDisable(this, view);
                Intent intent = new Intent(this, PINRequest.class);
                intent.putExtra("pageRequestFlag", "FORGET_PASSWORD");
                startActivityForResult(intent, 1);
                finish();


        }
    }

    /************************************************************************************
     * Class      : LoginPassword
     * Use        : Method call validate the form
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private boolean validateForm() {
        if (!util.validatePassword(this, passwordLayout, password)) {
            return false;
        }
        return true;
    }

    /************************************************************************************
     * Class      : LoginPassword
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

    /************************************************************************************
     * Class      : LoginPassword
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new LoginPassword.userLoginPasswordAsync().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(LoginPassword.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }

    /************************************************************************************
     * Class      : LoginPassword
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class userLoginPasswordAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(LoginPassword.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.userLoginPassword(password.getText().toString().trim(), userImeiNumber, userId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : LoginPassword
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @Override
    public void callbackReturn(String response) {
        dialog.dismiss();

        try {
            JSONObject jsonObject = new JSONObject(response);
            int mCode = jsonObject.getInt(userLoginPasswordRequest.statusCode);
            if (mCode == userLoginPasswordRequest.codeSuccess) {
                String data = jsonObject.getString(userLoginPasswordRequest.data);
                JSONObject responseJSONObject = new JSONObject(data);
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.SESSION_TOKEN, responseJSONObject.getString(userLoginPasswordRequest.SESSION_TOKEN));
                String firstname = responseJSONObject.getString(userLoginPasswordRequest.user_first_name);

                if (firstname == null || firstname.equals("null")) {
                    Intent intent = new Intent(this, Profile.class);
                    intent.putExtra("pageRequestFlag", "LOGIN");
                    startActivityForResult(intent, 1);
                    finish();
                    closeAllActivities();
                } else {
                    final JSONObject jsonObjectDesc = jsonObject.getJSONObject(userLoginPasswordRequest.desc);
                    String type = jsonObjectDesc.getString(userLoginPasswordRequest.type);
                    String title = jsonObjectDesc.getString(userLoginPasswordRequest.title);
                    String description = jsonObjectDesc.getString(userLoginPasswordRequest.description);
                    if (pageRequestFlag.equals(userLoginPasswordRequest.LOGIN) || pageRequestFlag.equals(userLoginPasswordRequest.FORGET_PASSWORD) || pageRequestFlag.equals(userLoginPasswordRequest.CHANGE_PASSWORD)) {
               /*         final Dialog dialog = new Dialog(this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.alert_dialog_warning);
                        TextView tvTitle = dialog.findViewById(R.id.tv_title);
                        TextView tvMessage = dialog.findViewById(R.id.tv_message);
                        tvMessage.setText(description);
                        tvTitle.setText(title);
                        TextView noBtn = dialog.findViewById(R.id.no_btn);
                        noBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {*/
                                dialog.dismiss();
                                Intent intent = new Intent(LoginPassword.this, Dashboard.class);
                                startActivity(intent);
                                finish();
                                closeAllActivities();

                        /*    }
                        });
                        dialog.show();*/


                    } else {
                        Intent intent = new Intent(this, Profile.class);
                        intent.putExtra("pageRequestFlag", "LOGIN");
                        startActivityForResult(intent, 1);
                        finish();
                        closeAllActivities();
                    }
                }

            } else {
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(userLoginPasswordRequest.desc);
                String type = jsonObjectDesc.getString(userLoginPasswordRequest.type);
                String title = jsonObjectDesc.getString(userLoginPasswordRequest.title);
                String description = jsonObjectDesc.getString(userLoginPasswordRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.getMessage());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}
