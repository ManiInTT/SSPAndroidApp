package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

public class OverAllBlock extends BaseActivity {

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

    //Time format
    int pYear, pMonth, pDate;
    Calendar selectedCalender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_type);
        ButterKnife.bind(this);
        registerBaseActivityReceiver();
        getWidgetConfig();
        setRecyclerView();
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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Ticket ticket = ticketArrayList.get(position);
                showDatePicker(ticket.getTicketId(), ticket.getTicketName(), ticket.getDrawCode(), ticket.getTicketDrawDate());


            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void showDatePicker(final String ticketId, final String ticketName, final String drawCode, String ticketDrawDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepickerCustom, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String dateofPurchase = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = formatter.parse(dateofPurchase);
                    formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = formatter.format(date);
                    selectedCalender.set(year, monthOfYear, dayOfMonth);
                    pYear = year;
                    pMonth = monthOfYear;
                    pDate = dayOfMonth;

                    try {
                        Intent intent = new Intent(OverAllBlock.this, MyResultHistoryDetails.class);
                        intent.putExtra("ticketTypeId", ticketId);
                        intent.putExtra("ticketTypeName", ticketName);
                        intent.putExtra("ticketCode", drawCode);
                        intent.putExtra("ticketDate", currentDate);
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {

                    }
                } catch (ParseException exception) {
                    Log.e("e", exception.toString());
                }


            }
        }, pYear, pMonth, pDate);
        Calendar cal = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        cal.set(1900, 01, 01);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.show();
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_select_ticket_type));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ticketArrayList = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        pYear = now.get(Calendar.YEAR);
        pMonth = now.get(Calendar.MONTH);
        pDate = now.get(Calendar.DAY_OF_MONTH);
        selectedCalender = now;
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
            group.setBackgroundColor(ContextCompat.getColor(OverAllBlock.this, R.color.colorPrimary));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class getTicketTypenAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(OverAllBlock.this, getString(R.string.loading_message));
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


    @Override
    @SuppressLint("MissingPermission")
    public void callbackReturn(String response) {
        dialog.dismiss();
        try {
            ticketArrayList.clear();
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            String message = jsonObject.getString(apiRequest.desc);
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


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }


}
