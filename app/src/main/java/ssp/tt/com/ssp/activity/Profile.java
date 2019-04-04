package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class Profile extends BaseActivity implements View.OnClickListener {
    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    RelativeLayout linearLayout;
    TextInputLayout firstNameLayout, lastNameLayout, doorLayout, pinCodeLayout, streetLayout, mobileLayout, dateOfBirthLayout, emailLayout, stateLayout, cityLayout, countryLayout;
    EditText dateOfBirth, firstName, lastName, doorNo, street, mobile, pincode, email, country, city, state;
    Button btn_delete;
    private int mYear, mMonth, mDay;
    String userImeiNumber;
    String genderFlag = "1", pageRequestFlag;
    int serviceRequestFlag = 1;
    String countryId = "0";
    String stateId = "0";
    String cityId = "0";
    int addressFlag = 0;
    WebServiceUtil userProfileRequest;
    RadioGroup gender;
    private String API_TYPE = "";
    private String API_RESULT = "API_MYRESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        registerBaseActivityReceiver();
        getWidgetConfig();
        serviceRequest("1");
    }

    /************************************************************************************
     * Class      : Profile
     * Use        : Method call widget configure
     * Created on : 2018-04-09
     * Updated on : 2018-04-09
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        userProfileRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
        findViewById(R.id.et_dob).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        dateOfBirth = (EditText) findViewById(R.id.et_dob);
        firstName = (EditText) findViewById(R.id.et_first_name);
        lastName = (EditText) findViewById(R.id.et_last_name);
        doorNo = (EditText) findViewById(R.id.et_door_no);
        city = (EditText) findViewById(R.id.et_city);
        state = (EditText) findViewById(R.id.et_state);
        country = (EditText) findViewById(R.id.et_country);
        email = (EditText) findViewById(R.id.et_contact_email);
        street = (EditText) findViewById(R.id.et_street);
        mobile = (EditText) findViewById(R.id.et_contact_number);
        pincode = (EditText) findViewById(R.id.et_pin_code);
        gender = (RadioGroup) findViewById(R.id.gender);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        dateOfBirthLayout = (TextInputLayout) findViewById(R.id.tl_dob);
        firstNameLayout = (TextInputLayout) findViewById(R.id.tl_first_name);
        lastNameLayout = (TextInputLayout) findViewById(R.id.tl_last_name);
        doorLayout = (TextInputLayout) findViewById(R.id.tl_door_no);
        emailLayout = (TextInputLayout) findViewById(R.id.tl_contact_email);
        streetLayout = (TextInputLayout) findViewById(R.id.tl_street);
        mobileLayout = (TextInputLayout) findViewById(R.id.tl_contact_number);
        pinCodeLayout = (TextInputLayout) findViewById(R.id.tl_pin_code);
        countryLayout = (TextInputLayout) findViewById(R.id.tl_country);
        stateLayout = (TextInputLayout) findViewById(R.id.tl_state);
        cityLayout = (TextInputLayout) findViewById(R.id.tl_city);
        TextView tvBank = (TextView) findViewById(R.id.et_bank);
        TextView ivBank = (TextView) findViewById(R.id.iv_bank);
        tvBank.setOnClickListener(this);
        ivBank.setOnClickListener(this);
        userImeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);
        Bundle extras = getIntent().getExtras();
        pageRequestFlag = extras.getString("pageRequestFlag");
        String userEmail = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_EMAIL, "");
        email.setText(userEmail);
        String userMobile = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_MOBILE, "");
        mobile.setText(userMobile);


        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("testingggg");
                Intent i = new Intent(Profile.this, CountrySelection.class);
                i.putExtra("stateId", "0");
                i.putExtra("addressFlag", "1");
                startActivityForResult(i, 0);
                //City.setText("");
                cityId = "0";
                stateId = "0";
            }
        });
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("testingggg");
                Intent i = new Intent(Profile.this, CountrySelection.class);
                i.putExtra("countryId", countryId);
                i.putExtra("addressFlag", "2");
                startActivityForResult(i, 1);
                cityId = "0";
            }
        });
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, CountrySelection.class);
                i.putExtra("stateId", stateId);
                i.putExtra("addressFlag", "3");
                startActivityForResult(i, 2);

            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetPresent = mConnectionDetector.isNetworkAvailable();
                if (isInternetPresent) {

                    final Dialog dialog = new Dialog(Profile.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog_error);
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    TextView tvMessage = dialog.findViewById(R.id.tv_message);
                    tvMessage.setText("You cannot access your data if you delete the account. Do you still want to delete your account ?");
                    tvTitle.setText(getResources().getString(R.string.warning));
                    TextView btnYes = dialog.findViewById(R.id.yes_btn);
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            new deleteUser().execute();
                        }
                    });
                    TextView btnNo = dialog.findViewById(R.id.no_btn);
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                } else {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "No Network Connection !", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    btn_delete.performClick();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });
    }


    /************************************************************************************
     * Class      : Profile
     * Use        : Method call onclick function
     * Created on : 2018-04-09
     * Updated on : 2018-04-09
     **************************************************************************************/

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.et_dob:

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -18);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                try {
                                    dateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    SimpleDateFormat format = new SimpleDateFormat("DD-mm-yyyy");
                                    Date date = format.parse(dateOfBirth.getText().toString());
                                    String formattedDate = new SimpleDateFormat("DD/mm/yyyy").format(date);
                                    dateOfBirth.setText(formattedDate);

                                } catch (Exception e) {
                                    Log.e("error", "" + e);
                                }

                            }

                        }, mYear, mMonth, mDay);
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();

                break;

            case R.id.et_country:

                break;

            case R.id.btn_update:
                if (validateForm()) {
                    serviceRequest("2");

                }
                break;
            case R.id.et_bank:
                intent = new Intent(this, UpdateAccount.class);
                intent.putExtra(UpdateAccount.INTENT_TITLE, "PROFILE");
                intent.putExtra(UpdateAccount.INTENT_AMOUNT, "");
                startActivity(intent);
                break;
            case R.id.iv_bank:
                intent = new Intent(this, UpdateAccount.class);
                intent.putExtra(UpdateAccount.INTENT_TITLE, "PROFILE");
                intent.putExtra(UpdateAccount.INTENT_AMOUNT, "");
                startActivity(intent);
                break;

        }
    }

    /************************************************************************************
     * Class      : Profile
     * Use        : Method call gender method option click
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    public void pinRequestOptionClickEvent(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.male:
                if (checked)
                    genderFlag = "1";

                break;
            case R.id.female:
                if (checked)
                    genderFlag = "2";

                break;
            case R.id.other:
                if (checked)
                    genderFlag = "3";

                break;
        }
    }

    /************************************************************************************
     * Class      : Profile
     * Use        : Method call move to previous page
     * Created on : 2018-04-09
     * Updated on : 2018-04-09
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
     * Class      : Profile
     * Use        : Method call validate the form
     * Created on : 2018-04-12
     * Updated on : 2018-04-12
     **************************************************************************************/
    private boolean validateForm() {
        boolean isValidate = true;
        if (!util.validAlphabets(this, firstNameLayout, firstName)) {
            firstNameLayout.setError(getString(R.string.err_msg_first_name));
            isValidate = false;
        }
        if (!util.validAlphabets(this, lastNameLayout, lastName)) {
            lastNameLayout.setError(getString(R.string.err_msg_last_name));
            isValidate = false;
        }
        if (!util.emptyString(this, dateOfBirthLayout, dateOfBirth)) {
            dateOfBirthLayout.setError(getString(R.string.err_msg_dob));
            isValidate = false;
        }
        if (!util.validateMobileNumber(this, mobileLayout, mobile)) {
            mobileLayout.setError(getString(R.string.err_msg_mobile_number));
            isValidate = false;
        }
        if (!util.validateEmail(this, emailLayout, email)) {
            emailLayout.setError(getString(R.string.err_msg_email));
            isValidate = false;
        }
        if (!util.emptyString(this, doorLayout, doorNo)) {
            doorLayout.setError(getString(R.string.err_msg_door_no));
            isValidate = false;
        }
        if (!util.emptyString(this, streetLayout, street)) {
            streetLayout.setError(getString(R.string.err_msg_street));
            isValidate = false;
        }
        if (!util.emptyString(this, pinCodeLayout, pincode)) {
            pinCodeLayout.setError(getString(R.string.err_msg_pin_code));
            isValidate = false;
        }
        if (!util.emptyString(this, countryLayout, country)) {
            countryLayout.setError(getString(R.string.err_msg_country));
            isValidate = false;
        }
        if (!util.emptyString(this, stateLayout, state)) {
            stateLayout.setError(getString(R.string.err_msg_state));
            isValidate = false;
        }
        if (!util.emptyString(this, cityLayout, city)) {
            cityLayout.setError(getString(R.string.err_msg_city));
            isValidate = false;
        }
        return isValidate;
    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    private void serviceRequest(String flag) {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            if (flag.equals("1")) {
                if (pageRequestFlag.equals("UPDATE")) {
                    new viewProfile().execute();
                }
            } else {
                new saveProfile().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(Profile.this, R.color.colorPrimary));
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

    }

    /************************************************************************************
     * Class      : saveProfile
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class saveProfile extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serviceRequestFlag = 4;
            dialog = ProgressUtil.showDialog(Profile.this, getString(R.string.loading_message));
            PreferenceConnector.writeString(Profile.this, PreferenceConnector.USER_NAME, firstName.getText().toString() + " " + lastName.getText().toString());

        }

        protected Void doInBackground(String... params) {

            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.userSaveProfile(userId, firstName.getText().toString().trim(), lastName.getText().toString().trim(),
                        genderFlag, dateOfBirth.getText().toString(), doorNo.getText().toString(), street.getText().toString(), Integer.parseInt(cityId), Integer.parseInt(stateId), Integer.parseInt(countryId), pincode.getText().toString(), userImeiNumber, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : saveProfile
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class viewProfile extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Profile.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.getUserDetails(userId, imeiNumber, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : getCountry
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class getCountry extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addressFlag = 1;
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.getCountryDetails(userId, countryId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : getState
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/


    /************************************************************************************
     * Class      : getCity
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class getCity extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addressFlag = 3;
        }

        protected Void doInBackground(String... params) {
            try {

                serviceConnector.getCityDetails(stateId, cityId, getApplicationContext());
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
        try {
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(userProfileRequest.statusCode);
            String mMessage = mJSONObject.getString(userProfileRequest.desc);
            if (serviceRequestFlag == 4) {
                dialog.dismiss();
                if (mCode == userProfileRequest.codeSuccess) {
                    WebServiceUtil webServiceUtil = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                    String title = jsonObjectDesc.getString(webServiceUtil.title);
                    String description = jsonObjectDesc.getString(webServiceUtil.description);
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog_warning);
                    dialog.setCancelable(false);
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    TextView tvMessage = dialog.findViewById(R.id.tv_message);
                    tvMessage.setText(description);
                    tvTitle.setText(title);
                    TextView noBtn = dialog.findViewById(R.id.no_btn);
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (pageRequestFlag.equals("UPDATE")) {
                                startActivity(new Intent(Profile.this, Dashboard.class));
                                finish();
                            } else {
                                startActivity(new Intent(Profile.this, SecuritySetting.class));
                                finish();
                            }

                        }
                    });
                    dialog.show();
                    Util.warningAlertDialog(this, title, description, 1);
                } else {
                    WebServiceUtil webServiceUtil = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                    String title = jsonObjectDesc.getString(webServiceUtil.title);
                    String description = jsonObjectDesc.getString(webServiceUtil.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }

            } else if (serviceRequestFlag == 1) {
                if (mCode == userProfileRequest.codeSuccess) {
                    try {
                        String data = mJSONObject.getString(userProfileRequest.data);
                        JSONArray userProfileDetails = new JSONArray(data);
                        dateOfBirth.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_dob"));
                        firstName.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_first_name"));
                        lastName.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_last_name"));
                        PreferenceConnector.writeString(this, PreferenceConnector.USER_NAME, firstName.getText().toString() + " " + lastName.getText().toString());
                        doorNo.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_door_no"));
                        pincode.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_pincode"));
                        street.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_street"));

                        PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_EMAIL, new JSONObject(userProfileDetails.get(0).toString()).getString("user_email"));
                        email.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_email"));
                        PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_MOBILE, new JSONObject(userProfileDetails.get(0).toString()).getString("user_mobile"));
                        mobile.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("user_mobile"));


                        if (new JSONObject(userProfileDetails.get(0).toString()).getString("user_gender").equals("1")) {
                            gender.check(R.id.male);
                        } else if (new JSONObject(userProfileDetails.get(0).toString()).getString("user_gender").equals("2")) {
                            gender.check(R.id.female);
                        } else if (new JSONObject(userProfileDetails.get(0).toString()).getString("user_gender").equals("3")) {
                            gender.check(R.id.other);
                        }
                        countryId = new JSONObject(userProfileDetails.get(0).toString()).getString("user_country");
                        stateId = new JSONObject(userProfileDetails.get(0).toString()).getString("user_state");
                        cityId = new JSONObject(userProfileDetails.get(0).toString()).getString("user_city");
                        serviceRequestFlag = 2;
                        new getCountry().execute();
                    } catch (Exception e) {
                        dialog.dismiss();
                    }
                } else {
                    serviceRequestFlag = 3;

                }
            } else if (addressFlag == 1) {
                String data = mJSONObject.getString(userProfileRequest.data);
                JSONArray userProfileDetails = new JSONArray(data);
                new Profile.getState().execute();
            } else if (addressFlag == 2) {
                String data = mJSONObject.getString(userProfileRequest.data);
                JSONArray userProfileDetails = new JSONArray(data);
                state.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("state_name"));
                country.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("country_name"));
                new Profile.getCity().execute();
            } else if (addressFlag == 3) {
                String data = mJSONObject.getString(userProfileRequest.data);
                JSONArray userProfileDetails = new JSONArray(data);
                city.setText(new JSONObject(userProfileDetails.get(0).toString()).getString("city_name"));
                serviceRequestFlag = 3;
                dialog.dismiss();
            } else if (addressFlag == 5) {
                dialog.dismiss();
                if (mCode == userProfileRequest.codeSuccess) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.success), "Your account has been deleted successfully. ", 0);
                } else {
                    WebServiceUtil webServiceUtil = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                    String title = jsonObjectDesc.getString(webServiceUtil.title);
                    String description = jsonObjectDesc.getString(webServiceUtil.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }
            }
        } catch (JSONException mException) {
            dialog.dismiss();
            Log.i("JSONException", mException.getMessage());

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == 1) {
            country.setText(data.getStringExtra("name").toString());
            countryId = data.getStringExtra("id").toString();
            state.setText("");
            city.setText("");

        }
        if (requestCode == 1 && resultCode == 1) {
            state.setText(data.getStringExtra("name").toString());
            stateId = data.getStringExtra("id").toString();
            city.setText("");

        }
        if (requestCode == 2 && resultCode == 1) {
            city.setText(data.getStringExtra("name").toString());
            cityId = data.getStringExtra("id").toString();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getState extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            addressFlag = 2;
            super.onPreExecute();
            //dialog = ProgressUtil.showDialog(Profile.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {

                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.getStateDetails(countryId, stateId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class deleteUser extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addressFlag = 5;
            dialog = ProgressUtil.showDialog(Profile.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.deleteCustomers(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
            }
            return null;
        }
    }


}