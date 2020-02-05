package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.MyBlockResultAdapter;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.model.MyBlockResultDao;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyBlockBreakDown extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.coordinatorLayout)
    RelativeLayout coordinatorLayout;


    @BindView(R.id.ticket_name)
    TextView ticketName;

    @BindView(R.id.block_name)
    TextView tvblockName;

    @BindView(R.id.drawcode_date)
    TextView drawcodeDate;

    @BindView(R.id.tv_prize)
    TextView tvPrize;

    @BindView(R.id.tv_member_of_block)
    TextView tvMemberOfBlock;

    @BindView(R.id.tv_first_prize_txt)
    TextView tvFirstPrizeTxt;

    @BindView(R.id.tv_first_prize)
    TextView tvFirstPrize;

    @BindView(R.id.tv_second_prize_txt)
    TextView tvSecondPrizeTxt;

    @BindView(R.id.tv_second_prize)
    TextView tvSecondPrize;

    @BindView(R.id.tv_third_prize_txt)
    TextView tvThirdPrizeTxt;

    @BindView(R.id.tv_third_prize)
    TextView tvThirdPrize;

    @BindView(R.id.tv_total_prize)
    TextView tvTotalPrize;

    @BindView(R.id.tv_block_commision)
    TextView tvBlockCommision;

    @BindView(R.id.tv_individual_commision_details)
    TextView tvIndividualCommisionDetails;

    @BindView(R.id.tv_individual_commision)
    TextView tvIndividualCommision;

    @BindView(R.id.tv_third_prize_view)
    RelativeLayout tvThirdPrizeView;

    @BindView(R.id.tv_second_prize_view)
    RelativeLayout tvSecondPrizeView;

    @BindView(R.id.tv_first_prize_view)
    RelativeLayout tvFirstPrizeView;

    @BindView(R.id.tv_first_view)
    View tvFirstView;

    @BindView(R.id.tv_second_view)
    View tvSecondView;

    @BindView(R.id.tv_third_view)
    View tvThirdView;

    ServiceConnector serviceConnector;
    WebServiceUtil apiRequest;
    ProgressDialog dialog;

    String errorMessage = "";
    String blockId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_down);
        registerBaseActivityReceiver();
        ButterKnife.bind(this);
        getWidgetConfig();
        getMyResult();

    }


    private void getMyResult() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new EarningBreakDownAPI().execute();
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
        blockId = getIntent().getStringExtra("blockId");
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_earning_breakdown));
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
    public void onClick(View v) {

    }


    @SuppressLint("StaticFieldLeak")
    private class EarningBreakDownAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(MyBlockBreakDown.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.earningBreakDown(getApplicationContext(), userId, imeiNumber, blockId);
            } catch (Exception e) {
            }
            return null;
        }
    }


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        dialog.dismiss();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            Log.i("response", response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                JSONObject jsonObject = mJSONObject.getJSONObject("data");
                JSONObject blockInfo = jsonObject.getJSONObject("0");
                String blockName = blockInfo.getString("blk_name");
                String ticketTypeName = blockInfo.getString("ltype_name");
                String drawCode = blockInfo.getString("draw_code");
                String drawDate = blockInfo.getString("draw_date");
                drawDate = Util.convertLocalDate(drawDate);
                String amount = blockInfo.getString("be_earn_amt");
                String totalNumbers = blockInfo.getString("tot_members");
                ticketName.setText(ticketTypeName);
                tvblockName.setText(blockName);
                drawcodeDate.setText(drawCode + " & " + drawDate);
                tvPrize.setText(amount);
                tvMemberOfBlock.setText(totalNumbers);

                JSONObject priceBreakdown = jsonObject.getJSONObject("price_breakdown");
                String netTotalPrize = priceBreakdown.getString("net_total_prize");
                String blkCommission = priceBreakdown.getString("blk_commission");
                String individualCommission = priceBreakdown.getString("individual_commission");
                tvTotalPrize.setText(netTotalPrize);
                tvBlockCommision.setText(blkCommission);
                tvIndividualCommision.setText(individualCommission);
                tvIndividualCommisionDetails.setText("(" + blkCommission + " / 100)");


                if (priceBreakdown.has("0")) {
                    JSONObject firstPrize = priceBreakdown.getJSONObject("0");
                    String pbreakVolume = firstPrize.getString("pbreak_volume");
                    String pbreakValue = firstPrize.getString("pbreak_value");
                    String totalPrizeAmt = firstPrize.getString("total_prize_amt");
                    setFirstPrize(pbreakVolume, pbreakValue, totalPrizeAmt);
                }

                if (priceBreakdown.has("1")) {
                    JSONObject secondPrize = priceBreakdown.getJSONObject("1");
                    String pbreakVolume = secondPrize.getString("pbreak_volume");
                    String pbreakValue = secondPrize.getString("pbreak_value");
                    String totalPrizeAmt = secondPrize.getString("total_prize_amt");
                    setSecondPrize(pbreakVolume, pbreakValue, totalPrizeAmt);
                }

                if (priceBreakdown.has("2")) {
                    JSONObject thirdPrize = priceBreakdown.getJSONObject("2");
                    String pbreakVolume = thirdPrize.getString("pbreak_volume");
                    String pbreakValue = thirdPrize.getString("pbreak_value");
                    String totalPrizeAmt = thirdPrize.getString("total_prize_amt");
                    setThirdPrize(pbreakVolume, pbreakValue, totalPrizeAmt);
                }

            } else {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 1);
            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.getMessage());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }

    private void setThirdPrize(String pbreakVolume, String pbreakValue, String totalPrizeAmt) {
        if (tvFirstPrize.getText().toString().length() > 0 && tvSecondPrize.getText().toString().length() > 0) {
            tvThirdPrizeTxt.setText("Third price(" + pbreakVolume + "x" + pbreakValue + ")");
            tvThirdPrize.setText(totalPrizeAmt);
            tvThirdPrizeView.setVisibility(View.VISIBLE);
            tvFirstView.setVisibility(View.VISIBLE);
        } else if (tvFirstPrize.getText().toString().length() > 0) {
            tvSecondPrizeTxt.setText("Second price(" + pbreakVolume + "x" + pbreakValue + ")");
            tvSecondPrize.setText(totalPrizeAmt);
            tvSecondPrizeView.setVisibility(View.VISIBLE);
            tvSecondView.setVisibility(View.VISIBLE);
        } else {
            setFirstPrize(pbreakVolume, pbreakValue, totalPrizeAmt);
        }
    }

    private void setSecondPrize(String pbreakVolume, String pbreakValue, String totalPrizeAmt) {
        if (tvFirstPrize.getText().toString().length() > 0) {
            tvSecondPrizeTxt.setText("Second price(" + pbreakVolume + "x" + pbreakValue + ")");
            tvSecondPrize.setText(totalPrizeAmt);
            tvSecondPrizeView.setVisibility(View.VISIBLE);
            tvSecondView.setVisibility(View.VISIBLE);
        } else {
            setFirstPrize(pbreakVolume, pbreakValue, totalPrizeAmt);
        }
    }

    private void setFirstPrize(String pbreakVolume, String pbreakValue, String totalPrizeAmt) {
        tvFirstPrizeTxt.setText("First price(" + pbreakVolume + "x" + pbreakValue + ")");
        tvFirstPrize.setText(totalPrizeAmt);
        tvFirstPrizeView.setVisibility(View.VISIBLE);
        tvFirstView.setVisibility(View.VISIBLE);
    }


}
