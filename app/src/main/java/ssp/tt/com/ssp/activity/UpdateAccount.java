package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.BankAdapter;
import ssp.tt.com.ssp.model.Bank;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class UpdateAccount extends BaseActivity {


    @BindView(R.id.et_bank)
    EditText etBank;

    @BindView(R.id.tl_bank_name)
    TextInputLayout tlBankName;

    @BindView(R.id.spinner_bank)
    Spinner spinnerBank;

    @BindView(R.id.et_branch_name)
    EditText etBranchName;

    @BindView(R.id.tl_account_number)
    TextInputLayout tlAccountNumber;

    @BindView(R.id.et_account_number)
    EditText etAccountNumber;

    @BindView(R.id.tl_account_name)
    TextInputLayout tlAccountName;

    @BindView(R.id.et_account_name)
    EditText etAccountName;

    @BindView(R.id.tl_account_ifsc_code)
    TextInputLayout tlAccountIfscCode;

    @BindView(R.id.et_account_ifsc_code)
    EditText etAccountIfscCode;

    @BindView(R.id.btn_update)
    Button btnUpdate;

    ArrayList<Bank> arrayListBank = new ArrayList<>();


    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;

    public int API_TYPE = 1;
    public final int API_ALL_BANK_LIST = 1;
    public final int API_GETTING_ACCOUNT_DETAILS = 2;
    public final int API_SAVE_ACCOUNT_DETAILS = 3;

    String bankId = "";
    String fromTitle = "";
    String yourAccountBalance = "";

    public static String INTENT_TITLE = "INTENT_TITLE";
    public static String INTENT_AMOUNT = "INTENT_AMOUNT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        ButterKnife.bind(this);
        setToolBar();
        registerBaseActivityReceiver();
        fromTitle = getIntent().getStringExtra(INTENT_TITLE);
        yourAccountBalance = getIntent().getStringExtra(INTENT_AMOUNT);
        etAccountIfscCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        new ALLBankListAPI().execute();
    }

    @OnClick(R.id.btn_update)
    public void setBtnUpdate(View view) {
        if (etBranchName.getText().toString().trim().length() == 0 && etAccountNumber.getText().toString().trim().length() == 0 &&
                etAccountName.getText().toString().trim().length() == 0 && etAccountIfscCode.getText().toString().trim().length() == 0) {
            tlBankName.setError(this.getString(R.string.please_enter_branch_name));
            Util.requestFocus(this, etBranchName);
            tlAccountNumber.setError(this.getString(R.string.please_enter_account_number));
            Util.requestFocus(this, etAccountNumber);
            tlAccountName.setError(this.getString(R.string.please_enter_your_account_name));
            Util.requestFocus(this, etAccountName);
            tlAccountIfscCode.setError(this.getString(R.string.please_enter_ifsc_code));
            Util.requestFocus(this, etAccountIfscCode);
        }
        if (etBranchName.getText().toString().trim().length() == 0) {
            tlBankName.setError(this.getString(R.string.please_enter_branch_name));
            Util.requestFocus(this, etBranchName);
        } else {
            tlBankName.setErrorEnabled(false);
        }
        if (etAccountNumber.getText().toString().trim().length() == 0) {
            Util.requestFocus(this, etAccountNumber);
            tlAccountNumber.setError(getString(R.string.please_enter_account_number));
        } else {
            tlAccountNumber.setErrorEnabled(false);
        }
        if (etAccountName.getText().toString().trim().length() == 0) {
            Util.requestFocus(this, etAccountName);
            tlAccountName.setError(this.getString(R.string.please_enter_your_account_name));
        } else {
            tlAccountName.setErrorEnabled(false);
        }
        if (etAccountIfscCode.getText().toString().trim().length() == 0) {
            Util.requestFocus(this, etAccountIfscCode);
            tlAccountIfscCode.setError(this.getString(R.string.please_enter_ifsc_code));
        } else {
            tlAccountIfscCode.setErrorEnabled(false);
        }

        if (etBranchName.getText().toString().trim().length() != 0 && etAccountNumber.getText().toString().trim().length() != 0 &&
                etAccountName.getText().toString().trim().length() != 0 && etAccountIfscCode.getText().toString().trim().length() != 0) {
            isInternetPresent = mConnectionDetector.isNetworkAvailable();
            if (isInternetPresent) {
                new SaveAccountDetails().execute();
            } else {
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), "No Network Connection !", 0);
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    public void setBtnCancel(View view) {
        finish();
    }

    @OnClick(R.id.et_bank)
    public void setBank(View view) {
        spinnerBank.performClick();
    }

    @OnClick(R.id.iv_bank)
    public void setIvBank(View view) {
        spinnerBank.performClick();
    }


    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_update_account));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        serviceConnector = new ServiceConnector();
        apiRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
    }

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

    private void setSpinnerListener() {
        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrayListBank.size() > 0) {
                    etBank.setText(arrayListBank.get(position).getBankName());
                    bankId = arrayListBank.get(position).getBankId();
                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void callbackReturn(String response) {
        switch (API_TYPE) {
            case API_ALL_BANK_LIST:
                getBankAPIResponse(response);
                break;
            case API_GETTING_ACCOUNT_DETAILS:
                getAccountDetails(response);
                break;
            case API_SAVE_ACCOUNT_DETAILS:
                getSaveAccountDetails(response);
                break;
        }
    }

    private void getBankAPIResponse(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String bankId = jsonObject.getString("bank_id");
                    String bankName = jsonObject.getString("bank_name");
                    arrayListBank.add(new Bank(bankId, bankName));
                }
                BankAdapter bankAdapter = new BankAdapter(UpdateAccount.this,
                        R.layout.item_bank, R.id.title, arrayListBank);
                spinnerBank.setAdapter(bankAdapter);
                setSpinnerListener();
                new GettingAccountDetails().execute();
            } else {
                dialog.dismiss();
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            dialog.dismiss();
            Log.i("JSONException", mException.toString());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }

    private void getAccountDetails(String response) {
        try {
            dialog.dismiss();
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(apiRequest.statusCode);
            if (mCode == apiRequest.codeSuccess) {
                btnUpdate.setText("Update");
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONArray jsonArray = mJSONObject.getJSONArray(userLoginRequest.data);
                JSONObject jsonObjectData = jsonArray.getJSONObject(0);
                String ub_id = jsonObjectData.getString(userLoginRequest.ub_id);
                String ub_user_id = jsonObjectData.getString(userLoginRequest.ub_user_id);
                String ub_acc_no = jsonObjectData.getString(userLoginRequest.ub_acc_no);
                String ub_acc_name = jsonObjectData.getString(userLoginRequest.ub_acc_name);
                bankId = jsonObjectData.getString(userLoginRequest.ub_bank_id);
                String ub_branch_name = jsonObjectData.getString(userLoginRequest.ub_branch_name);
                String ub_ifsc_code = jsonObjectData.getString(userLoginRequest.ub_ifsc_code);
                etAccountName.setText(ub_acc_name);
                etAccountNumber.setText(ub_acc_no);
                etAccountIfscCode.setText(ub_ifsc_code);
                etBranchName.setText(ub_branch_name);
                setSpinner();
            } else {
                btnUpdate.setText("Submit");
            }
        } catch (JSONException mException) {
            dialog.dismiss();
            Log.i("JSONException", mException.toString());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }

    private void setSpinner() {
        int spinnerPostion = 0;
        for (int index = 0; index < arrayListBank.size(); index++) {
            String mBankId = arrayListBank.get(index).getBankId();
            if (mBankId.equals(bankId)) {
                spinnerPostion = index;
            }
        }
        spinnerBank.setSelection(spinnerPostion);
    }


    private void getSaveAccountDetails(String response) {
        try {
            dialog.dismiss();
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(apiRequest.statusCode);
            if (mCode == apiRequest.codeSuccess) {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                if (fromTitle.equals("PROFILE")) {
                    Util.warningAlertDialog(this, title, description, 1);
                } else {
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
                            Intent intent = new Intent(UpdateAccount.this, WithDraw.class);
                            intent.putExtra(WithDraw.BALANCE_AMOUNT, yourAccountBalance);
                            startActivity(intent);

                        }
                    });
                    dialog.show();


                }
            } else {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            dialog.dismiss();
            Log.i("JSONException", mException.toString());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }


    @SuppressLint("StaticFieldLeak")
    private class SaveAccountDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_SAVE_ACCOUNT_DETAILS;
            dialog = ProgressUtil.showDialog(UpdateAccount.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String branchName = etBranchName.getText().toString().trim();
                String accountNumber = etAccountNumber.getText().toString().trim();
                String accountName = etAccountName.getText().toString().trim();
                String ifscCode = etAccountIfscCode.getText().toString().trim();
                serviceConnector.saveBankAccount(getApplicationContext(), UserId, imeiNumber, bankId, branchName, accountNumber, accountName, ifscCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GettingAccountDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_GETTING_ACCOUNT_DETAILS;
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getCheckRegisterWithBank(getApplicationContext(), UserId, imeiNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ALLBankListAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_ALL_BANK_LIST;
            dialog = ProgressUtil.showDialog(UpdateAccount.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.getBankList(getApplicationContext());
            } catch (Exception e) {
            }
            return null;
        }
    }

}
