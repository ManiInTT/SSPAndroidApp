package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
 * Class Name   :   PINRequest
 * Description  :   PINRequest Screen
 * Created at   :   2018-03-28
 * Updated at   :   2018-03-28
 *******************************************************************************/
public class PINRequest extends BaseActivity implements View.OnClickListener {
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    Util util;
    EditText emailAddress, mobileNumber;
    TextInputLayout emailAddressLayout, mobileNumberLayout;
    int requestFlag = 1;
    TextView requestText;
    String pageRequestFlag = "";
    String userImeiNumber;
    WebServiceUtil userPinRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_request);
        registerBaseActivityReceiver();
        getWidgetConfig();

    }

    /************************************************************************************
     * Class      : PINRequest
     * Use        : Method call widget configure
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        userPinRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
        findViewById(R.id.btn_request_bin).setOnClickListener(this);
        emailAddress = findViewById(R.id.et_email_address);
        mobileNumber = findViewById(R.id.et_mobile_number);
        requestText = findViewById(R.id.tv_request_text);
        emailAddressLayout = (TextInputLayout) findViewById(R.id.tl_email_address);
        mobileNumberLayout = (TextInputLayout) findViewById(R.id.tl_mobile_number);
        emailAddressLayout.setVisibility(View.GONE);
        //set tool bar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_pin_request));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        requestText.setText(R.string.reset_password);
        userImeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
        Bundle extras = getIntent().getExtras();
        pageRequestFlag = extras.getString("pageRequestFlag");

    }

    /************************************************************************************
     * Class      : PINRequest
     * Use        : Method call onclick function
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_bin:
                if (validateForm()) {
                    serviceRequest();
                }
                break;
        }
    }

    /************************************************************************************
     * Class      : PINRequest
     * Use        : Method call validate the form
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    private boolean validateForm() {
        boolean validateFlag = true;
        if (requestFlag == 1) {
            if (!util.validateMobileNumber(this, mobileNumberLayout, mobileNumber)) {
                validateFlag = false;
            }
        } else {
            if (!util.validateEmail(this, emailAddressLayout, emailAddress)) {
                validateFlag = false;
            }
        }

        return validateFlag;
    }

    /************************************************************************************
     * Class      : PINRequest
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

    /************************************************************************************
     * Class      : PINRequest
     * Use        : Method call pin request method option click
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    public void pinRequestOptionClickEvent(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.sms:
                if (checked)
                    requestFlag = 1;
                emailAddressLayout.setVisibility(View.GONE);
                mobileNumberLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.email:
                if (checked)
                    requestFlag = 2;
                emailAddressLayout.setVisibility(View.VISIBLE);
                mobileNumberLayout.setVisibility(View.GONE);
                break;
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
            new PINRequest.userForgetPasswordAsync().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(PINRequest.this, R.color.colorPrimary));
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

    }

    /************************************************************************************
     * Class      : PINRequest
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class userForgetPasswordAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(PINRequest.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.userForgetPassword(emailAddress.getText().toString().trim(), userImeiNumber, getApplicationContext());
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
        try {
            JSONObject jsonObject = new JSONObject(response);
            int mCode = jsonObject.getInt(userPinRequest.statusCode);
            if (mCode == userPinRequest.codeSuccess) {
                String data = jsonObject.getString(userPinRequest.data);
                JSONObject responseJSONObject = new JSONObject(data);
                closeAllActivities();
                PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_EMAIL, emailAddress.getText().toString());
                Intent intent = new Intent(this, PINConfirmation.class);
                intent.putExtra("pin", responseJSONObject.getString(userPinRequest.USER_PIN));
                intent.putExtra("pageRequestFlag", pageRequestFlag);
                startActivityForResult(intent, 2);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }
}
