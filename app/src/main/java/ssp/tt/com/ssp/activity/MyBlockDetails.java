package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
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
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyBlockDetails extends BaseActivity {

    @BindView(R.id.coordinatorLayout)
    LinearLayout coordinatorLayout;

    @BindView(R.id.tv_ticket_type)
    TextView tv_ticket_type;

    @BindView(R.id.tv_draw_no)
    TextView tv_draw_no;

    @BindView(R.id.tv_draw_on)
    TextView tv_draw_on;

    @BindView(R.id.tv_members)
    TextView tv_members;

    @BindView(R.id.tv_no_of_price)
    TextView tv_no_of_price;

    @BindView(R.id.tv_total_prize)
    TextView tv_total_prize;

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
    String errorMessage = "";
    String blockId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_block_details);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
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


    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_my_block_details));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        blockId = getIntent().getStringExtra("blockId");


    }


    @SuppressLint("StaticFieldLeak")
    private class MyResultAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(MyBlockDetails.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getBlockDetails(getApplicationContext(), userId, imeiNumber, blockId);
            } catch (Exception e) {
                callbackReturn(e.toString());
            }
            return null;
        }
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


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        dialog.dismiss();
       // response = getSampleResponse();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONObject jsonObject = mJSONObject.getJSONObject("data");
                if (jsonObject.has("0")) {
                    JSONObject jsonObjectFirst = jsonObject.getJSONObject("0");
                    String blockName = jsonObjectFirst.getString("blk_name");
                    String ticketTypeName = jsonObjectFirst.getString("ltype_name");
                    String drawCode = jsonObjectFirst.getString("draw_code");
                    String drawDate = jsonObjectFirst.getString("draw_date");
                    drawDate = Util.convertLocalDate(drawDate);
                    String blkId = jsonObjectFirst.getString("blk_id");
                    String totalNumbers = jsonObjectFirst.getString("tot_members");
                    String noOfPrize = jsonObjectFirst.getString("no_of_prize");
                    String totalPrize = jsonObjectFirst.getString("total_prize");
                    tv_ticket_type.setText(ticketTypeName);
                    tv_draw_no.setText("Draw Number " + drawCode);
                    tv_draw_on.setText("Draw On " + drawDate);
                    tv_members.setText("Total members :" + totalNumbers);
                    tv_no_of_price.setText("Number of prize :" + noOfPrize);
                    tv_total_prize.setText("Total prize value :" + totalPrize);
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
                    String prize_value = jsonObjectPos.getString("be_earn_amt");
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
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 1);
            }
        } catch (JSONException mException) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.getMessage(), 1);

        }
    }

    private String getSampleResponse() {
        return "{\n" +
                "    \"status\": \"200\",\n" +
                "    \"data\": {\n" +
                "        \"0\": {\n" +
                "            \"blk_id\": \"67\",\n" +
                "            \"blk_name\": \"NI-367-2\",\n" +
                "            \"draw_code\": \"NI-367\",\n" +
                "            \"draw_date\": \"2018-12-21 00:00:00\",\n" +
                "            \"ltype_name\": \"Nirmal\",\n" +
                "            \"tot_members\": \"3\",\n" +
                "            \"no_of_prize\": \"2\",\n" +
                "            \"total_prize\": \"50000.00\"\n" +
                "        },\n" +
                "        \"lottery_prize\": [\n" +
                "            {\n" +
                "                \"drb_pbreak_position\": \"Second\",\n" +
                "                \"drb_lt_id\": \"297\",\n" +
                "                \"be_earn_amt\": \"3333.33\",\n" +
                "                \"lt_series\": \"NE\",\n" +
                "                \"lt_number\": \"567911\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"drb_pbreak_position\": \"Second\",\n" +
                "                \"drb_lt_id\": \"298\",\n" +
                "                \"be_earn_amt\": \"3333.33\",\n" +
                "                \"lt_series\": \"NE\",\n" +
                "                \"lt_number\": \"567912\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"desc\": {\n" +
                "        \"type\": \"success\",\n" +
                "        \"title\": \"Blocks\",\n" +
                "        \"description\": \"Block earnings details fetched successfully\"\n" +
                "    }\n" +
                "}";
    }


}
