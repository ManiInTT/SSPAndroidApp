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
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class TransactionHistory extends BaseActivity {
    TableLayout t1;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    Util util;
    WebServiceUtil apiRequest;
    LinearLayout linearLayout;
    HashMap<Integer, String> spinnerMap = new HashMap<Integer, String>();
    JSONArray questionArray = new JSONArray();

    public int API_TYPE = 1;
    public final int API_TRANSACTION_HISTORY = 1;
    public final int API_REQUEST_STATMENT = 2;


    @BindView(R.id.main_table)
    TableLayout tl;

    @BindView(R.id.tv_approved)
    TextView tvApproved;

    @BindView(R.id.tv_pending)
    TextView tvPending;

    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    //Time format
    int pYear, pMonth, pDate;
    Calendar selectedCalender;
    Date fromDate = null;
    Date toDate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        serviceRequest();
        Calendar now = Calendar.getInstance();
        pYear = now.get(Calendar.YEAR);
        pMonth = now.get(Calendar.MONTH);
        pDate = now.get(Calendar.DAY_OF_MONTH);
        selectedCalender = now;
    }

    @OnClick(R.id.tv_approved)
    public void setApproved(View v) {
        tvApproved.setTextColor(getResources().getColor(R.color.black));
        tvPending.setTextColor(getResources().getColor(R.color.textColor));
        tvRejected.setTextColor(getResources().getColor(R.color.textColor));
        tl.removeAllViews();
        addHeaders();
        loadTable("Approved");
    }


    @OnClick(R.id.tv_pending)
    public void setPending(View v) {
        tvApproved.setTextColor(getResources().getColor(R.color.textColor));
        tvPending.setTextColor(getResources().getColor(R.color.black));
        tvRejected.setTextColor(getResources().getColor(R.color.textColor));
        tl.removeAllViews();
        addHeaders();
        loadTable("Pending");
    }

    @OnClick(R.id.tv_rejected)
    public void setRejected(View v) {
        tvApproved.setTextColor(getResources().getColor(R.color.textColor));
        tvPending.setTextColor(getResources().getColor(R.color.textColor));
        tvRejected.setTextColor(getResources().getColor(R.color.black));
        tl.removeAllViews();
        addHeaders();
        loadTable("Rejected");
    }

    @OnClick(R.id.tv_request_statement)
    public void setRequestStatement(View view) {
        final Dialog dialog = new Dialog(TransactionHistory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog_request_statement);
        final TextView tvOneWeek = dialog.findViewById(R.id.tv_one_week);
        final TextView tvOneMonth = dialog.findViewById(R.id.tv_one_month);
        final TextView tvMore = dialog.findViewById(R.id.tv_more);
        final LinearLayout llMore = dialog.findViewById(R.id.ll_more);
        final TextView btnCancel = dialog.findViewById(R.id.cancel_btn);
        final TextView btnOk = dialog.findViewById(R.id.ok_btn);
        final EditText etFromDate = dialog.findViewById(R.id.et_from_date);
        final TextView ivFromDate = dialog.findViewById(R.id.iv_from_date);
        final EditText etToDate = dialog.findViewById(R.id.et_to_date);
        final TextView ivToDate = dialog.findViewById(R.id.iv_to_date);
        fromDate = null;
        toDate = null;
        etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivToDate.performClick();
            }
        });
        etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFromDate.performClick();
            }
        });
        ivFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionHistory.this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

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

        ivToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionHistory.this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

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
                    Toast.makeText(TransactionHistory.this, "Please select start date", Toast.LENGTH_SHORT).show();
                } else if (toDate == null) {
                    Toast.makeText(TransactionHistory.this, "Please select end date", Toast.LENGTH_SHORT).show();
                } else if (toDate.before(fromDate)) {
                    Toast.makeText(TransactionHistory.this, "Oops! Looks like you have selected Start Date greater than End Date", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    new RequestStatmentAPI().execute("3", etFromDate.getText().toString(), etToDate.getText().toString());
                }

            }
        });
        dialog.show();

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_transaction_history));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {

            new TransactionHistory.getTransactionList().execute();

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
            group.setBackgroundColor(ContextCompat.getColor(TransactionHistory.this, R.color.colorPrimary));
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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
        dialog.dismiss();
        if (API_TYPE == API_TRANSACTION_HISTORY) {
            try {
                System.out.println("Transaction  Details" + response);
                JSONObject jsonObject = new JSONObject(response);
                int mCode = jsonObject.getInt(apiRequest.statusCode);
                if (mCode == apiRequest.codeSuccess) {
                    String data = jsonObject.getString(apiRequest.data);
                    JSONObject jsonData = new JSONObject(data);
                    questionArray = new JSONArray(jsonData.getString("transactions"));
                    tl.removeAllViews();
                    addHeaders();
                    loadTable("Approved");
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
        } else {
            try {
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
            } catch (JSONException mException) {
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
            }
        }
    }

    private void loadTable(String approved) {
        try {
            for (int qIndex = 0; qIndex < questionArray.length(); qIndex++) {
                String transStatus = new JSONObject(questionArray.get(qIndex).toString()).getString("trans_status");
                if (transStatus.equals(approved)) {
                    final TableRow tr = new TableRow(this);
                    tr.setId(qIndex);
                    tr.setLayoutParams(getLayoutParams());
                    spinnerMap.put(qIndex, new JSONObject(questionArray.get(qIndex).toString()).toString());
                    tr.addView(getTextView(qIndex + 1, Util.convertLocalDateTime(new JSONObject(questionArray.get(qIndex).toString()).getString("trans_date")), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                    tr.addView(getTextView(qIndex + questionArray.length(), new JSONObject(questionArray.get(qIndex).toString()).getString("trans_type"), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                    tr.addView(getTextView(qIndex + questionArray.length(), new JSONObject(questionArray.get(qIndex).toString()).getString("trans_amount"), ContextCompat.getColor(this, R.color.textColor), Typeface.NORMAL, ContextCompat.getColor(this, R.color.white)));
                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            String transactionDetails = spinnerMap.get(tr.getId());
                            Intent intent = new Intent(TransactionHistory.this, TransactionDetails.class);

                        }
                    });
                    tl.addView(tr, getTblLayoutParams());
                }
            }
        } catch (Exception e) {

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
            API_TYPE = API_TRANSACTION_HISTORY;
            dialog = ProgressUtil.showDialog(TransactionHistory.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getTransactionList(getApplicationContext(), UserId, imeiNumber, "1");
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class RequestStatmentAPI extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_REQUEST_STATMENT;
            dialog = ProgressUtil.showDialog(TransactionHistory.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
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

}
