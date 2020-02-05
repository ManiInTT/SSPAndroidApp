package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class SecuritySetting extends BaseActivity implements View.OnClickListener {
    Util util;
    TextInputLayout ansLayout;
    EditText secAnswer;
    Spinner secretQuestionList;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    WebServiceUtil apiRequest;
    int RequestFlag = 1;
    String questionId = "";
    HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setting);
        registerBaseActivityReceiver();
        getWidgetConfig();
        serviceRequest();
    }

    /************************************************************************************
     * Class      : SecuritySetting
     * Use        : Method call widget configure
     * Created on : 2018-04-13
     * Updated on :  2018-04-13
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        apiRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
        findViewById(R.id.btn_update).setOnClickListener(this);

        ansLayout = (TextInputLayout) findViewById(R.id.tl_ans);
        secAnswer = (EditText) findViewById(R.id.et_answer);
        secretQuestionList = (Spinner) findViewById(R.id.sp_secret_question);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_security_setting));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);
        secretQuestionList.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        String question = spinnerMap.get(secretQuestionList.getSelectedItemPosition());
                        try {
                            if (position > 0) {
                                JSONObject questionJsonObject = new JSONObject(question.toString());
                                questionId = questionJsonObject.getString("sq_id");
                            } else {
                                questionId = "";

                            }

                        } catch (JSONException mException) {

                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

    }

    /************************************************************************************
     * Class      : SecuritySetting
     * Use        : Method call onclick function
     * Created on :  2018-04-13
     * Updated on : 2018-04-13
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                RequestFlag = 2;
                if (validateForm()) {
                    serviceRequest();

                }

        }
    }

    /************************************************************************************
     * Class      : SecuritySetting
     * Use        : Method call validate the form
     * Created on :  2018-04-13
     * Updated on : 2018-04-13
     **************************************************************************************/

    private boolean validateForm() {
        if (questionId == "" || questionId.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please Select Secret Question", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!util.validateSecret(this, ansLayout, secAnswer)) {
            ansLayout.setError(getString(R.string.err_msg_sec_answer));
            return false;
        }


        return true;
    }

    /************************************************************************************
     * Class      : saveProfile
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class getSecuritySettings extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(SecuritySetting.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.getSecuritySettings(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : saveSecuritySettings
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class saveSecuritySettings extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(SecuritySetting.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.saveSecuritySettings(getApplicationContext(), UserId, imeiNumber, questionId, secAnswer.getText().toString());
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

            System.out.println("save Security Question test" + response);
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(apiRequest.statusCode);
            if (mCode == apiRequest.codeSuccess) {
                if (RequestFlag == 1) {
                    String data = mJSONObject.getString(apiRequest.data);
                    JSONArray questionArray = new JSONArray(data);
                    String security_questions[] = new String[questionArray.length() + 1];
                    spinnerMap.put(0, "");
                    security_questions[0] = "Select";
                    for (int qIndex = 0; qIndex < questionArray.length(); qIndex++) {
                        spinnerMap.put(qIndex + 1, new JSONObject(questionArray.get(qIndex).toString()).toString());
                        security_questions[qIndex + 1] = new JSONObject(questionArray.get(qIndex).toString()).getString("sq_text");
                    }
                    ArrayAdapter<String> securityQuestions = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, security_questions);
                    secretQuestionList.setAdapter(securityQuestions);
                } else {
                    startActivity(new Intent(this, Dashboard.class));
                    finish();
                }
            }
        } catch (JSONException mException) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
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
            if (RequestFlag == 1) {
                new SecuritySetting.getSecuritySettings().execute();
            } else {

                new SecuritySetting.saveSecuritySettings().execute();
            }
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
            group.setBackgroundColor(ContextCompat.getColor(SecuritySetting.this, R.color.colorPrimary));
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }
}
