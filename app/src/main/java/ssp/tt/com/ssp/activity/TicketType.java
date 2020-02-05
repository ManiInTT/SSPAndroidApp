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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.GridSpacingItemDecoration;
import ssp.tt.com.ssp.adapter.RecyclerTouchListener;
import ssp.tt.com.ssp.adapter.TicketTypeAdapter;
import ssp.tt.com.ssp.model.Ticket;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class TicketType extends BaseActivity {

    @BindView(R.id.gv_ticket_type)
    RecyclerView recyclerView;

    @BindView(R.id.linearLayout)
    RelativeLayout linearLayout;

    TicketTypeAdapter ticketTypeAdapter;

    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;

    WebServiceUtil apiRequest;
    ArrayList<Ticket> ticketArrayList;

    public int API_TYPE = 1;
    public final int API_TICKET_TYPE = 1;
    public final int API_PURCHASE_HISTORY = 2;
    public final int API_CANCEL_PURCHASE = 3;

    int cartItemCount = 0;
    View headerView;
    View footerView;

    TextView tvTotalTickets;
    TextView tvTotalAmount;
    TextView textCartItemCount;
    TextView btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_type);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceRequest();
    }

    private void setRecyclerView() {
        // set Adapter
        ticketTypeAdapter = new TicketTypeAdapter(this, ticketArrayList);
        recyclerView.setAdapter(ticketTypeAdapter);

        // setLayoutManager:LinearLayoutManager
        int spacingInPixels = 0;
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if ((ticketTypeAdapter.hasHeader() && ticketTypeAdapter.isHeader(position)) ||
                        ticketTypeAdapter.hasFooter() && ticketTypeAdapter.isFooter(position))
                    return manager.getSpanCount();

                return 1;
            }
        });

        //add header
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_header_ticket_type, recyclerView, false);
        ticketTypeAdapter.setHeaderView(headerView);

        //add footer
        footerView = LayoutInflater.from(this).inflate(R.layout.layout_footer, recyclerView, false);
        tvTotalTickets = footerView.findViewById(R.id.tv_total_tickets);
        tvTotalAmount = footerView.findViewById(R.id.textView6);
        TextView btnUpdate = footerView.findViewById(R.id.btn_update);
        btnCancel = footerView.findViewById(R.id.btn_cancel);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketType.this, MyCart.class);
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

        ticketTypeAdapter.setFooterView(footerView);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                try {
                    Ticket ticket = ticketArrayList.get(position - 1);
                    PreferenceConnector.writeString(getApplicationContext(), "ticketRate", ticket.getTicketRate());
                    Intent intent = new Intent(TicketType.this, TicketSeries.class);
                    intent.putExtra("ticketTypeId", ticket.getTicketId());
                    intent.putExtra("ticketTypeName", ticket.getTicketName());
                    intent.putExtra("ticketRate", ticket.getTicketRate());
                    intent.putExtra("ticketDate", ticket.getTicketDrawDate());
                    startActivityForResult(intent, 1);
                } catch (Exception e) {

                }

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
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
        toolbar.setTitle(getString(R.string.title_activity_ticket_type));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ticketArrayList = new ArrayList<>();
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
            new getTicketTypenAsync().execute();
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
            group.setBackgroundColor(ContextCompat.getColor(TicketType.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
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
    private class getTicketTypenAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_TICKET_TYPE;
            dialog = ProgressUtil.showDialog(TicketType.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getTicketType(UserId, imeiNumber, getApplicationContext());
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

    @SuppressLint("StaticFieldLeak")
    private class CancelPurchase extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_CANCEL_PURCHASE;
            dialog = ProgressUtil.showDialog(TicketType.this, getString(R.string.loading_message));
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


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        switch (API_TYPE) {
            case API_TICKET_TYPE:
                getTicketTypeResponse(response);
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

    private void getTicketTypeResponse(String response) {
        try {
            ticketArrayList.clear();
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                String data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(tIndex);
                    String ticketId = jsonObject1.getString(apiRequest.ltTypeId);
                    String ticketRate = jsonObject1.getString(apiRequest.ltTypeRate);
                    String ticketName = jsonObject1.getString(apiRequest.TICKET_NAME);
                    String ticketType = jsonObject1.getString(apiRequest.TICKET_TYPE);
                    String drawDate = jsonObject1.getString(apiRequest.draw_date);
                    String grand_prize = jsonObject1.getString(apiRequest.grand_prize);
                    String drawCode = jsonObject1.getString(apiRequest.ltDrawCode);
                    ticketArrayList.add(new Ticket(ticketId, ticketName, ticketType, ticketRate, drawDate, grand_prize, drawCode, 0));
                }
                ticketTypeAdapter.setData(ticketArrayList);
                new PurchaseSummary().execute();
            } else {
                dialog.dismiss();
                WebServiceUtil webServiceUtil = new WebServiceUtil();
                JSONObject jsonObjectDesc = jsonObject.getJSONObject(webServiceUtil.desc);
                String title = jsonObjectDesc.getString(webServiceUtil.title);
                String description = jsonObjectDesc.getString(webServiceUtil.description);
                Util.warningAlertDialog(this, title, description, 0);
            }
        } catch (Exception e) {
            dialog.dismiss();
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);

        }
    }

    private void getPurchaseHistory(String response) {
        cartItemCount = 0;
        dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                PreferenceConnector.writeString(this, PreferenceConnector.PURCHASE_HISTORY, response);
                String data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);
                float totalAmount = 0;
                cartItemCount = jsonArray.length();
                float ltRate = 0;
                for (int iIndex = 0; iIndex < ticketArrayList.size(); iIndex++) {
                    String ticketId = ticketArrayList.get(iIndex).getTicketId();
                    for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                        jsonObject = jsonArray.getJSONObject(tIndex);
                        String ltTypeId = jsonObject.getString(apiRequest.ltTypeId);
                        ltRate = Float.valueOf(jsonObject.getString(apiRequest.ltRate));
                        int count = ticketArrayList.get(iIndex).getSelectedTicket();
                        if (ltTypeId.equals(ticketId)) {
                            ticketArrayList.get(iIndex).setSelectedTicket(count + 1);
                        }
                    }
                }
                totalAmount = cartItemCount * ltRate;
                tvTotalAmount.setText(" " + totalAmount);
            }
            ticketTypeAdapter.notifyDataSetChanged();
            setupBadge(cartItemCount);

        } catch (Exception e) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), e.toString(), 0);
        }
    }


    private void setupBadge(int badgeCount) {
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
        if (cartItemCount == 0) {
            btnCancel.setVisibility(View.INVISIBLE);
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
                Intent intent = new Intent(TicketType.this, MyCart.class);
                intent.putExtra("cartItemCount", cartItemCount);
                intent.putExtra("cartItemTotal", tvTotalAmount.getText().toString());
                startActivity(intent);
                return true;

        }
        return false;
    }


    private void cancelAlertDialog() {
        final Dialog dialog = new Dialog(TicketType.this);
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

}
