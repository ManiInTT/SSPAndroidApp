package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.DashboardAdapter;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

import static ssp.tt.com.ssp.activity.WithDraw.BALANCE_AMOUNT;

/*******************************************************************************
 * Class Name   :   Dashboard
 * Description  :   Dashboard Screen
 * Created at   :   2018-03-28
 * Updated at   :   2018-03-28
 *******************************************************************************/

public class Dashboard extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GridView androidGridView;
    String[] DashboardMenuTexts = {
            "My eWallet", "Result", "Buy ticket", "Gift the ticket", "Withdrawal", "My blocks", "My Profile", "Support"
    };
    int[] DashboardMenuIcons = {
            R.string.icon_wallet,
            R.string.icon_result,
            R.string.icon_ticket,
            R.string.icon_gift_ticket,
            R.string.icon_withdrawal,
            R.string.icon_blocks,
            R.string.icon_blocks, R.string.icon_support
    };
    TextView login_email;

    ServiceConnector serviceConnector;
    WebServiceUtil apiRequest;
    ProgressDialog dialog;

    public int API_TYPE = 1;
    public final int API_WALLET_BALANCE = 1;
    public final int API_PURCHASE_HISTORY = 2;
    public final int API_CHECK_REGISTER_WITH_BANK = 3;
    int cartItemCount = 0;
    float totalAmount = 0;
    TextView textCartItemCount;
    String yourAccountBalance = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWidgetConfig();
        registerBaseActivityReceiver();
        gridItemClick();
    }

    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {
            new WalletBalance().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceRequest();
    }

    /************************************************************************************
     * Class      : Dashboard
     * Use        : Method call widget configure
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    private void getWidgetConfig() {
        serviceConnector = new ServiceConnector();
        serviceConnector.registerCallback(this);
        apiRequest = new WebServiceUtil();
        DashboardAdapter adapterViewAndroid = new DashboardAdapter(Dashboard.this, DashboardMenuTexts, DashboardMenuIcons);
        androidGridView = (GridView) findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_dashboard));
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        login_email = (TextView) header.findViewById(R.id.login_email);
        TextView tvUsrName = (TextView) header.findViewById(R.id.user_name);

        navigationView.setNavigationItemSelectedListener(this);
        String userEmail = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_EMAIL, "");
        String userName = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_NAME, "");
        tvUsrName.setText(userName);
        login_email.setText(userEmail);

    }

    /************************************************************************************
     * Class      : Dashboard
     * Use        : Method call grid item click event
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    public void gridItemClick() {
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int pos, long id) {
                if (pos == 0) {
                    startActivity(new Intent(Dashboard.this, Ewallet.class));
                } else if (pos == 1) {
                    startActivity(new Intent(Dashboard.this, ResultActivity.class));
                } else if (pos == 2) {
                    startActivity(new Intent(Dashboard.this, TicketType.class));
                } else if (pos == 4) {
                    new CheckRegisterWithBank().execute();
                } else if (pos == 6) {
                    Intent intent = new Intent(Dashboard.this, Profile.class);
                    intent.putExtra("pageRequestFlag", "UPDATE");
                    startActivity(intent);
                } else if (pos == 7) {
                    Intent intent = new Intent(Dashboard.this, Support.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_wallet) {
            startActivity(new Intent(Dashboard.this, Ewallet.class));
        } else if (id == R.id.nav_result) {
            startActivity(new Intent(Dashboard.this, ResultActivity.class));
        } else if (id == R.id.buy_ticket) {
            startActivity(new Intent(Dashboard.this, TicketType.class));
        } else if (id == R.id.nav_with_drawal) {
            new CheckRegisterWithBank().execute();
        } else if (id == R.id.profile) {
            Intent intent = new Intent(Dashboard.this, Profile.class);
            intent.putExtra("pageRequestFlag", "UPDATE");
            startActivity(intent);
        } else if (id == R.id.logout) {
            Intent intent = new Intent(Dashboard.this, Login.class);
            intent.putExtra("pageRequestFlag", "LOGIN");
            startActivity(intent);
            finish();
        } else if (id == R.id.change_password) {
            Intent intent = new Intent(this, PINRequest.class);
            intent.putExtra("pageRequestFlag", "CHANGE_PASSWORD");
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBaseActivityReceiver();
    }

    @Override
    public void callbackReturn(String response) {
        switch (API_TYPE) {
            case API_WALLET_BALANCE:
                getWalletBalance(response);
                break;
            case API_PURCHASE_HISTORY:
                getPurchaseHistory(response);
                break;
            case API_CHECK_REGISTER_WITH_BANK:
                getCheckRegisterResponse(response);
                break;
        }

    }

    private void getCheckRegisterResponse(String response) {
        try {
            dialog.dismiss();
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(apiRequest.statusCode);
            if (mCode == apiRequest.codeSuccess) {
                Intent intent = new Intent(Dashboard.this, WithDraw.class);
                intent.putExtra(BALANCE_AMOUNT, yourAccountBalance);
                startActivity(intent);
            } else {
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                String title = jsonObjectDesc.getString(userLoginRequest.title);
                String description = jsonObjectDesc.getString(userLoginRequest.description);
                Util.warningAlertDialog(this, title, description, 0);
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alert_dialog_warning);
                dialog.setCancelable(false);
                TextView tvTitle = dialog.findViewById(R.id.tv_title);
                TextView tvMessage = dialog.findViewById(R.id.tv_message);
                tvMessage.setText(description);
                tvTitle.setText(title);
                TextView noBtn = dialog.findViewById(R.id.no_btn);
                noBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(Dashboard.this, UpdateAccount.class);
                        intent.putExtra(UpdateAccount.INTENT_TITLE, "WALLET");
                        intent.putExtra(UpdateAccount.INTENT_AMOUNT, yourAccountBalance);
                        startActivity(intent);

                    }
                });
                dialog.show();
            }
        } catch (JSONException mException) {
            Log.i("JSONException ffff", mException.getMessage());
        }
    }

    private void getWalletBalance(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                String data = jsonObject.getString(apiRequest.data);
                JSONObject jsonObjectData = new JSONObject(data);
                String total = jsonObjectData.getString(apiRequest.USER_TOTAL_AMOUNT);
                total = total.replaceAll(",", "");
                yourAccountBalance = total.replaceAll(",", "");
                PreferenceConnector.writeFloat(this, PreferenceConnector.USER_TOTAL_AMOUNT, Float.valueOf(total));
                new PurchaseSummary().execute();
            } else {
                dialog.dismiss();
            }
        } catch (Exception e) {
            dialog.dismiss();
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
                totalAmount = 0;
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    cartItemCount = cartItemCount + 1;
                    jsonObject = jsonArray.getJSONObject(tIndex);
                    float ltRate = Float.valueOf(jsonObject.getString(apiRequest.ltRate));
                    totalAmount = totalAmount + ltRate;
                }
            }
            setupBadge(cartItemCount);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
            case R.id.action_cart:
                Intent intent = new Intent(Dashboard.this, MyCart.class);
                intent.putExtra("cartItemCount", cartItemCount);
                intent.putExtra("cartItemTotal", String.valueOf(totalAmount));
                startActivity(intent);
                return true;
        }
        return false;
    }


    @SuppressLint("StaticFieldLeak")
    private class WalletBalance extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            API_TYPE = API_WALLET_BALANCE;
            dialog = ProgressUtil.showDialog(Dashboard.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.walletBalance(getApplicationContext(), userId, imeiNumber);
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
    private class CheckRegisterWithBank extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Dashboard.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            API_TYPE = API_CHECK_REGISTER_WITH_BANK;
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getCheckRegisterWithBank(getApplicationContext(), UserId, imeiNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
