package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
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
import ssp.tt.com.ssp.adapter.GridSpacingItemDecoration;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.adapter.TicketSeriousAdapter;
import ssp.tt.com.ssp.model.LotterySeries;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class TicketSeries extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    TextView tvTotalTickets;
    TextView tvTotalAmount;


    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    WebServiceUtil apiRequest;
    String ticketTypeId = "";
    String ticketRate = "";
    ArrayList<LotterySeries> lotterySeriesArrayList;
    public static final int REQUEST_CODE = 1;
    TextView textCartItemCount;

    public int API_TYPE = 1;
    public final int API_TICKET_SERIOUS = 1;
    public final int API_PURCHASE_HISTORY = 2;
    public final int API_CANCEL_PURCHASE = 3;
    TicketSeriousAdapter lotterySeriousAdapter;
    int cartItemCount = 0;
    int ticketSeriousItemCount = 0;
    View headerView;
    View footerView;
    String ticketTypeName = "";
    String ticketDate = "";
    TextView btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_series);
        TicketSerial.isloaded = false;
        registerBaseActivityReceiver();
        ButterKnife.bind(this);
        getWidgetConfig();
        getExtras();
        setRecyclerView();
        serviceRequest();
    }


    private void setRecyclerView() {
        // set Adapter
        lotterySeriousAdapter = new TicketSeriousAdapter(this, lotterySeriesArrayList);
        recyclerView.setAdapter(lotterySeriousAdapter);

        // setLayoutManager:LinearLayoutManager
        int spacingInPixels = 0;
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false, 0));
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if ((lotterySeriousAdapter.hasHeader() && lotterySeriousAdapter.isHeader(position)) ||
                        lotterySeriousAdapter.hasFooter() && lotterySeriousAdapter.isFooter(position))
                    return manager.getSpanCount();

                return 1;
            }
        });

        //add header
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_ticket_serious, recyclerView, false);
        TextView tvTicketType = headerView.findViewById(R.id.tv_ticket_type);
        TextView tvDrawNo = headerView.findViewById(R.id.tv_draw_no);
        TextView tvDrawOn = headerView.findViewById(R.id.tv_draw_on);
        tvTicketType.setText(ticketTypeName.toUpperCase());
        tvDrawNo.setText("Draw Number");
        tvDrawOn.setText("Draw on " + Util.convertLocalDate(ticketDate));

        //add footer
        footerView = LayoutInflater.from(this).inflate(R.layout.layout_footer_carddetails, recyclerView, false);
        tvTotalTickets = footerView.findViewById(R.id.tv_total_tickets);
        tvTotalAmount = footerView.findViewById(R.id.textView6);
        TextView btnUpdate = footerView.findViewById(R.id.btn_update);
        btnCancel = footerView.findViewById(R.id.btn_cancel);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketSeries.this, MyCart.class);
                intent.putExtra("cartItemCount", cartItemCount);
                intent.putExtra("cartItemTotal", tvTotalAmount.getText().toString());
                startActivity(intent);
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
                try {
                    LotterySeries lotterySeries = lotterySeriesArrayList.get(position - 1);
                    Intent intent = new Intent(TicketSeries.this, TicketSerial.class);
                    intent.putExtra(apiRequest.ltSeries, lotterySeries.getLotterySeries());
                    intent.putExtra(apiRequest.ltDrawId, lotterySeries.getLotteryId());
                    intent.putExtra(apiRequest.ltDrawCode, lotterySeries.getLotteryDrawCode());
                    intent.putExtra(apiRequest.ltDrawDate, lotterySeries.getLotteryDrawDate());
                    intent.putExtra(apiRequest.selectedArray, lotterySeries.getJsonArray().toString());
                    intent.putExtra(apiRequest.seriousCount, lotterySeries.getLotteryCount());
                    intent.putExtra(apiRequest.lotteryRate, lotterySeries.getLotteryRate());
                    intent.putExtra("ticketTypeName", ticketTypeName);
                    intent.putExtra(apiRequest.cardCount, cartItemCount);
                    startActivityForResult(intent, REQUEST_CODE);

                } catch (Exception e) {

                }

            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));
    }


    private void getExtras() {
        PreferenceConnector.writeInteger(this, PreferenceConnector.BADGE_COUNT, 0);
        Bundle extras = getIntent().getExtras();
        ticketTypeId = extras.getString("ticketTypeId");
        ticketRate = extras.getString("ticketRate");
        ticketTypeName = extras.getString("ticketTypeName");
        ticketDate = extras.getString("ticketDate");
    }


    private void getWidgetConfig() {
        lotterySeriesArrayList = new ArrayList<>();
        util = new Util();
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_ticket_series));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

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
            group.setBackgroundColor(ContextCompat.getColor(TicketSeries.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);

            snackbar.show();
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class TicketTypeSeries extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_TICKET_SERIOUS;
            dialog = ProgressUtil.showDialog(TicketSeries.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getTicketSeries(UserId, imeiNumber, ticketTypeId, getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class PurchaseSummary extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_PURCHASE_HISTORY;
            dialog = ProgressUtil.showDialog(TicketSeries.this, getString(R.string.loading_message));
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
            case API_TICKET_SERIOUS:
                getTicketSeriousResponse(response);
                break;
            case API_PURCHASE_HISTORY:
                getPurchaseHistory(response);
                break;
            case API_CANCEL_PURCHASE:
                getCancelPurchase(response);
                break;
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

    private void getPurchaseHistory(String response) {
        cartItemCount = 0;
        float totalAmount = 0;
        float lotteryRate = 0;
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                PreferenceConnector.writeString(this, PreferenceConnector.PURCHASE_HISTORY, response);
                String data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);
                cartItemCount = jsonArray.length();
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    jsonObject = jsonArray.getJSONObject(tIndex);
                    String ltSeries = jsonObject.getString(apiRequest.ltSeries);
                    float ltRate = Float.valueOf(jsonObject.getString(apiRequest.ltRate));
                    lotteryRate = Float.valueOf(jsonObject.getString(apiRequest.ltRate));
                    for (int iIndex = 0; iIndex < lotterySeriesArrayList.size(); iIndex++) {
                        String lotterySeries = lotterySeriesArrayList.get(iIndex).getLotterySeries();
                        float seroiusAmount = lotterySeriesArrayList.get(iIndex).getSeriousAmount();
                        int count = lotterySeriesArrayList.get(iIndex).getLotteryCount();
                        lotterySeriesArrayList.get(iIndex).setLotteryRate(ltRate);
                        if (lotterySeries.equals(ltSeries)) {
                            ticketSeriousItemCount = ticketSeriousItemCount + 1;
                            lotterySeriesArrayList.get(iIndex).getJsonArray().put(jsonObject);
                            lotterySeriesArrayList.get(iIndex).setLotteryCount(count + 1);
                            lotterySeriesArrayList.get(iIndex).setSeriousAmount(seroiusAmount + ltRate);
                        }
                    }
                }
            }
            totalAmount = cartItemCount * lotteryRate;
            setTotal(totalAmount);
            setupBadge(cartItemCount);
            lotterySeriousAdapter.setData(lotterySeriesArrayList);
            lotterySeriousAdapter.setHeaderView(headerView);
            lotterySeriousAdapter.setFooterView(footerView);

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);
        }
    }

    private void setTotal(float totalAmount) {
        tvTotalAmount.setText(" " + totalAmount);
        PreferenceConnector.writeFloat(this, PreferenceConnector.USER_CURRENT_AMOUNT, Float.valueOf(totalAmount));
    }

    private void getTicketSeriousResponse(String response) {
        dialog.dismiss();
        lotterySeriesArrayList.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                String data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);
                JSONArray jsonArrayTicket = new JSONArray();
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    jsonObject = jsonArray.getJSONObject(tIndex);
                    String ltId = jsonObject.getString(apiRequest.ltDrawId);
                    String ltDrawCode = jsonObject.getString(apiRequest.ltDrawCode);
                    String ltDrawDate = jsonObject.getString(apiRequest.ltDrawDate);
                    String ltSeries = jsonObject.getString(apiRequest.ltSeries);
                    int rate = 0;
                    try {
                        rate = Integer.valueOf(ticketRate);
                    } catch (Exception e) {

                    }
                    lotterySeriesArrayList.add(new LotterySeries(ltId, ltSeries, ltDrawCode, ltDrawDate, 0, rate, 0, jsonArrayTicket));

                }
                lotterySeriousAdapter.setData(lotterySeriesArrayList);
                new PurchaseSummary().execute();
            } else {
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cart:
                Intent intent = new Intent(TicketSeries.this, MyCart.class);
                intent.putExtra("cartItemCount", cartItemCount);
                intent.putExtra("cartItemTotal", tvTotalAmount.getText().toString());
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TicketSerial.isloaded = false;
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                clearData();
                serviceRequest();
            }
        } catch (Exception ex) {
            Toast.makeText(TicketSeries.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void clearData() {
        lotterySeriesArrayList.clear();
        lotterySeriousAdapter.notifyDataSetChanged();
    }


    private void setupBadge(int badgeCount) {

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

        if (badgeCount == 0 || badgeCount == 1) {
            tvTotalTickets.setText(badgeCount + " Ticket");
        } else {
            tvTotalTickets.setText(badgeCount + " Tickets");
        }

        if (cartItemCount == 0) {
            btnCancel.setVisibility(View.INVISIBLE);
        }
    }

    private void cancelAlertDialog() {
        final Dialog dialog = new Dialog(TicketSeries.this);
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
            dialog = ProgressUtil.showDialog(TicketSeries.this, getString(R.string.loading_message));
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
