package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class WithDraw extends BaseActivity {

    public static final String BALANCE_AMOUNT = "BalanceAmount";

    @BindView(R.id.tv_amount)
    TextView tv_amount;

    @BindView(R.id.et_amount)
    EditText etAmount;

    @BindView(R.id.tv_balance_amount)
    TextView tvBalanceAmount;

    @BindView(R.id.cb_terms)
    CheckBox cbTerms;

    @BindView(R.id.btn_with_draw)
    Button btnWithDraw;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    double totalAmount = 0.0;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;

    public int API_TYPE = 0;
    public final int API_WITH_DRAW = 1;
    public final int API_TERMS = 2;
    String requestFlag = "1";
    String requestSentFlag = "1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        ButterKnife.bind(this);
        setToolBar();
        registerBaseActivityReceiver();
        setData();
        setTextWatcher();
        new getWithdrawRequestHistory().execute();

        cbTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbTerms.isChecked()) {
                    cbTerms.setChecked(false);
                    new TermsRequestAPI().execute();
                } else {
                    cbTerms.setChecked(false);
                }
            }
        });

    }

    @OnClick(R.id.tvPendingWihdraw)
    public void setPendingWithdraw(View view) {
        Intent intent = new Intent(this, TransactionHistory.class);
        intent.putExtra(TransactionHistory.OPEN_TAB, 2);
        startActivity(intent);
    }


    @OnClick(R.id.btn_with_draw)
    public void setBtnWithDraw(View view) {
        if (etAmount.getText().toString().trim().length() == 0) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_amount), 0);
        } else if (!cbTerms.isChecked()) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_terms), 0);
        } else {
            isInternetPresent = mConnectionDetector.isNetworkAvailable();
            if (isInternetPresent) {
                new WithDrawRequestAPI().execute();
            } else {
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), "No Network Connection !", 0);
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    public void setBtnCancel(View view) {
        finish();
    }

    private void setTextWatcher() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentAmount = s.toString();
                if (!currentAmount.equals("")) {
                    double withDrawAmount = Double.valueOf(currentAmount);
                    double amountTotal = totalAmount - withDrawAmount;
                    if (amountTotal > 0) {
                        String mystring = getResources().getString(R.string.rs);
                        tvBalanceAmount.setText(mystring + NumberFormat.getNumberInstance(Locale.US).format(amountTotal));
                        btnWithDraw.setEnabled(true);
                        btnWithDraw.setAlpha(1f);
                    } else {
                        btnWithDraw.setEnabled(false);
                        btnWithDraw.setAlpha(0.6f);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setData() {
        String yourAccountBalance = getIntent().getStringExtra(BALANCE_AMOUNT);
        yourAccountBalance = yourAccountBalance.replaceAll(",", "");
        try {
            totalAmount = Double.valueOf(yourAccountBalance);
            String mystring = getResources().getString(R.string.rs);
            tv_amount.setText(mystring + NumberFormat.getNumberInstance(Locale.US).format(totalAmount));
            tvBalanceAmount.setText(mystring + NumberFormat.getNumberInstance(Locale.US).format(totalAmount));
        } catch (Exception e) {

        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_with_draw));
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

    /**
     * This function add the headers to the table
     **/
    public void addHeaders() {
        TableLayout tl = findViewById(R.id.main_table);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "Date & time", Color.WHITE, Typeface.BOLD, ContextCompat.getColor(this, R.color.colorAccent)));
        tr.addView(getTextView(0, "Status", Color.WHITE, Typeface.BOLD, ContextCompat.getColor(this, R.color.colorAccent)));
        tr.addView(getTextView(0, "Amount", Color.WHITE, Typeface.BOLD, ContextCompat.getColor(this, R.color.colorAccent)));
        tl.addView(tr, getTblLayoutParams());
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title);
        tv.setWidth(200);
        tv.setTextColor(color);
        tv.setPadding(10, 10, 10, 10);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        return tv;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                200,
                TableRow.LayoutParams.WRAP_CONTENT);
    }


    @NonNull
    private TableRow.LayoutParams getLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                200,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 2, 2, 2);
        return params;
    }


    @Override
    public void callbackReturn(String response) {

        if (requestSentFlag.equals("1")) {


            try {

                if (requestFlag.equals("1")) {
                    System.out.println("Transaction  Details" + response);
                    JSONObject mJSONObject = new JSONObject(response);
                    int mCode = mJSONObject.getInt(apiRequest.statusCode);
                    if (mCode == apiRequest.codeSuccess) {
                        addHeaders();
                        String data = mJSONObject.getString(apiRequest.data);
                        //JSONObject jsonData = new JSONObject(data);
                        JSONArray questionArray = new JSONArray(data);
                        TableLayout tl = findViewById(R.id.main_table);
                        for (int qIndex = 0; qIndex < 10; qIndex++) {
                            System.out.println("Width draw  Details" + questionArray.get(qIndex));

                            TableRow tr = new TableRow(this);
                            tr.setLayoutParams(getLayoutParams());
                            tr.addView(getTextView(qIndex + 1, Util.convertLocalDateTime(new JSONObject(questionArray.get(qIndex).toString()).getString("wr_date")), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                            tr.addView(getTextView(qIndex + questionArray.length(), new JSONObject(questionArray.get(qIndex).toString()).getString("wr_status"), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                            tr.addView(getTextView(qIndex + questionArray.length(), new JSONObject(questionArray.get(qIndex).toString()).getString("wr_amt"), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                            tl.addView(tr, getTblLayoutParams());
                        }
                    }
//                else {
//                    WebServiceUtil userLoginRequest = new WebServiceUtil();
//                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
//                    String title = jsonObjectDesc.getString(userLoginRequest.title);
//                    String description = jsonObjectDesc.getString(userLoginRequest.description);
//                    Util.warningAlertDialog(this, title, description, 0);
//                }
                }
            } catch (JSONException mException) {
                Log.i("JSONException ffff", mException.getMessage());
            }

        }
        if (API_TYPE == API_WITH_DRAW) {
            try {
                dialog.dismiss();
                JSONObject mJSONObject = new JSONObject(response);
                int mCode = mJSONObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);
                    Util.successAlertDialog(this, title, description, 1);
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
        } else if (API_TYPE == API_TERMS) {
            try {
                dialog.dismiss();
                JSONObject mJSONObject = new JSONObject(response);
                int mCode = mJSONObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONArray jsonArray = mJSONObject.getJSONArray(userLoginRequest.data);
                    String terms_for = jsonArray.getJSONObject(0).getString(userLoginRequest.terms_for);
                    String terms_content = jsonArray.getJSONObject(0).getString(userLoginRequest.terms_content);
                    final Dialog dialog = new Dialog(WithDraw.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog_terms);
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    TextView tvMessage = dialog.findViewById(R.id.tv_message);
                    tvMessage.setText(terms_content);
                    tvTitle.setText(terms_for);
                    TextView btnYes = dialog.findViewById(R.id.yes_btn);
                    btnYes.setText("Accept");
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            cbTerms.setChecked(true);

                        }
                    });
                    TextView btnNo = dialog.findViewById(R.id.no_btn);
                    btnNo.setText("Decline");
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
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


    }

    @SuppressLint("StaticFieldLeak")
    private class getWithdrawRequestHistory extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressUtil.showDialog(Ewallet.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            requestSentFlag = "1";
            requestFlag = "1";
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getWithdrawRequestHistory(getApplicationContext(), UserId, imeiNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class WithDrawRequestAPI extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(WithDraw.this, getString(R.string.loading_message));
            API_TYPE = API_WITH_DRAW;
            requestSentFlag = "2";
            requestFlag = "2";
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String amount = etAmount.getText().toString().trim();
                serviceConnector.withDrawRequestAPI(getApplicationContext(), UserId, imeiNumber, amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TermsRequestAPI extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_TERMS;
            requestSentFlag = "3";
            requestFlag = "3";
            dialog = ProgressUtil.showDialog(WithDraw.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String termsOf = "withdraw";
                serviceConnector.termsRequestAPI(getApplicationContext(), UserId, imeiNumber, termsOf);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
