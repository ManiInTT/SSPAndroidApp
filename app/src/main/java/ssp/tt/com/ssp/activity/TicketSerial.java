package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.ULocale;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.adapter.TicketAdapter;
import ssp.tt.com.ssp.model.Lottery;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class TicketSerial extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    TextView tvDrawNumber;
    TextView tvTicketType;
    TextView tvSeries;

    TextView tvDrawOn;
    TextView tvTotalTickets;
    TextView tvTotalAmount;


    public int API_TYPE = 1;
    public final int API_ADD_TICKET_DETAILS = 1;
    public final int API_UPDATE_HOLD_TICKET = 2;
    public final int API_UPDATE_UNHOLD_TICKET = 3;
    public final int API_CANCEL_PURCHASE = 4;

    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    WebServiceUtil apiRequest;
    TicketAdapter ticketAdapter;
    String ticketseriesId = "", drawId = "";
    ArrayList<Lottery> lotteryArrayList = new ArrayList<>();
    TextView textCartItemCount;
    int badgeCount = 0;
    int selectPosition = 0;
    String selectedArray = "";
    float lotteryRate = 0;
    public static boolean isloaded = false;
    float userTotalAmount;
    float userCurrentAmount;
    View headerView;
    View footerView;
    TextView btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_serial);
        isloaded = true;
        ButterKnife.bind(this);
        getWidgetConfig();
        getExtras();
        registerBaseActivityReceiver();
        setRecyclerView();
        serviceRequest();
    }


    private void setRecyclerView() {
        // set Adapter
        ticketAdapter = new TicketAdapter(this, lotteryArrayList);
        recyclerView.setAdapter(ticketAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //add header
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_ticket_serial, recyclerView, false);
        tvDrawNumber = headerView.findViewById(R.id.tv_draw_no);
        tvTicketType = headerView.findViewById(R.id.tv_ticket_type);
        tvSeries = headerView.findViewById(R.id.tv_series);
        tvDrawOn = headerView.findViewById(R.id.tv_draw_on);
        Bundle extras = getIntent().getExtras();
        String drawCode = extras.getString(apiRequest.ltDrawCode);
        String drawDate = extras.getString(apiRequest.ltDrawDate);
        String ticketTypeName = extras.getString("ticketTypeName");
        tvDrawNumber.setText("Draw number " + drawCode);
        tvSeries.setText(ticketseriesId + " SERIES");
        tvDrawOn.setText("Draw on " + Util.convertLocalDate(drawDate));
        tvTicketType.setText(ticketTypeName);


        //add footer
        footerView = LayoutInflater.from(this).inflate(R.layout.layout_footer_ticket_serial, recyclerView, false);
        tvTotalTickets = footerView.findViewById(R.id.tv_total_tickets);
        tvTotalAmount = footerView.findViewById(R.id.textView6);
        TextView btnUpdate = footerView.findViewById(R.id.tv_next_day_tickets);
        btnCancel = footerView.findViewById(R.id.btn_cancel);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int oldCount = PreferenceConnector.readInteger(TicketSerial.this, PreferenceConnector.BADGE_COUNT, 0);
//                badgeCount = oldCount + badgeCount;
//                PreferenceConnector.writeInteger(TicketSerial.this, PreferenceConnector.BADGE_COUNT, badgeCount);
//                isloaded = false;
//                Intent intent = getIntent();
//                intent.putExtra("badgeCount", badgeCount);
//                setResult(RESULT_OK, intent);
//                finish();
                startActivity(new Intent(TicketSerial.this, TicketType.class));
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlertDialog();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                selectPosition = position - 1;
                if (lotteryArrayList.get(selectPosition).isTicketSelected()) {
                    userCurrentAmount = userCurrentAmount - lotteryRate;
                    new UpdateUnHold().execute(lotteryArrayList.get(selectPosition).getTicketId());
                } else {
                    userCurrentAmount = userCurrentAmount + lotteryRate;
                    if (userTotalAmount > userCurrentAmount) {
                        new UpdateHold().execute(lotteryArrayList.get(selectPosition).getTicketId());
                    } else {
                        Toast.makeText(getApplicationContext(), "order failed. insufficient balance", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }));


    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        ticketseriesId = extras.getString(apiRequest.ltSeries);
        drawId = extras.getString(apiRequest.ltDrawId);
        selectedArray = extras.getString(apiRequest.selectedArray);
        badgeCount = extras.getInt(apiRequest.cardCount);
        lotteryRate = extras.getFloat(apiRequest.lotteryRate);
        userTotalAmount = PreferenceConnector.readFloat(TicketSerial.this, PreferenceConnector.USER_TOTAL_AMOUNT, 0);
        userCurrentAmount = PreferenceConnector.readFloat(TicketSerial.this, PreferenceConnector.USER_CURRENT_AMOUNT, 0);

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
        toolbar.setTitle(getString(R.string.title_activity_ticket_series));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            new TicketTypeSeries().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(TicketSerial.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }

    /************************************************************************************
     * Class      : Register
     * Use        : Method call service request
     * Created on : 2018-04-07
     * Updated on : 2018-04-07
     **************************************************************************************/
    @SuppressLint("StaticFieldLeak")
    private class TicketTypeSeries extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_ADD_TICKET_DETAILS;
            dialog = ProgressUtil.showDialog(TicketSerial.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getTicketSerial(UserId, imeiNumber, ticketseriesId, drawId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class UpdateHold extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_UPDATE_HOLD_TICKET;
            dialog = ProgressUtil.showDialog(TicketSerial.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String ltId = params[0];
                serviceConnector.updateHold(getApplicationContext(), UserId, imeiNumber, ltId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void callbackReturn(String response) {
        switch (API_TYPE) {
            case API_ADD_TICKET_DETAILS:
                getTicketDetailsResponse(response);
                break;
            case API_UPDATE_HOLD_TICKET:
                getUpdateTicketHoldReponse(response);
                break;
            case API_UPDATE_UNHOLD_TICKET:
                getUpdateTicketUnHoldReponse(response);
                break;
            case API_CANCEL_PURCHASE:
                getCancelPurchase(response);
                break;
        }

    }

    private void getUpdateTicketHoldReponse(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                badgeCount = badgeCount + 1;
                lotteryArrayList.get(selectPosition).setTicketSelected(true);
                ticketAdapter.notifyDataSetChanged();
                if (badgeCount == 0 || badgeCount == 1) {
                    tvTotalTickets.setText(badgeCount + " Ticket");
                } else {
                    tvTotalTickets.setText(badgeCount + " Tickets");
                }
                setupBadge();
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning),e.toString(), 0);

        }
    }

    private void getUpdateTicketUnHoldReponse(String response) {
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                badgeCount = badgeCount - 1;
                lotteryArrayList.get(selectPosition).setTicketSelected(false);
                ticketAdapter.notifyDataSetChanged();
                setupBadge();
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning),  e.toString(), 0);

        }
    }

    private void getTicketDetailsResponse(String response) {
        dialog.dismiss();
        lotteryArrayList.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                String data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    boolean isTickedSelected = false;
                    jsonObject = jsonArray.getJSONObject(tIndex);
                    String ltId = jsonObject.getString(apiRequest.ltId);
                    String ltSeries = jsonObject.getString(apiRequest.ltSeries);
                    String ltNumber = jsonObject.getString(apiRequest.ltNumber);
                    String ltStatusText = jsonObject.getString(apiRequest.ltStatusText);

                    JSONArray jsonSelectedArray = new JSONArray(selectedArray);
                    for (int iIndex = 0; iIndex < jsonSelectedArray.length(); iIndex++) {
                        String tmplsLtId = jsonSelectedArray.getJSONObject(iIndex).getString(apiRequest.tmplsLtId);
                        if (tmplsLtId.equals(ltId)) {
                            isTickedSelected = true;
                        }
                    }
                    lotteryArrayList.add(new Lottery(ltId, ltNumber, ltSeries, isTickedSelected));
                }
                if (lotteryArrayList.size() > 0) {
                    ticketAdapter.setData(lotteryArrayList);
                    ticketAdapter.setHeaderView(headerView);
                    ticketAdapter.setFooterView(footerView);
                    setupBadge();
                } else {
                    WebServiceUtil webServiceUtil = new WebServiceUtil();
                    JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                    String title = jsonObjectDesc.getString(webServiceUtil.title);
                    String description = jsonObjectDesc.getString(webServiceUtil.description);
                    Util.warningAlertDialog(this, title, description, 0);
                }

            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 1);
            }

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning),  e.toString(), 1);
        }
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
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        //setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                int oldCount = PreferenceConnector.readInteger(TicketSerial.this, PreferenceConnector.BADGE_COUNT, 0);
                badgeCount = oldCount + badgeCount;
                PreferenceConnector.writeInteger(TicketSerial.this, PreferenceConnector.BADGE_COUNT, badgeCount);
                isloaded = false;
                intent = getIntent();
                intent.putExtra("badgeCount", badgeCount);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.action_cart:
                intent = new Intent(TicketSerial.this, MyCart.class);
                intent.putExtra("cartItemCount", badgeCount);
                intent.putExtra("cartItemTotal", tvTotalAmount.getText().toString());
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        int oldCount = PreferenceConnector.readInteger(TicketSerial.this, PreferenceConnector.BADGE_COUNT, 0);
        badgeCount = oldCount + badgeCount;
        PreferenceConnector.writeInteger(TicketSerial.this, PreferenceConnector.BADGE_COUNT, badgeCount);
        isloaded = false;
        Intent intent = getIntent();
        intent.putExtra("badgeCount", badgeCount);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (badgeCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(badgeCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }

        if (badgeCount == 0 || badgeCount == 1) {
            tvTotalTickets.setText(badgeCount + " Ticket");
        } else {
            tvTotalTickets.setText(badgeCount + " Tickets");
        }

        tvTotalAmount.setText("" + badgeCount * lotteryRate);

        if(badgeCount==0) {
            btnCancel.setVisibility(View.INVISIBLE);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class UpdateUnHold extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_UPDATE_UNHOLD_TICKET;
            dialog = ProgressUtil.showDialog(TicketSerial.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String ltId = params[0];
                serviceConnector.updateUnHold(getApplicationContext(), UserId, imeiNumber, ltId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.toString());
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);

        }
    }

    private void cancelAlertDialog() {
        final Dialog dialog = new Dialog(TicketSerial.this);
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
            dialog = ProgressUtil.showDialog(TicketSerial.this, getString(R.string.loading_message));
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
