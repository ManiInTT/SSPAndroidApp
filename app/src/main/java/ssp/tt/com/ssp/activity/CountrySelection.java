package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.CountryAdapter;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

/**
 * @author
 */
public class CountrySelection extends BaseActivity {
    EditText countryTxt;
    ImageView deleteBtn;
    RelativeLayout searchLayout;
    ListView countryList;
    CountryAdapter countryAdapter;
    ArrayList<HashMap<String, String>> countryHashMap;
    boolean isInternetPresent = false;
    StringBuffer serverResponse;
    ProgressDialog dialog;
    String search;
    String countryId = "";
    String stateId = "";
    Util util;
    String addressFlag = "";
    ServiceConnector serviceConnector;
    WebServiceUtil userProfileRequest;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutConfiguration();
        widgetConfiguration();
        addFilter();
        clickEvents();
        Intent i = getIntent();
        addressFlag = i.getStringExtra("addressFlag");
        serviceRequest();
    }
    private void serviceRequest() {
        isInternetPresent = mConnectionDetector.isNetworkAvailable();
        if (isInternetPresent) {

            if (addressFlag.equals("1")) {
                new CountrySelection.getCountry().execute();

            } else if (addressFlag.equals("2")){
                Intent i = getIntent();
                countryId = i.getStringExtra("countryId");
                new CountrySelection.getState().execute();

            }else if (addressFlag.equals("3")){
                Intent i = getIntent();
                stateId = i.getStringExtra("stateId");
                new CountrySelection.getCity().execute();
            }

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
            group.setBackgroundColor(ContextCompat.getColor(CountrySelection.this, R.color.colorPrimary));
            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }

    }

    private void clickEvents() {

        deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                countryTxt.setText("");
                deleteBtn.setImageResource(R.drawable.search);

            }
        });
        countryList.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // hide KB
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(countryTxt.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private class getCountry extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(CountrySelection.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {

                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.getCountryDetails(userId,"", getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /***
     * @author
     * @description Configures layout
     * @method layoutConfiguration
     * @date 19/02/2015
     ***/
    private void layoutConfiguration() {

        overridePendingTransition(0, 0);
        setContentView(R.layout.country_selection);
    }

    private void widgetConfiguration() {

        util = new Util();
        countryHashMap = new  ArrayList<HashMap<String, String>>();
        serviceConnector = new ServiceConnector();
        userProfileRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
        countryTxt = (EditText) findViewById(R.id.et_country_search);
        deleteBtn = (ImageView) findViewById(R.id.country_search_delete);
        countryList = (ListView) findViewById(R.id.countries_listview);
        countryList.setFocusableInTouchMode(true);
        countryList.requestFocus();
        searchLayout = (RelativeLayout) findViewById(R.id.search_layout);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

    }


    private void addFilter() {

        deleteBtn.setClickable(false);
        countryTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                // Abstract Method of TextWatcher Interface.
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Abstract Method of TextWatcher Interface.
                deleteBtn.setImageResource(R.drawable.close);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (countryAdapter != null) {
                    countryAdapter.getFilter().filter(s.toString());
                    deleteBtn.setClickable(true);

                    if (count == 0 && start == 0) {
                        deleteBtn.setImageResource(R.drawable.search);
                        deleteBtn.setClickable(false);
                    }

                    if (count < before) {

                    } else {
                        countryAdapter.notifyDataSetChanged();
                        countryList.setAdapter(countryAdapter);
                    }

                }
            }
        });

    }

    /**
     * mapComparator
     */
    public static Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
        @Override
        public int compare(Map<String, String> m1, Map<String, String> m2) {
            return m1.get("country_name").compareTo(m2.get("country_name"));
        }
    };

    @Override
    public void callbackReturn(String response) {
        dialog.dismiss();
        try {
            JSONObject mJSONObject = new JSONObject(response);
            int mCode = mJSONObject.getInt(userProfileRequest.statusCode);
            String mMessage = mJSONObject.getString(userProfileRequest.desc);
            if (addressFlag.equals("1")) {
                if (mCode == userProfileRequest.codeSuccess) {
                    String data = mJSONObject.getString(userProfileRequest.data);
                    JSONArray countryArray = new JSONArray(data);


                    for (int qIndex = 0; qIndex < countryArray.length(); qIndex++) {
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        map1.put("id", new JSONObject(countryArray.get(qIndex).toString()).getString("country_id"));
                        map1.put("name", new JSONObject(countryArray.get(qIndex).toString()).getString("country_name"));
                        countryHashMap.add(map1);
                    }
                    if (countryHashMap != null) {
                        // Collections.sort(countryHashMap, mapComparator);
                        countryAdapter = new CountryAdapter(CountrySelection.this,
                                countryHashMap);
                        countryAdapter.notifyDataSetChanged();
                        countryList.setAdapter(countryAdapter);
                    }
                }

            }  else if (addressFlag.equals("2")) {
                if (mCode == userProfileRequest.codeSuccess) {
                    String data = mJSONObject.getString(userProfileRequest.data);
                    JSONArray countryArray = new JSONArray(data);


                    for (int qIndex = 0; qIndex < countryArray.length(); qIndex++) {
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        map1.put("id", new JSONObject(countryArray.get(qIndex).toString()).getString("state_id"));
                        map1.put("name", new JSONObject(countryArray.get(qIndex).toString()).getString("state_name"));
                        countryHashMap.add(map1);
                    }
                    if (countryHashMap != null) {
                        // Collections.sort(countryHashMap, mapComparator);
                        countryAdapter = new CountryAdapter(CountrySelection.this,
                                countryHashMap);
                        countryAdapter.notifyDataSetChanged();
                        countryList.setAdapter(countryAdapter);
                    }
                }

            }else if (addressFlag.equals("3")) {
                if (mCode == userProfileRequest.codeSuccess) {
                    String data = mJSONObject.getString(userProfileRequest.data);
                    JSONArray countryArray = new JSONArray(data);


                    for (int qIndex = 0; qIndex < countryArray.length(); qIndex++) {
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        map1.put("id", new JSONObject(countryArray.get(qIndex).toString()).getString("city_id"));
                        map1.put("name", new JSONObject(countryArray.get(qIndex).toString()).getString("city_name"));
                        countryHashMap.add(map1);
                    }
                    if (countryHashMap != null) {
                        countryAdapter = new CountryAdapter(CountrySelection.this,
                                countryHashMap);
                        countryAdapter.notifyDataSetChanged();
                        countryList.setAdapter(countryAdapter);
                    }
                }

            }
        } catch (JSONException mException) {
            Log.i("JSONException", mException.getMessage());

        }
    }
    @SuppressLint("StaticFieldLeak")
    private class getCity extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(CountrySelection.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {

                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.getCityDetails(stateId,"", getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class getState extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(CountrySelection.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {

                String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                serviceConnector.getStateDetails(countryId,"", getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
