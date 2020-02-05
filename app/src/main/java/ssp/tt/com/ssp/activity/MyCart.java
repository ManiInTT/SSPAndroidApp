package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.PurchaseAdapter;
import ssp.tt.com.ssp.model.PurchaseHistory;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyCart extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    @BindView(R.id.tv_total_tickets)
    TextView tvTotalTickets;

    @BindView(R.id.textView6)
    TextView tvTotalAmount;

    @BindView(R.id.tv_next_day_tickets)
    TextView tvNextDayTickets;

    @BindView(R.id.btn_cancel)
    TextView tvCancel;

    public int API_TYPE = 1;
    public final int API_PURCHASE_SUMMARY = 1;
    public final int API_CONFIRM = 2;
    public final int API_CANCEL_PURCHASE = 3;


    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    WebServiceUtil apiRequest;
    PurchaseAdapter purchaseAdapter;
    ArrayList<PurchaseHistory> purchaseHistoryArrayList = new ArrayList<>();
    TextView textCartItemCount;

    int cartItemCount;
    String cartItemTotal;

    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        ButterKnife.bind(this);
        getWidgetConfig();
        registerBaseActivityReceiver();
        getExtra();
        serviceRequest();
    }

    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new PurchaseSummary().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(MyCart.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }

    private void getExtra() {
        cartItemCount = getIntent().getIntExtra("cartItemCount", 0);
        cartItemTotal = getIntent().getStringExtra("cartItemTotal");
        tvTotalAmount.setText("" + cartItemTotal);
    }

    @OnClick(R.id.tv_next_day_tickets)
    public void setTvConfirm(View v) {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new ConfirmSummary().execute();
        }
    }

    @OnClick(R.id.btn_cancel)
    public void setCancel(View v) {
        cancelAlertDialog();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
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
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_my_cart));
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        textCartItemCount.setVisibility(View.GONE);
        setupBadge();
        return true;
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (cartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(cartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }

        if (cartItemCount == 0 || cartItemCount == 1) {
            tvTotalTickets.setText(cartItemCount + " Ticket");
        } else {
            tvTotalTickets.setText(cartItemCount + " Tickets");
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class ConfirmSummary extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_CONFIRM;
            dialog = ProgressUtil.showDialog(MyCart.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                HashMap<String, String> map = new HashMap<>();
                for (int index = 0; index < purchaseHistoryArrayList.size(); index++) {
                    map.put("lottery[" + index + "]", purchaseHistoryArrayList.get(index).getTmplsLtId());
                }
                Log.i("ArrayList", map.toString());
                serviceConnector.confirmPurchase(getApplicationContext(), userId, imeiNumber, map);
            } catch (Exception e) {
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class PurchaseSummary extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_PURCHASE_SUMMARY;
            dialog = ProgressUtil.showDialog(MyCart.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.purchaseSummary(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void callbackReturn(String response) {
        switch (API_TYPE) {
            case API_PURCHASE_SUMMARY:
                getPurchaseSummary(response);
                break;
            case API_CONFIRM:
                getConfirmSummary(response);
                break;
            case API_CANCEL_PURCHASE:
                getCancelPurchase(response);
                break;

        }
    }

    private void getConfirmSummary(String response) {
        dialog.dismiss();
        WebServiceUtil webServiceUtil = new WebServiceUtil();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                JSONObject objectJSONObject = jsonObject.getJSONObject(webServiceUtil.data);
                String lottery_units = "0";
                if (objectJSONObject.has("lottery_units")) {
                    lottery_units = objectJSONObject.getString("lottery_units");
                    String net_amt = objectJSONObject.getString("net_amt");
                    String available_amt = objectJSONObject.getString("avaliable_amt");
                    String balance_amt = objectJSONObject.getString("balance_amt");
                    Intent intent = new Intent(MyCart.this, ssp.tt.com.ssp.activity.PurchaseSummary.class);
                    intent.putExtra("data", data);
                    intent.putExtra("cartItemCount", cartItemCount);
                    intent.putExtra("cartItemTotal", cartItemTotal);
                    intent.putExtra("lottery_units", lottery_units);
                    intent.putExtra("available_amt", available_amt);
                    intent.putExtra("net_amt", net_amt);
                    intent.putExtra("balance_amt", balance_amt);
                    startActivity(intent);
                }else{
                    Util.warningAlertDialog(this, getResources().getString(R.string.warning), "You don't have lottery units", 0);
                }
            } else {
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String errorMessage = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, errorMessage, 0);
            }

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);
        }

    }


    private void getPurchaseSummary(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);

                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    jsonObject = jsonArray.getJSONObject(tIndex);
                    String tmplsLtId = jsonObject.getString(apiRequest.tmplsLtId);
                    String tmplsSelectedOn = jsonObject.getString(apiRequest.tmplsSelectedOn);
                    String lTypeName = jsonObject.getString(apiRequest.lTypeName);
                    String ltSeries = jsonObject.getString(apiRequest.ltSeries);
                    String ltNumber = jsonObject.getString(apiRequest.ltNumber);
                    String ltDrawCode = jsonObject.getString(apiRequest.ltDrawCode);
                    String ltRate = jsonObject.getString(apiRequest.ltRate);
                    purchaseHistoryArrayList.add(new PurchaseHistory(tmplsLtId, tmplsSelectedOn, lTypeName, ltSeries, ltNumber, ltDrawCode, ltRate));
                }
                if (purchaseHistoryArrayList.size() > 0) {
                    purchaseAdapter = new PurchaseAdapter(purchaseHistoryArrayList);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(purchaseAdapter);
                    purchaseAdapter.notifyDataSetChanged();
                    cartItemCount = purchaseHistoryArrayList.size();
                    setupBadge();
                }

            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String errorMessage = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, errorMessage, 0);
            }

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);
        }
    }


    private void getCancelPurchase(String response) {
        try {
            dialog.dismiss();
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                closeAllActivities();
                Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String errorMessage = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, errorMessage, 0);
            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.toString());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }

    private void cancelAlertDialog() {
        final Dialog dialog = new Dialog(MyCart.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog_error);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(getResources().getString(R.string.cancel_purchase_message));
        tvTitle.setText(getResources().getString(R.string.cancel_purchase_title));
        TextView btnYes = dialog.findViewById(R.id.yes_btn);
        btnYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                new CancelPurchase().execute();
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

    @SuppressLint("StaticFieldLeak")
    private class CancelPurchase extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_CANCEL_PURCHASE;
            dialog = ProgressUtil.showDialog(MyCart.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.cancelPurchase(getApplicationContext(), userId, imeiNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
