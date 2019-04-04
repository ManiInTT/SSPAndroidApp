package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class PurchaseSummary extends BaseActivity {

    @BindView(R.id.tv_total_tickets)
    TextView tvTotalTickets;

    @BindView(R.id.textView6)
    TextView tvTotalAmount;

    @BindView(R.id.tv_unit)
    TextView tvUnit;

    @BindView(R.id.tv_available_amount)
    TextView tvAvailableAmount;

    @BindView(R.id.tv_balance_after_purchase)
    TextView tvBalanceAfterPurchase;

    @BindView(R.id.tv_net_amount)
    TextView tvNetAmount;

    @BindView(R.id.cb_terms)
    CheckBox cbTerms;

    public int API_TYPE = 1;
    public final int API_PURCHASE = 1;
    public final int API_TERMS = 2;


    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    WebServiceUtil apiRequest;

    int cartItemCount;
    String cartItemTotal;
    String data = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_summary);
        ButterKnife.bind(this);
        getWidgetConfig();
        registerBaseActivityReceiver();
        getExtra();
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

    private void getExtra() {
        data = getIntent().getStringExtra("data");
        cartItemCount = getIntent().getIntExtra("cartItemCount", 0);
        cartItemTotal = getIntent().getStringExtra("cartItemTotal");
        tvTotalAmount.setText("" + cartItemTotal);
        tvTotalTickets.setText(cartItemCount + " Tickets");
        String lottery_units = getIntent().getStringExtra("lottery_units");
        String available_amt = getIntent().getStringExtra("available_amt");
        String net_amt = getIntent().getStringExtra("net_amt");
        String balance_amt = getIntent().getStringExtra("balance_amt");
        tvUnit.setText(lottery_units);
        tvAvailableAmount.setText(available_amt);
        tvNetAmount.setText(net_amt);
        tvBalanceAfterPurchase.setText(balance_amt);
    }

    @OnClick(R.id.tv_pay)
    public void setTvConfirm(View v) {
        if (!cbTerms.isChecked()) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_terms), 0);
        } else {
            isInternetPresent = mConnectionDetector.isNetworkAvailable();
            if (isInternetPresent) {
                new LotteryPurchase().execute();
            } else {
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), "No Network Connection !", 0);
            }
        }

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
        toolbar.setTitle(getString(R.string.purchase_summary));
        setSupportActionBar(toolbar);
    }


    @Override
    public void callbackReturn(String response) {
        if (API_TYPE == API_PURCHASE) {
            dialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt(apiRequest.statusCode);
                if (code == apiRequest.codeSuccess) {
                    closeAllActivities();
                    Intent intent = new Intent(PurchaseSummary.this, Dashboard.class);
                    startActivity(intent);
                } else {
                    WebServiceUtil webServiceUtil = new WebServiceUtil();
                    JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                    String title = jsonObjectDesc.getString(webServiceUtil.title);
                    String description = jsonObjectDesc.getString(webServiceUtil.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }
            } catch (Exception e) {
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 1);

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
                    final Dialog dialog = new Dialog(PurchaseSummary.this);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }


    @SuppressLint("StaticFieldLeak")
    private class LotteryPurchase extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_PURCHASE;
            dialog = ProgressUtil.showDialog(PurchaseSummary.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                JSONArray jsonArray = new JSONArray(data);
                HashMap<String, String> map = new HashMap<>();
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(tIndex);
                    String tmplsLtId = jsonObject.getString(apiRequest.tmplsLtId);
                    map.put("lottery[" + tIndex + "]", tmplsLtId);
                }
                Log.i("ArrayList", map.toString());
                serviceConnector.lotteryPurchase(getApplicationContext(), userId, imeiNumber, map);
            } catch (Exception e) {
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
            dialog = ProgressUtil.showDialog(PurchaseSummary.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String termsOf = "purchase";
                serviceConnector.termsRequestAPI(getApplicationContext(), UserId, imeiNumber, termsOf);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
