package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.DefaultItemAnimator;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.MyBlockResultAdapter;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyEarningSummary extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    @BindView(R.id.coordinatorLayout)
    RelativeLayout coordinatorLayout;

    @BindView(R.id.tv_total_earning)
    TextView tvTotalEarning;

    ServiceConnector serviceConnector;
    WebServiceUtil apiRequest;
    ProgressDialog dialog;

    MyBlockResultAdapter myBlockResultAdapter;
    private int API_TYPE = 1;
    private int API_PREVIOUS_RESULT = 1;
    private int API_CURRENT_RESULT = 2;

    final ArrayList<MyBlockResultDao> myBlockResultDaoList = new ArrayList<>();
    String errorMessage = "";
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_summary_result);
        registerBaseActivityReceiver();
        ButterKnife.bind(this);
        getWidgetConfig();
        getMyResult();
    }

    private void getMyResult() {
        String totalEarning = getIntent().getStringExtra("TOTAL_EARNING");
        tvTotalEarning.setText(totalEarning);
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new MyPrevBlockResultAPI().execute();
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


    private void getWidgetConfig() {
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_earning_result));
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


    private void setListView() {
        dialog.dismiss();
        if (myBlockResultDaoList.size() > 0) {
            myBlockResultAdapter = new MyBlockResultAdapter(this, myBlockResultDaoList, "EARNING");
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(myBlockResultAdapter);
            myBlockResultAdapter.notifyDataSetChanged();
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new
                    RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int _position) {
                            MyBlockResultDao myBlockResultDao = myBlockResultDaoList.get(_position);
                            Intent intent = new Intent(MyEarningSummary.this, MyBlockBreakDown.class);
                            intent.putExtra("blockId", myBlockResultDao.getBlockId());
                            startActivity(intent);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));
        } else {
            Util.warningAlertDialog(this, title, errorMessage, 0);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    @Override
    public void onClick(View v) {

    }


    @SuppressLint("StaticFieldLeak")
    private class MyPrevBlockResultAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_PREVIOUS_RESULT;

        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.viewPreviousBlockEarning(getApplicationContext(), userId, imeiNumber);
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
            dialog = ProgressUtil.showDialog(MyEarningSummary.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.viewCurrentBlockEarning(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        if (API_TYPE == API_PREVIOUS_RESULT) {
            getPreviousResultResponse(response);
        } else if (API_TYPE == API_CURRENT_RESULT) {
            getCurrentResultResponse(response);
        }
    }

    private void getPreviousResultResponse(String response) {
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            String blockType = "";
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String blkId = jsonObject.getString("blk_id");
                    String blockName = jsonObject.getString("blk_name");
                    String drawCode = jsonObject.getString("draw_code");
                    String drawDate = jsonObject.getString("draw_date");
                    drawDate = Util.convertLocalDate(drawDate);
                    String ticketTypeName = jsonObject.getString("ltype_name");
                    String prizeAmount = jsonObject.getString("be_earn_amt");
                    MyBlockResultDao myBlockResultDao = new MyBlockResultDao(blkId, "", blockType, ticketTypeName, blockName, drawCode, drawDate, prizeAmount, prizeAmount, "", "", "");
                    myBlockResultDaoList.add(myBlockResultDao);

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
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String blockType = "";
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String blkId = jsonObject.getString("blk_id");
                    String blockName = jsonObject.getString("blk_name");
                    String drawCode = jsonObject.getString("draw_code");
                    String drawDate = jsonObject.getString("draw_date");
                    drawDate = Util.convertLocalDate(drawDate);
                    String ticketTypeName = jsonObject.getString("ltype_name");
                    String prizeAmount = jsonObject.getString("be_earn_amt");
                    MyBlockResultDao myBlockResultDao = new MyBlockResultDao(blkId, "", blockType, ticketTypeName, blockName, drawCode, drawDate, prizeAmount, prizeAmount, "", "", "");
                    myBlockResultDaoList.add(myBlockResultDao);
                }
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(webServiceUtil.desc);
                title = jsonObjectDesc.getString(webServiceUtil.title);
                errorMessage = jsonObjectDesc.getString(webServiceUtil.description);
            }
            setListView();
        } catch (JSONException mException) {
            Log.i("JSONException", mException.getMessage());
        }
    }


}
