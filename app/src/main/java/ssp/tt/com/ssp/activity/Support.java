package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.BankAdapter;
import ssp.tt.com.ssp.adapter.SupportListAdapter;
import ssp.tt.com.ssp.interfaces.SupportRequestListener;
import ssp.tt.com.ssp.model.Bank;
import ssp.tt.com.ssp.model.SupportRequest;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class Support extends BaseActivity implements SupportRequestListener {


    @BindView(R.id.et_subject)
    EditText etSubject;

    @BindView(R.id.et_message)
    EditText etMessage;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;


    private int API_TYPE = 1;
    private int API_REQUEST_LIST = 1;
    private int API_POST_REQUEST = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_request);
        ButterKnife.bind(this);
        setToolBar();
        registerBaseActivityReceiver();
        new GetSupportDetails().execute();
    }

    @OnClick(R.id.btn_update)
    public void setBtnUpdate(View view) {
        if (etSubject.getText().toString().trim().length() == 0) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_subject), 0);
        } else if (etMessage.getText().toString().trim().length() == 0) {
            Util.warningAlertDialog(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_enter_message), 0);
        } else {
            isInternetPresent = mConnectionDetector.isNetworkAvailable();
            if (isInternetPresent) {
                new SaveSupportDetails().execute();
            } else {
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), "No Network Connection !", 0);
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    public void setBtnCancel(View view) {
        finish();
    }


    @OnClick(R.id.tv_latest_support)
    public void setLatestSupport(View view) {
        Intent intent = new Intent(this, ViewSupportList.class);
        startActivity(intent);

    }


    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_support));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        serviceConnector = new ServiceConnector();
        apiRequest = new WebServiceUtil();
        serviceConnector.registerCallback(this);
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


    @SuppressLint("WrongConstant")
    @Override
    public void callbackReturn(String response) {
        if (API_TYPE == API_REQUEST_LIST) {
            try {
                JSONObject mJSONObject = new JSONObject(response);
                Log.i("response", response);
                String status = mJSONObject.getString("status");
                if (status.equals("200")) {
                    dialog.dismiss();
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONArray jsonArray = mJSONObject.getJSONArray(userLoginRequest.data);
                    List<SupportRequest> supportRequestList = new ArrayList<>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        String ct_id = jsonObject.getString(userLoginRequest.ct_id);
                        String ct_serial_no = jsonObject.getString(userLoginRequest.ct_serial_no);
                        String ct_subject = jsonObject.getString(userLoginRequest.ct_subject);
                        String ct_date = jsonObject.getString(userLoginRequest.ct_date);
                        String ct_status = jsonObject.getString(userLoginRequest.ct_status);
                        supportRequestList.add(new SupportRequest(ct_id, ct_serial_no, ct_subject, ct_date, ct_status));
                    }

                    SupportListAdapter supportListAdapter = new SupportListAdapter(supportRequestList);
                    recyclerView.setHasFixedSize(true);
                    supportListAdapter.setSupportRequestListener(this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
                    recyclerView.addItemDecoration(itemDecor);
                    recyclerView.setAdapter(supportListAdapter);

                } else {
                    dialog.dismiss();
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
        } else if (API_TYPE == API_POST_REQUEST) {
            try {
                JSONObject mJSONObject = new JSONObject(response);
                Log.i("response", response);
                String status = mJSONObject.getString("status");
                etMessage.setText("");
                etSubject.setText("");
                if (status.equals("200")) {
                    dialog.dismiss();

                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);

                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog_request);
                    dialog.setCancelable(false);
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    TextView tvMessage = dialog.findViewById(R.id.tv_message);
                    TextView tvTicketInfo = dialog.findViewById(R.id.tv_ticket_info);
                    tvMessage.setText(description);
                    tvTicketInfo.setVisibility(View.GONE);
                    tvTitle.setText(title);
                    TextView noBtn = dialog.findViewById(R.id.no_btn);
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            new GetSupportDetails().execute();

                        }
                    });
                    dialog.show();

                    try{
                        JSONObject jsonObject = mJSONObject.getJSONObject("data");
                        String ctSerialNo = jsonObject.getString("ct_serial_no");
                        tvTicketInfo.setText("Ticket No : "+ctSerialNo);
                        tvTicketInfo.setVisibility(View.VISIBLE);
                    }catch (Exception e){

                    }


                } else {
                    dialog.dismiss();
                    WebServiceUtil userLoginRequest = new WebServiceUtil();
                    JSONObject jsonObjectDesc = mJSONObject.getJSONObject(userLoginRequest.desc);
                    String title = jsonObjectDesc.getString(userLoginRequest.title);
                    String description = jsonObjectDesc.getString(userLoginRequest.description);

                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.alert_dialog_request);
                    dialog.setCancelable(false);
                    TextView tvTitle = dialog.findViewById(R.id.tv_title);
                    TextView tvMessage = dialog.findViewById(R.id.tv_message);
                    TextView tvTicketInfo = dialog.findViewById(R.id.tv_ticket_info);
                    tvMessage.setText(description);
                    tvTicketInfo.setVisibility(View.GONE);
                    tvTitle.setText(title);
                    TextView noBtn = dialog.findViewById(R.id.no_btn);
                    noBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            new GetSupportDetails().execute();
                        }
                    });
                    dialog.show();

                    try{
                        JSONObject jsonObject = mJSONObject.getJSONObject("data");
                        String ctSerialNo = jsonObject.getString("ct_serial_no");
                        tvTicketInfo.setText("Ticket No : "+ctSerialNo);
                        tvTicketInfo.setVisibility(View.VISIBLE);
                    }catch (Exception e){

                    }
                }
            } catch (JSONException mException) {
                dialog.dismiss();
                Log.i("JSONException", mException.toString());
                Util.warningAlertDialog(this, getResources().getString(R.string.warning), mException.toString(), 0);


            }
        }
    }

    @Override
    public void clickSupport(String ctId) {
        Intent intent = new Intent(this, ViewSupportListDetails.class);
        intent.putExtra("ctId", ctId);
        startActivity(intent);
    }


    @SuppressLint("StaticFieldLeak")
    private class SaveSupportDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Support.this, getString(R.string.loading_message));
            API_TYPE = API_POST_REQUEST;
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String subject = etSubject.getText().toString().trim();
                String message = etMessage.getText().toString().trim();
                serviceConnector.saveSupport(getApplicationContext(), UserId, imeiNumber, subject, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSupportDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(Support.this, getString(R.string.loading_message));
            API_TYPE = API_REQUEST_LIST;
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                String opt = "5";
                serviceConnector.getRequestSupport(getApplicationContext(), UserId, imeiNumber, opt);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
