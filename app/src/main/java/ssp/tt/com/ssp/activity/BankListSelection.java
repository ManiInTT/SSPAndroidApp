package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.CountryAdapter;
import ssp.tt.com.ssp.model.Bank;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

/**
 * @author
 */
public class BankListSelection extends BaseActivity {

    EditText countryTxt;
    ImageView deleteBtn;
    RelativeLayout searchLayout;
    ListView countryList;
    CountryAdapter countryAdapter;
    ArrayList<HashMap<String, String>> countryHashMap;
    Util util;
    LinearLayout linearLayout;
    ArrayList<Bank> banks;

    public static final String ARG_BANK = "bankList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutConfiguration();
        widgetConfiguration();
        addFilter();
        clickEvents();
        Intent i = getIntent();
        banks = i.getParcelableArrayListExtra(ARG_BANK);
        setRecyclerView();
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


    private void layoutConfiguration() {
        overridePendingTransition(0, 0);
        setContentView(R.layout.country_selection);
    }

    private void widgetConfiguration() {
        util = new Util();
        countryHashMap = new ArrayList<HashMap<String, String>>();
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




    public void setRecyclerView() {
        for (int qIndex = 0; qIndex < banks.size(); qIndex++) {
            HashMap<String, String> map1 = new HashMap<String, String>();
            map1.put("id", banks.get(qIndex).getBankId());
            map1.put("name", banks.get(qIndex).getBankName());
            countryHashMap.add(map1);
        }
        if (countryHashMap != null) {
            // Collections.sort(countryHashMap, mapComparator);
            countryAdapter = new CountryAdapter(BankListSelection.this,
                    countryHashMap);
            countryAdapter.notifyDataSetChanged();
            countryList.setAdapter(countryAdapter);
        }
    }

    @Override
    public void callbackReturn(String string) {

    }
}
