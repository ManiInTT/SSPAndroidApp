package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.BankAdapter;
import ssp.tt.com.ssp.model.Bank;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class Deposit extends BaseActivity {


    ServiceConnector serviceConnector;
    WebServiceUtil apiRequest;
    ProgressDialog dialog;

    private int API_TYPE = 1;
    private int API_ALL_BANK_LIST = 1;
    private int API_DEPOSIT = 2;
    private int API_DEPOSIT_IMAGE = 3;
    private int API_ACCOUNT_DETAILS = 4;
    private int API_REMOVE_IMAGE = 5;

    public final static int IMAGE_REQUEST = 201;
    File receiptFile = null;


    @BindView(R.id.et_date)
    EditText etDate;

    @BindView(R.id.coordinatorLayout)
    RelativeLayout coordinatorLayout;

    @BindView(R.id.spinner_bank)
    Spinner spinnerBank;

    @BindView(R.id.ll_cheque)
    LinearLayout llCheque;

    @BindView(R.id.ll_cash)
    LinearLayout llCash;


    @BindView(R.id.tv_deposit)
    RadioButton radioButtonDeposit;

    @BindView(R.id.tv_netbanking)
    RadioButton radioButtonNetBanking;

    @BindView(R.id.tv_cheque)
    RadioButton radioButtonCheque;

    @BindView(R.id.tv_cash)
    RadioButton radioButtonCash;

    @BindView(R.id.tv_draft)
    RadioButton radioButtonDraft;

    @BindView(R.id.btn_cancel)
    TextView txtCancel;

    @BindView(R.id.tv_amount_total)
    TextView tvAmountTotal;

    @BindView(R.id.et_amount)
    EditText etAmount;

    @BindView(R.id.et_beneficiary_account)
    EditText etBeneficiaryAccount;

    @BindView(R.id.et_beneficiary_name)
    EditText etBeneficiaryName;

    @BindView(R.id.et_accout_number)
    EditText etAccoutNumber;

    @BindView(R.id.tv_account_number)
    TextView tvAccountNumber;

    @BindView(R.id.tv_cheque_number)
    TextView tvChequeNumber;

    @BindView(R.id.tv_receipt_challan)
    TextView tvReceiptChallan;

    @BindView(R.id.et_check_number)
    EditText etCheckNumber;

    @BindView(R.id.et_check_date)
    EditText etCheckDate;

    @BindView(R.id.tv_check_date)
    TextView tvCheckDate;

    @BindView(R.id.rl_check_date)
    RelativeLayout rlCheckDate;

    @BindView(R.id.tv_transaction_number)
    TextView tvTransactionNumber;

    @BindView(R.id.et_transaction_number)
    EditText etTransactionNumber;

    @BindView(R.id.et_bank)
    EditText etBank;

    @BindView(R.id.et_ifsc_code)
    EditText etIfscCode;

    @BindView(R.id.et_receipt_challon)
    Button etReceiptChallon;

    @BindView(R.id.et_payment_notes)
    EditText etPaymentNotes;

    @BindView(R.id.tv_path)
    TextView tvPath;


    ArrayList<Bank> arrayListBank = new ArrayList<>();
    //Time format
    int pYear, pMonth, pDate;
    Calendar selectedCalender;

    String modeOfPayment = "Deposit";
    String modeOfDeposit = "Cash";
    String bankId = "";
    String errorMessage;
    String title = "";
    String uploadImageName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        getBankList();
        setView();
        etIfscCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
    }


    @OnTextChanged(value = R.id.et_amount,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterAmountInput(Editable editable) {
        tvAmountTotal.setText(editable.toString());
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

    private void cancelAlertDialog(String title, String message) {
        final Dialog dialog = new Dialog(Deposit.this);
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
                onBackPressed();
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

    @OnClick(R.id.et_check_date)
    public void setCheckDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String dateofPurchase = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = formatter.parse(dateofPurchase);
                    etCheckDate.setText(formatter.format(date));
                    selectedCalender.set(year, monthOfYear, dayOfMonth);
                    pYear = year;
                    pMonth = monthOfYear;
                    pDate = dayOfMonth;
                } catch (ParseException exception) {
                    Log.e("e", exception.toString());
                }


            }
        }, pYear, pMonth, pDate);
        Calendar cal = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        cal.set(1900, 01, 01);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.et_date)
    public void setSelectDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String dateofPurchase = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = formatter.parse(dateofPurchase);
                    etDate.setText(formatter.format(date));
                    selectedCalender.set(year, monthOfYear, dayOfMonth);
                    pYear = year;
                    pMonth = monthOfYear;
                    pDate = dayOfMonth;
                } catch (ParseException exception) {
                    Log.e("e", exception.toString());
                }


            }
        }, pYear, pMonth, pDate);
        Calendar cal = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        cal.set(1900, 01, 01);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.et_receipt_challon)
    public void addReceiptImage() {
        pickImage(IMAGE_REQUEST, CropImageView.CropShape.RECTANGLE, 4, 4);

    }


    @Override
    public void onImageChosen(int requestCode, Uri uri) {
        super.onImageChosen(requestCode, uri);
        if (IMAGE_REQUEST == requestCode) {
            File file = new File(uri.getPath());
            this.receiptFile = file;
            tvPath.setText(receiptFile.getName());
            tvPath.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.btn_cancel)
    public void depositCancel() {
        cancelAlertDialog("Cancel this deposit ?", "You can buy tickets only with eWallet cash.");
    }

    @OnClick(R.id.btn_update)
    public void setSave() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            if (modeOfPayment.equals("Deposit")) {
                if (modeOfDeposit.equals("Cash")) {
                    if (etAmount.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_amount), 0);
                    } else if (Integer.valueOf(etAmount.getText().toString().trim()) == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_valid_amount), 0);
                    } else if (etBeneficiaryAccount.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_beneficiary_account), 0);
                    } else if (tvPath.getText().toString().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_choose_challan_receipt), 0);
                    } else if (etPaymentNotes.getText().toString().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_payment_notes), 0);
                    } else {
                        new DepositImageUploadAPI().execute();
                    }
                } else if (modeOfDeposit.equals("Cheque")) {
                    if (etAmount.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_amount), 0);
                    } else if (Integer.valueOf(etAmount.getText().toString().trim()) == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_valid_amount), 0);
                    } else if (etBeneficiaryName.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_beneficiary_account), 0);
                    } else if (etAccoutNumber.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_account_number), 0);
                    } else if (etCheckNumber.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_cheque_number), 0);
                    } else if (etCheckDate.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_cheque_date), 0);
                    } else if (etIfscCode.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_ifsc_code), 0);
                    } else if (tvPath.getText().toString().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_choose_challan_receipt), 0);
                    } else if (etPaymentNotes.getText().toString().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_payment_notes), 0);
                    } else {
                        new DepositImageUploadAPI().execute();
                    }

                } else if (modeOfDeposit.equals("DD")) {
                    if (etAmount.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_amount), 0);
                    } else if (Integer.valueOf(etAmount.getText().toString().trim()) == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_valid_amount), 0);
                    } else if (etBeneficiaryName.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_beneficiary_account), 0);
                    } else if (etCheckNumber.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_cheque_number), 0);
                    } else if (etCheckDate.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_cheque_date), 0);
                    } else if (etIfscCode.getText().toString().trim().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_ifsc_code), 0);
                    } else if (tvPath.getText().toString().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_choose_challan_receipt), 0);
                    } else if (etPaymentNotes.getText().toString().length() == 0) {
                        Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_payment_notes), 0);
                    } else {
                        new DepositImageUploadAPI().execute();
                    }
                }
            } else if (modeOfPayment.equals("NetBanking")) {

                if (etAmount.getText().toString().trim().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_amount), 0);
                } else if (Integer.valueOf(etAmount.getText().toString().trim()) == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_valid_amount), 0);
                } else if (etBeneficiaryName.getText().toString().trim().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_beneficiary), 0);
                } else if (etAccoutNumber.getText().toString().trim().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_your_account_number), 0);
                } else if (etCheckNumber.getText().toString().trim().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_your_account_name), 0);
                } else if (etTransactionNumber.getText().toString().trim().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_trnsfer_number), 0);
                } else if (etIfscCode.getText().toString().trim().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_ifsc_code), 0);
                } else if (etPaymentNotes.getText().toString().length() == 0) {
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_payment_notes), 0);
                } else {
                    new DepositAPI().execute();
                }
            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Network Connection !", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getBankList();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }


    }

    @OnClick(R.id.et_bank)
    public void setBank() {
        spinnerBank.performClick();
    }

    @OnClick(R.id.tv_deposit)
    public void setDeposit() {
        if (radioButtonDeposit.isChecked()) {
            modeOfPayment = "Deposit";
        }
        setView();
    }

    @OnClick(R.id.tv_netbanking)
    public void setNetBanking() {
        if (radioButtonNetBanking.isChecked()) {
            modeOfPayment = "NetBanking";
        }
        setView();
    }

    private void setView() {
        if (modeOfPayment.equals("Deposit")) {
            tvAccountNumber.setVisibility(View.VISIBLE);
            etAccoutNumber.setVisibility(View.VISIBLE);
            tvTransactionNumber.setVisibility(View.GONE);
            etTransactionNumber.setVisibility(View.GONE);
            tvReceiptChallan.setVisibility(View.VISIBLE);
            etReceiptChallon.setVisibility(View.VISIBLE);
            tvCheckDate.setVisibility(View.VISIBLE);
            rlCheckDate.setVisibility(View.VISIBLE);
            if (modeOfDeposit.equals("Cash")) {
                radioButtonCash.setText("Cash");
                radioButtonCheque.setText("Cheque");
                radioButtonDraft.setText("DD");
                tvAccountNumber.setText("Account number");
                tvChequeNumber.setText("Cheque Number");
                tvCheckDate.setText("Cheque Date");
                tvReceiptChallan.setText("Receipt /  bank challan");
                llCash.setVisibility(View.VISIBLE);
                llCheque.setVisibility(View.GONE);
            } else if (modeOfDeposit.equals("Cheque")) {
                llCheque.setVisibility(View.VISIBLE);
                llCash.setVisibility(View.GONE);
                radioButtonCash.setText("Cash");
                radioButtonCheque.setText("Cheque");
                radioButtonDraft.setText("DD");
                tvAccountNumber.setText("Account number");
                tvChequeNumber.setText("Cheque Number");
                tvReceiptChallan.setText("Receipt /  bank challan");
                tvCheckDate.setText("Cheque Date");
            } else if (modeOfDeposit.equals("DD")) {
                llCheque.setVisibility(View.VISIBLE);
                llCash.setVisibility(View.GONE);
                radioButtonCash.setText("Cash");
                radioButtonCheque.setText("Cheque");
                radioButtonDraft.setText("DD");
                tvAccountNumber.setVisibility(View.GONE);
                etAccoutNumber.setVisibility(View.GONE);
                tvChequeNumber.setText("Demand draft number");
                tvCheckDate.setText("Demand draft date");
                tvReceiptChallan.setText("Receipt / Bank challan / DD");
            }
        } else if (modeOfPayment.equals("NetBanking")) {
            radioButtonCash.setText("NEFT");
            radioButtonCheque.setText("IMPS");
            radioButtonDraft.setText("RTGS");
            llCheque.setVisibility(View.VISIBLE);
            llCash.setVisibility(View.GONE);
            tvAccountNumber.setText("Your account number");
            tvChequeNumber.setText("Your account name");
            tvReceiptChallan.setVisibility(View.GONE);
            etReceiptChallon.setVisibility(View.GONE);
            tvTransactionNumber.setVisibility(View.VISIBLE);
            etTransactionNumber.setVisibility(View.VISIBLE);
            tvCheckDate.setVisibility(View.GONE);
            rlCheckDate.setVisibility(View.GONE);
            tvAccountNumber.setVisibility(View.VISIBLE);
            etAccoutNumber.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.tv_cash)
    public void setCash() {
        if (radioButtonCash.isChecked()) {
            modeOfDeposit = "Cash";
        }
        setView();
    }

    @OnClick(R.id.tv_cheque)
    public void setCheque() {
        if (radioButtonCheque.isChecked()) {
            modeOfDeposit = "Cheque";
        }
        setView();
    }

    @OnClick(R.id.tv_draft)
    public void setDraft() {
        if (radioButtonDraft.isChecked()) {
            modeOfDeposit = "DD";
        }
        setView();
    }

    private void getBankList() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new ALLBankListAPI().execute();
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Network Connection !", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getBankList();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    private void getWidgetConfig() {
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_deposit));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(new Date());
        etDate.setText(formattedDate);
        Calendar now = Calendar.getInstance();
        pYear = now.get(Calendar.YEAR);
        pMonth = now.get(Calendar.MONTH);
        pDate = now.get(Calendar.DAY_OF_MONTH);
        selectedCalender = now;
        etDate.setFocusable(false);
        etDate.setClickable(true);
        etCheckDate.setFocusable(false);
        etCheckDate.setClickable(true);
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


    @SuppressLint("StaticFieldLeak")
    private class ALLBankListAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_ALL_BANK_LIST;
            dialog = ProgressUtil.showDialog(Deposit.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                serviceConnector.getBankList(getApplicationContext());
            } catch (Exception e) {
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DepositImageUploadAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_DEPOSIT_IMAGE;
            dialog = ProgressUtil.showDialog(Deposit.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            serviceConnector.depositImage(getApplicationContext(), receiptFile);
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class removeImageUploadAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_REMOVE_IMAGE;
        }

        protected Void doInBackground(String... params) {
            String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
            String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
            serviceConnector.removeImage(getApplicationContext(), userId, imeiNumber, params[0]);
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class AccountDetailsAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_ACCOUNT_DETAILS;
        }

        protected Void doInBackground(String... params) {
            String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
            String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
            serviceConnector.getBeneficiaryAccount(getApplicationContext(), userId, imeiNumber);
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DepositAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_DEPOSIT;
            if (!modeOfPayment.equals("Deposit")) {
                dialog = ProgressUtil.showDialog(Deposit.this, getString(R.string.loading_message));
            }

        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                if (modeOfPayment.equals("Deposit")) {
                    if (modeOfDeposit.equals("Cash")) {
                        String transDate = etDate.getText().toString();
                        String transBaId = "1";
                        String transMode = "0";
                        String transAmount = etAmount.getText().toString();
                        String transBeneficiaryAccount = etBeneficiaryAccount.getText().toString();
                        String transNotes = etPaymentNotes.getText().toString();
                        serviceConnector.depositCash(getApplicationContext(), userId, imeiNumber, transDate, transBaId,
                                transMode, transAmount, transNotes, params[0]);
                    } else if (modeOfDeposit.equals("Cheque")) {
                        String transDate = etDate.getText().toString();
                        String transBaId = "1";
                        String transMode = "1";
                        String transAmount = etAmount.getText().toString();
                        String transNotes = etPaymentNotes.getText().toString();
                        String transChequeDate = etCheckDate.getText().toString();
                        String transAccountNumber = etAccoutNumber.getText().toString();
                        String transBeneficiary = etBeneficiaryName.getText().toString();
                        String transChequeNumber = etCheckNumber.getText().toString();
                        String transBankId = bankId;
                        String transBankIFSC = etIfscCode.getText().toString();
                        serviceConnector.depositCheque(getApplicationContext(), userId, imeiNumber, transDate, transBaId,
                                transMode, transAmount, transNotes, transChequeDate, transChequeNumber, transAccountNumber, transBankId,
                                transBankIFSC, params[0]);

                    } else if (modeOfDeposit.equals("DD")) {
                        String transDate = etDate.getText().toString();
                        String transBaId = "1";
                        String transMode = "2";
                        String transBeneficiary = etBeneficiaryName.getText().toString();
                        String transAmount = etAmount.getText().toString();
                        String transNotes = etPaymentNotes.getText().toString();
                        String transChequeDate = etCheckDate.getText().toString();
                        String transAccountNumber = etAccoutNumber.getText().toString();
                        String transChequeNumber = etCheckNumber.getText().toString();
                        String transBankId = bankId;
                        String transBankIFSC = etIfscCode.getText().toString();
                        serviceConnector.depositCheque(getApplicationContext(), userId, imeiNumber, transDate, transBaId,
                                transMode, transAmount, transNotes, transChequeDate, transChequeNumber, transAccountNumber, transBankId,
                                transBankIFSC, params[0]);
                    }
                } else if (modeOfPayment.equals("NetBanking")) {
                    String transMode = "4";
                    if (modeOfDeposit.equals("Cash")) {
                        transMode = "4";
                    } else if (modeOfDeposit.equals("Cheque")) {
                        transMode = "5";
                    } else if (modeOfDeposit.equals("DD")) {
                        transMode = "6";
                    }
                    String transDate = etDate.getText().toString();
                    String transBaId = "2";
                    String transAmount = etAmount.getText().toString();
                    String transBeneficiary = etBeneficiaryName.getText().toString();
                    String yourAccountNumber = etAccoutNumber.getText().toString();
                    String yourAccountName = etCheckNumber.getText().toString();
                    String transactionNumber = etTransactionNumber.getText().toString();
                    String transBankId = bankId;
                    String transBankIFSC = etIfscCode.getText().toString();
                    String transNotes = etPaymentNotes.getText().toString();
                    String transTransDate = etDate.getText().toString();
                    serviceConnector.depositNetBanking(getApplicationContext(), userId, imeiNumber, transDate, transBaId,
                            transMode, transAmount, transNotes, yourAccountNumber, yourAccountName, transBankId,
                            transBankIFSC, transTransDate, transactionNumber);
                }
            } catch (Exception e) {
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        if (API_TYPE == API_ALL_BANK_LIST) {
            getBankAPIResponse(response);
        } else if (API_TYPE == API_ACCOUNT_DETAILS) {
            getAccountDetails(response);
        } else if (API_TYPE == API_DEPOSIT_IMAGE) {
            getDepositImageResponse(response);
        } else if (API_TYPE == API_DEPOSIT) {
            getDepositResponse(response);
        } else if (API_TYPE == API_REMOVE_IMAGE) {
            getRemoveImageResponse(response);
        }

    }


    private void getAccountDetails(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String beneficiaryName = jsonObject.getString("ba_acc_name");
                    String bankAccountNo = jsonObject.getString("ba_acc_no");
                    etBeneficiaryName.setText(beneficiaryName);
                    etBeneficiaryName.setEnabled(false);
                    etBeneficiaryName.setFocusable(false);
                    etBeneficiaryName.setFocusableInTouchMode(false);
                    etBeneficiaryAccount.setText(beneficiaryName);
                    etBeneficiaryAccount.setEnabled(false);
                    etBeneficiaryAccount.setFocusable(false);
                    etBeneficiaryAccount.setFocusableInTouchMode(false);
                    etAccoutNumber.setText(bankAccountNo);
                    etAccoutNumber.setEnabled(false);
                    etAccoutNumber.setFocusable(false);
                    etAccoutNumber.setFocusableInTouchMode(false);
                }
                dialog.dismiss();
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

    private void getDepositImageResponse(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                uploadImageName = mJSONObject.getString("data");
                new DepositAPI().execute(uploadImageName);
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
            Log.i("JSONException", mException.getMessage());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
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
                BankAdapter bankAdapter = new BankAdapter(Deposit.this,
                        R.layout.item_bank, R.id.title, arrayListBank);
                spinnerBank.setAdapter(bankAdapter);
                setSpinnerListener();
                new AccountDetailsAPI().execute();
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


    private void getDepositResponse(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                dialog.dismiss();
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.successAlertDialog(this, title, description, 1);
            } else {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                title = jsonObjectDesc.getString(userLoginRequest.title);
                errorMessage = jsonObjectDesc.getString(userLoginRequest.description);
                new removeImageUploadAPI().execute(uploadImageName);
            }
        } catch (JSONException mException) {
            dialog.dismiss();
            Log.i("JSONException", mException.getMessage());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
        }
    }

    private void getRemoveImageResponse(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            WebServiceUtil userLoginRequest = new WebServiceUtil();

            if (status.equals("200")) {
                dialog.dismiss();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
            } else {
                dialog.dismiss();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            dialog.dismiss();
            Log.i("JSONException", mException.getMessage());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
        }
    }


}
