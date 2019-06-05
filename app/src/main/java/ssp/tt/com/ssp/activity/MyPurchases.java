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
import ssp.tt.com.ssp.adapter.MyBlockResultNewAdapter;
import ssp.tt.com.ssp.adapter.MyPurchasesAdapter;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.model.MyPurchasesModel;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyPurchases extends BaseActivity {

    @BindView(R.id.gv_my_purchases)
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

    MyPurchasesAdapter myResultAdapter;
    final ArrayList<MyPurchasesModel> myPurchasesModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        setRecyclerView();
        getMyResult();
        new MyPurchasesAPI().execute();

    }

    private void getMyResult() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new MyPurchasesAPI().execute();
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
        myResultAdapter = new MyPurchasesAdapter(this, myPurchasesModelArrayList);
        recyclerView.setAdapter(myResultAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    MyPurchasesModel myPurchasesModel = myPurchasesModelArrayList.get(position - 1);
                    Intent intent = new Intent(MyPurchases.this, MyBlockDetails.class);
                    intent.putExtra("purId", myPurchasesModel.getPur_id());
                    startActivityForResult(intent, 1);
                } catch (Exception e) {

                }

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }



    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_my_purchases));
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
    private class MyPurchasesAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_CURRENT_RESULT;
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.viewMyPurchases(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
       if (API_TYPE == API_CURRENT_RESULT) {
            getMyPurchasesResponse(response);
        }
    }




    private void getMyPurchasesResponse(String response) {
       // dialog.dismiss();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String blockType = "";
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONArray data = mJSONObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    String pur_id = jsonObject.getString("pur_id");
                    String trans_amount = jsonObject.getString("trans_amount");
                    String no_of_units = jsonObject.getString("no_of_units");
                    String pur_date = jsonObject.getString("pur_date");
                    pur_date = Util.convertLocalDate(pur_date);

                    MyPurchasesModel myPurchasesModel = new MyPurchasesModel(pur_id, pur_date, trans_amount, no_of_units);
                    myPurchasesModelArrayList.add(myPurchasesModel);

                    myResultAdapter.setData(myPurchasesModelArrayList);

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
