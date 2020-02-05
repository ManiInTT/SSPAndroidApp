package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import ssp.tt.com.ssp.adapter.MyBlockResultNewAdapter;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyBlock extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;


    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private int API_TYPE = 1;
    private int API_TOTAL_RESULT = 1;
    private int API_CURRENT_RESULT = 2;


    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;
    View headerView;
    View footerView;
    TextView tvTotalEarning;
    TextView tvTotalEarningTitle;
    String errorMessage = "";
    String title = "";
    String totalEarnings = "";

    MyBlockResultNewAdapter myResultAdapter;
    final ArrayList<MyBlockResultDao> myBlockResultDaoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_type);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        setRecyclerView();
        getMyResult();


    }

    private void getMyResult() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new TotalBlockResultAPI().execute();
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
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }


    private void setRecyclerView() {
        // set Adapter
        myResultAdapter = new MyBlockResultNewAdapter(this, myBlockResultDaoList);
        recyclerView.setAdapter(myResultAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //add header
//        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_myreult, recyclerView, false);
//        tvTotalEarning = headerView.findViewById(R.id.tv_total_earning);
//        tvTotalEarningTitle = headerView.findViewById(R.id.tv_total_earning_title);
//        tvTotalEarningTitle.setText("Total block price for this week");
//        myResultAdapter.setHeaderView(headerView);

//        LinearLayout block_top = headerView.findViewById(R.id.block_top);
//        block_top.setVisibility(View.GONE);

        //add footer
        footerView = LayoutInflater.from(this).inflate(R.layout.layout_footer_myresult, recyclerView, false);
        TextView tvHistory = footerView.findViewById(R.id.tv_history);

        tvHistory.setVisibility(View.GONE);

        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthYear();
            }
        });
        myResultAdapter.setFooterView(footerView);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {

                    Log.d("df","Testr");

                    MyBlockResultDao myBlockResultDao = myBlockResultDaoList.get(position);
//                    Intent intent = new Intent(MyBlock.this, MyBlockDetails.class);
//                    intent.putExtra("blockId", myBlockResultDao.getBlockId());
//                    startActivityForResult(intent, 1);
                    Log.d("df","Testr" + myBlockResultDao.getBlockId());
                    Intent intent = new Intent(MyBlock.this, MyBlockDetails.class);
                    intent.putExtra("blockId", myBlockResultDao.getBlockId());
                    startActivity(intent);
                } catch (Exception e) {

                }

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void showMonthYear() {
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MyBlock.this, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                Intent intent = new Intent(getApplicationContext(), MyBlockResultHistory.class);
                intent.putExtra("MONTH", selectedMonth);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_my_block));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


    @SuppressLint("StaticFieldLeak")
    private class TotalBlockResultAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_TOTAL_RESULT;
            dialog = ProgressUtil.showDialog(MyBlock.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.totalCurrentBlocks(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class MyCurrentBlockResultAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_CURRENT_RESULT;
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.viewUserBlocks(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        if (API_TYPE == API_TOTAL_RESULT) {
            getTotalResultResponse(response);
        } else if (API_TYPE == API_CURRENT_RESULT) {
            getCurrentResultResponse(response);
        }
    }

    private void getTotalResultResponse(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    totalEarnings = jsonObject.getString("total_earnings");
                    tvTotalEarning.setText(getResources().getString(R.string.rs) + " " + totalEarnings);
                }
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                title = jsonObjectDesc.getString(webServiceUtil.title);
                errorMessage = jsonObjectDesc.getString(webServiceUtil.description);
            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.getMessage());
        }
        new MyCurrentBlockResultAPI().execute();
    }


    private void getCurrentResultResponse(String response) {
        dialog.dismiss();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String blockType = "";
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String blockName = jsonObject.getString("blk_name");
                    String ticketTypeName = jsonObject.getString("ltype_name");
                    String drawCode = jsonObject.getString("draw_code");
                    String drawDate = jsonObject.getString("draw_date");
                   // drawDate = Util.convertLocalDate(drawDate);
                    String blkId = jsonObject.getString("blk_id");
                    String totalNumbers = jsonObject.getString("tot_members");
                    String prizeAmount = "";
                    String drawId = "";
                    MyBlockResultDao myBlockResultDao = new MyBlockResultDao(blkId, drawId, blockType, ticketTypeName, blockName, drawCode, drawDate, totalNumbers, prizeAmount, "", "", "");
                    myBlockResultDaoList.add(myBlockResultDao);
                    blockType = "";
                    myResultAdapter.setData(myBlockResultDaoList);

                }
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                title = jsonObjectDesc.getString(webServiceUtil.title);
                errorMessage = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, errorMessage, 0);
            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.getMessage());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);
        }
    }


}
