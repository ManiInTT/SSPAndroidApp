package ssp.tt.com.ssp.activity;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.DashboardAdapter;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

import static ssp.tt.com.ssp.activity.WithDraw.BALANCE_AMOUNT;

public class Ewallet extends BaseActivity {

    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    Util util;
    WebServiceUtil apiRequest;

    GridView androidGridView;
    String requestFlag = "1";
    String requestSentFlag = "1";
    String[] DashboardMenuTexts = {
            "Deposit", "Withdraw"

    };
    int[] DashboardMenuIcons = {
            R.string.icon_wallet, R.string.icon_withdrawal

    };
    @BindView(R.id.tv_more_transaction)
    TextView tv_more_transaction;
    @BindView(R.id.et_deposit)
    TextView et_deposit;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.et_gain)
    TextView et_gain;

    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    //Time format
    int pYear, pMonth, pDate;
    Calendar selectedCalender;
    Date fromDate = null;
    Date toDate = null;
    String yourAccountBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewallet);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        serviceRequest("1");
        Calendar now = Calendar.getInstance();
        pYear = now.get(Calendar.YEAR);
        pMonth = now.get(Calendar.MONTH);
        pDate = now.get(Calendar.DAY_OF_MONTH);
        selectedCalender = now;

    }

    /************************************************************************************
     * Class      : Dashboard
     * Use        : Method call widget configure
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        apiRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
        DashboardAdapter adapterViewAndroid = new DashboardAdapter(Ewallet.this, DashboardMenuTexts, DashboardMenuIcons);
        androidGridView = (GridView) findViewById(R.id.ewallet_grid);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(Ewallet.this, Deposit.class);
                    startActivity(intent);
                } else {
                    new CheckRegisterWithBank().execute();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_e_wallet));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    @OnClick(R.id.tv_request_statement)
    public void setRequestStatement(View view) {
        final Dialog dialog = new Dialog(Ewallet.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog_request_statement);
        final TextView tvOneWeek = dialog.findViewById(R.id.tv_one_week);
        final TextView tvOneMonth = dialog.findViewById(R.id.tv_one_month);
        final TextView tvMore = dialog.findViewById(R.id.tv_more);
        final LinearLayout llMore = dialog.findViewById(R.id.ll_more);
        final TextView btnCancel = dialog.findViewById(R.id.cancel_btn);
        final TextView btnOk = dialog.findViewById(R.id.ok_btn);
        final EditText etFromDate = dialog.findViewById(R.id.et_from_date);
        final EditText etToDate = dialog.findViewById(R.id.et_to_date);
        fromDate = null;
        toDate = null;
        etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Ewallet.this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            String dateofPurchase = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            fromDate = formatter.parse(dateofPurchase);
                            etFromDate.setText(formatter.format(fromDate));
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
        });
        etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Ewallet.this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            String dateofPurchase = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            toDate = formatter.parse(dateofPurchase);
                            etToDate.setText(formatter.format(toDate));
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
        });

        tvOneWeek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                new RequestStatmentAPI().execute("1");

            }
        });
        tvOneMonth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                new RequestStatmentAPI().execute("2");
            }
        });
        tvMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tvOneWeek.setVisibility(View.GONE);
                tvOneMonth.setVisibility(View.GONE);
                tvMore.setVisibility(View.GONE);
                llMore.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fromDate == null) {
                    Toast.makeText(Ewallet.this, "Please select start date", Toast.LENGTH_SHORT).show();
                } else if (toDate == null) {
                    Toast.makeText(Ewallet.this, "Please select end date", Toast.LENGTH_SHORT).show();
                } else if (toDate.before(fromDate)) {
                    Toast.makeText(Ewallet.this, "Oops! Looks like you have selected Start Date greater than End Date", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    new RequestStatmentAPI().execute("3", etFromDate.getText().toString(), etToDate.getText().toString());
                }

            }
        });
        dialog.show();

    }

    @OnClick(R.id.tv_more_transaction)
    public void moreTransaction(View view) {
          Intent intent = new Intent(this, TransactionHistory.class);
        intent.putExtra(TransactionHistory.OPEN_TAB, 1);
        startActivity(intent);
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                200,
                TableRow.LayoutParams.WRAP_CONTENT);
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

    /**
     * This function add the headers to the table
     **/
    public void addHeaders() {
        TableLayout tl = findViewById(R.id.main_table);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "Date & time", Color.WHITE, Typeface.BOLD, ContextCompat.getColor(this, R.color.colorAccent)));
        tr.addView(getTextView(0, "Type", Color.WHITE, Typeface.BOLD, ContextCompat.getColor(this, R.color.colorAccent)));
        tr.addView(getTextView(0, "Amount", Color.WHITE, Typeface.BOLD, ContextCompat.getColor(this, R.color.colorAccent)));
        tl.addView(tr, getTblLayoutParams());
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
            new GetUserWalletDetails().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(Ewallet.this, R.color.colorPrimary));
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

    }

    @NonNull
    private TableRow.LayoutParams getLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                200,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 2, 2, 2);
        return params;
    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @Override
    public void callbackReturn(String response) {
        if (requestSentFlag.equals("1")) {
            new getTransactionList().execute();
        }
        if (requestSentFlag.equals("2")) {
            dialog.dismiss();
        }

        try {
            if (requestFlag.equals("1")) {
                System.out.println("Transaction  Details" + response);
                JSONObject mJSONObject = new JSONObject(response);
                int mCode = mJSONObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    String data = mJSONObject.getString(apiRequest.data);
                    JSONObject jsonData = new JSONObject(data);
                    et_gain.setText(jsonData.getString("user_tot_gained"));
                    et_deposit.setText(jsonData.getString("user_tot_deposit"));

                    String mystring = getResources().getString(R.string.rs);
                    yourAccountBalance = jsonData.getString("user_tot_amt");
                    yourAccountBalance = yourAccountBalance.replaceAll(",", "");
                    try {
                        double totalAmount = Double.valueOf(yourAccountBalance);
                        tv_amount.setText(mystring + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount));
                    } catch (Exception e) {

                    }
                } else {
                    yourAccountBalance = "0.00";
                    String mystring = getResources().getString(R.string.rs);
                    double totalAmount = Double.valueOf(yourAccountBalance);
                    tv_amount.setText(mystring + NumberFormat.getNumberInstance(Locale.ENGLISH).format(totalAmount));
                    et_gain.setText("0.00");
                    et_deposit.setText("0.00");

                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }

            } else if (requestFlag.equals("2")) {
                System.out.println("Transaction  Details" + response);
                JSONObject mJSONObject = new JSONObject(response);
                int mCode = mJSONObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    addHeaders();
                    String data = mJSONObject.getString(apiRequest.data);
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray questionArray = new JSONArray(jsonData.getString("transactions"));
                    TableLayout tl = findViewById(R.id.main_table);
                    for (int qIndex = 0; qIndex < 5; qIndex++) {
                        TableRow tr = new TableRow(this);
                        tr.setLayoutParams(getLayoutParams());
                        tr.addView(getTextView(qIndex + 1, Util.convertLocalDateTime(new JSONObject(questionArray.get(qIndex).toString()).getString("trans_date")), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                        tr.addView(getTextView(qIndex + questionArray.length(), new JSONObject(questionArray.get(qIndex).toString()).getString("trans_type"), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                        tr.addView(getTextView(qIndex + questionArray.length(), new JSONObject(questionArray.get(qIndex).toString()).getString("trans_amount"), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                        tl.addView(tr, getTblLayoutParams());
                    }
                } else {
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }
            } else if (requestFlag.equals("3")) {
                dialog.dismiss();
                JSONObject mJSONObject = new JSONObject(response);
                int mCode = mJSONObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);
                    Util.successAlertDialog(this, title, description, 0);
                } else {
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }
            } else if (requestFlag.equals("4")) {
                dialog.dismiss();
                JSONObject mJSONObject = new JSONObject(response);
                int mCode = mJSONObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    Intent intent = new Intent(Ewallet.this, WithDraw.class);
                    intent.putExtra(BALANCE_AMOUNT, yourAccountBalance);
                    startActivity(intent);
                } else {
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);
                    Util.warningAlertDialog(this, title, description, 0);
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog_success);
                    dialog.setCancelable(false);
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    TextView tvMessage = dialog.findViewById(R.id.tv_message);
                    tvMessage.setText(description);
                    tvTitle.setText(title);
                    TextView noBtn = dialog.findViewById(R.id.no_btn);
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(Ewallet.this, UpdateAccount.class);
                            intent.putExtra(UpdateAccount.INTENT_TITLE, "WALLET");
                            intent.putExtra(UpdateAccount.INTENT_AMOUNT, yourAccountBalance);
                            startActivity(intent);

                        }
                    });
                    dialog.show();


                }
            }


        } catch (JSONException mException) {
            Log.i("JSONException ffff", mException.getMessage());
        }
    }

    /************************************************************************************
     * Class      : Ewallet
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class GetUserWalletDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Ewallet.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getUserWalletDetails(getApplicationContext(), UserId, imeiNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /************************************************************************************
     * Class      : Ewallet
     * Use        : Method call service request
     * Created on : 2018-04-17
     * Updated on : 2018-04-17
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class getTransactionList extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressUtil.showDialog(Ewallet.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            requestSentFlag = "2";
            requestFlag = "2";
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getTransactionList(getApplicationContext(), UserId, imeiNumber, "1","");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class RequestStatmentAPI extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Ewallet.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            requestSentFlag = "3";
            requestFlag = "3";
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String opt = params[0];
                if (opt.equals("3")) {
                    String startDate = params[1];
                    String endDate = params[2];
                    serviceConnector.getRequestStatement(getApplicationContext(), UserId, imeiNumber, opt, startDate, endDate);
                } else {
                    serviceConnector.getRequestStatement(getApplicationContext(), UserId, imeiNumber, opt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckRegisterWithBank extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Ewallet.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            requestSentFlag = "4";
            requestFlag = "4";
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

}
