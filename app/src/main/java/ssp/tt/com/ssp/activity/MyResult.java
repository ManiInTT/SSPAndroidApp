package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.MyResultNewAdapter;
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyResult extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;


    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;
    View headerView;
    View footerView;
    TextView tvTotalEarning;
    TextView tvTotalEarningTitle;

    MyResultNewAdapter myResultAdapter;
    final ArrayList<MyBlockResultDao> myBlockResultDaoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_type);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_dashboard));
        setSupportActionBar(toolbar);
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
        // set Adapter
        myResultAdapter = new MyResultNewAdapter(this, myBlockResultDaoList);
        recyclerView.setAdapter(myResultAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //add header
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_myreult, recyclerView, false);
        tvTotalEarning = headerView.findViewById(R.id.tv_total_earning);
        tvTotalEarningTitle = headerView.findViewById(R.id.tv_total_earning_title);
        tvTotalEarningTitle.setText("Total price for this week");
        myResultAdapter.setHeaderView(headerView);

        //add footer
        footerView = LayoutInflater.from(this).inflate(R.layout.layout_footer_myresult, recyclerView, false);
        TextView tvHistory = footerView.findViewById(R.id.tv_history);
        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthYear();
            }
        });
        myResultAdapter.setFooterView(footerView);
    }

    private void showMonthYear() {
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MyResult.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                Intent intent = new Intent(getApplicationContext(), MyResultHistory.class);
                intent.putExtra("MONTH", selectedMonth+1);
                intent.putExtra("YEAR", selectedYear);
                startActivity(intent);
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setMinYear(2000)
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMaxYear(today.get(Calendar.YEAR))
                .build()
                .show();
    }

    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_my_result));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @SuppressLint("StaticFieldLeak")
    private class MyResultAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(MyResult.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.viewResult(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
                callbackReturn(e.toString());
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject1 = new JSONObject(response);
            Log.i("response", response);
            String status = jsonObject1.getString("status");
            float thisWeekPrize = 0;
            String blockType = "";
            if (status.equals("200")) {
                JSONArray data = jsonObject1.getJSONArray("data");
                for (int iindex = 0; iindex < data.length(); iindex++) {
                    JSONObject jsonObject = data.getJSONObject(iindex);
                    String blkId = jsonObject.getString("drb_id");
                    String lt_id = jsonObject.getString("lt_id");
                    String blockName = jsonObject.getString("blk_name");
                    String drawCode = jsonObject.getString("draw_code");
                    String drawDate = jsonObject.getString("draw_date");
                    drawDate = Util.convertLocalDate(drawDate);
                    String ticketTypeName = jsonObject.getString("ltype_name");
                    String prizeAmount = jsonObject.getString("lt_prize_amt");
                    String drBreakPosition = jsonObject.getString("drb_pbreak_position");
                    String ltSeries = jsonObject.getString("lt_series");
                    String ltNumber = jsonObject.getString("lt_number");
                    try {
                        float currentAmount = Float.parseFloat(prizeAmount);
                        thisWeekPrize = thisWeekPrize + currentAmount;
                    } catch (NumberFormatException e) {
                        Log.i("NumberFormatException", e.toString());
                    }
                    MyBlockResultDao myBlockResultDao = new MyBlockResultDao(blkId, lt_id, blockType, ticketTypeName, blockName, drawCode, drawDate, prizeAmount, prizeAmount, ltSeries, drBreakPosition, "", ltNumber);
                    myBlockResultDaoList.add(myBlockResultDao);
                }
                tvTotalEarning.setText(getResources().getString(R.string.rs) + " " + thisWeekPrize);
                myResultAdapter.setData(myBlockResultDaoList);
            } else {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject1.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);

            }
        } catch (JSONException e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);

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


}
