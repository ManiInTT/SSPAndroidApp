package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyResultHistoryDetails extends BaseActivity {

    @BindView(R.id.coordinatorLayout)
    LinearLayout coordinatorLayout;


    @BindView(R.id.tv_ticket_type)
    TextView tv_ticket_type;

    @BindView(R.id.tv_draw_no)
    TextView tv_draw_no;

    @BindView(R.id.tv_draw_on)
    TextView tv_draw_on;

    @BindView(R.id.ll_first)
    LinearLayout llFirst;

    @BindView(R.id.tv_prize1)
    TextView tvFirstPrizeAmount;

    @BindView(R.id.tv_ticket1)
    TextView tvFirstPriceTicket;

    @BindView(R.id.ll_second)
    LinearLayout llSecond;

    @BindView(R.id.tv_prize2)
    TextView tvSecondPrizeAmount;

    @BindView(R.id.tv_ticket2)
    TextView tvSecondPriceTicket;


    @BindView(R.id.ll_third)
    LinearLayout llthird;

    @BindView(R.id.tv_prize3)
    TextView tvThirdPrizeAmount;

    @BindView(R.id.tv_ticket3)
    TextView tvThirdPriceTicket;


    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;


    String ticketTypeName = "";
    String ticketDate = "";
    String ticketCode = "";
    String ticketTypeId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_history_details);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        setRecyclerView();
        getMyResult();
    }

    private void getMyResult() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new MyResultAPI().execute();
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Network Connection !", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getMyResult();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }


    private void setRecyclerView() {
        Bundle extras = getIntent().getExtras();
        ticketTypeName = extras.getString("ticketTypeName");
        ticketDate = extras.getString("ticketDate");
        ticketCode = extras.getString("ticketCode");
        ticketTypeId = extras.getString("ticketTypeId");
        //ticketDate = Util.convertLocalDate(ticketDate);
        tv_ticket_type.setText(ticketTypeName);
        tv_draw_no.setText("Draw Number " + ticketCode);
        tv_draw_on.setText("Draw On " + ticketDate);

    }


    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_result_of_the_day));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @SuppressLint("StaticFieldLeak")
    private class MyResultAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(MyResultHistoryDetails.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getResultDetails(getApplicationContext(), userId, imeiNumber, ticketTypeId, ticketDate);
            } catch (Exception e) {
                callbackReturn(e.toString());
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        Log.i("Response", response);
        // response = getSampleResponse();
        dialog.dismiss();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONObject jsonObject = mJSONObject.getJSONObject("data");
                if (jsonObject.has("0")) {
                    JSONObject jsonObjectRaw = jsonObject.getJSONObject("0");
                    String ticketTypeName = jsonObjectRaw.getString("ltype_name");
                    String drawCode = jsonObjectRaw.getString("draw_code");
                    String drawDate = jsonObjectRaw.getString("draw_date");
                    drawDate = Util.convertLocalDate(drawDate);
                    tv_ticket_type.setText(ticketTypeName);
                    tv_draw_no.setText("Draw Number " + drawCode);
                    tv_draw_on.setText("Draw On " + drawDate);
                }

                int firstPrice = 0;
                int secondPrice = 0;
                int thirdPrice = 0;
                JSONArray lotteryPrize = jsonObject.getJSONArray("lottery_prize");
                tvFirstPriceTicket.setText("");
                tvSecondPriceTicket.setText("");
                tvThirdPriceTicket.setText("");
                for (int index = 0; index < lotteryPrize.length(); index++) {
                    JSONObject jsonObjectPos = lotteryPrize.getJSONObject(index);
                    String drbPpbreak_Position = jsonObjectPos.getString("drb_pbreak_position");
                    String prize_value = jsonObjectPos.getString("prize_value");
                    String lt_series = jsonObjectPos.getString("lt_series");
                    String lt_number = jsonObjectPos.getString("lt_number");

                    //First Prize
                    if (drbPpbreak_Position.equals("First")) {
                        llFirst.setVisibility(View.VISIBLE);
                        tvFirstPrizeAmount.setText(prize_value);
                        if (firstPrice % 2 == 0) {
                            tvFirstPriceTicket.append("\n " + lt_series + " " + lt_number);
                        } else {
                            tvFirstPriceTicket.append(" " + lt_series + " " + lt_number);
                        }
                        firstPrice = firstPrice + 1;
                    }
                    //Second Prize

                    else if (drbPpbreak_Position.equals("Second")) {
                        llSecond.setVisibility(View.VISIBLE);
                        tvSecondPrizeAmount.setText(prize_value);
                        if (secondPrice % 2 == 0) {
                            tvSecondPriceTicket.append("\n " + lt_series + " " + lt_number);
                        } else {
                            tvSecondPriceTicket.append(" " + lt_series + " " + lt_number);
                        }
                        secondPrice = secondPrice + 1;
                    }

                    //Consolation Prize
                    else {
                        llthird.setVisibility(View.VISIBLE);
                        tvThirdPrizeAmount.setText(prize_value);
                        if (thirdPrice % 2 == 0) {
                            tvThirdPriceTicket.append("\n " + lt_series + " " + lt_number);
                        } else {
                            tvThirdPriceTicket.append(" " + lt_series + " " + lt_number);
                        }
                        thirdPrice = thirdPrice + 1;
                    }
                }
            } else {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 1);

            }
        } catch (JSONException e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 1);
        }

    }

    private String getSampleResponse() {
        return "{\"status\":\"200\",\n" +
                " \"data\": { \"0\" : { \"draw_id\" :\"34\",\"draw_code\":\"KT-179\", \"draw_date\":\"2018-12-22 00:02:00\",\"ltype_name\":\"Karunya\",\"dr_id\":\"9\"}, \"lottery_prize\":[{\"drb_pbreak_position\":\"First\",\"prize_value\":\"100000.00\",\"lt_series\":\"KT\",\"lt_number\":\"158243\"},{\"drb_pbreak_position\":\"First\",\"prize_value\":\"100000.00\",\"lt_series\":\"AT\",\"lt_number\":\"154546\"},\n" +
                "{\"drb_pbreak_position\":\"First\",\"prize_value\":\"100000.00\",\"lt_series\":\"CT\",\"lt_number\":\"157886\"},{\"drb_pbreak_position\":\"First\",\"prize_value\":\"100000.00\",\"lt_series\":\"IO\",\"lt_number\":\"156543\"},\n" +
                "{\"drb_pbreak_position\":\"Second\",\"prize_value\":\"100000.00\",\"lt_series\":\"MO\",\"lt_number\":\"153564\"},\n" +
                "{\"drb_pbreak_position\":\"Second\",\"prize_value\":\"100000.00\",\"lt_series\":\"KT\",\"lt_number\":\"158243\"},{\"drb_pbreak_position\":\"Second\",\"prize_value\":\"100000.00\",\"lt_series\":\"AT\",\"lt_number\":\"154546\"},\n" +
                "{\"drb_pbreak_position\":\"Consolation\",\"prize_value\":\"100000.00\",\"lt_series\":\"CT\",\"lt_number\":\"157886\"},{\"drb_pbreak_position\":\"Consolation\",\"prize_value\":\"100000.00\",\"lt_series\":\"IO\",\"lt_number\":\"156543\"},\n" +
                "{\"drb_pbreak_position\":\"Consolation\",\"prize_value\":\"100000.00\",\"lt_series\":\"MO\",\"lt_number\":\"153564\"}]}}";
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }


}
