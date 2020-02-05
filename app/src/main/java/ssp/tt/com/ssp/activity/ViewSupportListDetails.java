package ssp.tt.com.ssp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.adapter.SupportListAdapter;
import ssp.tt.com.ssp.adapter.SupportListDetailsAdapter;
import ssp.tt.com.ssp.model.SupportRequest;
import ssp.tt.com.ssp.model.SupportTrack;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.utils.ProgressUtil;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class ViewSupportListDetails extends BaseActivity {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.tv_request_id)
    TextView tvRequestId;

    @BindView(R.id.tv_subject)
    TextView tvSubject;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.tv_message)
    TextView tvMessage;

    @BindView(R.id.ll_no_assigned)
    LinearLayout llNoAssigned;

    @BindView(R.id.ll_view)
    LinearLayout llView;

    @BindView(R.id.trouble_status)
    TextView troubleStatus;

    @BindView(R.id.tv_comments)
    TextView tvComments;

    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    WebServiceUtil apiRequest;

    String ctId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_support_request_details);
        ButterKnife.bind(this);
        setToolBar();
        registerBaseActivityReceiver();
        ctId = getIntent().getStringExtra("ctId");
        llView.setVisibility(View.GONE);
        new GetSupportDetails().execute();
    }


    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_view_track));
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


    @Override
    public void callbackReturn(String response) {
        try {
            Log.i("response", response);
            JSONObject mJSONObject = new JSONObject(response);
            String status = mJSONObject.getString("status");
            if (status.equals("200")) {
                llView.setVisibility(View.VISIBLE);
                dialog.dismiss();
                WebServiceUtil userLoginRequest = new WebServiceUtil();
                JSONObject jsonObject1 = mJSONObject.getJSONObject(userLoginRequest.data);
                String ct_id = jsonObject1.getString(userLoginRequest.ct_id);
                String ct_serial_no = jsonObject1.getString(userLoginRequest.ct_serial_no);
                String ct_subject = jsonObject1.getString(userLoginRequest.ct_subject);
                String ct_date = jsonObject1.getString(userLoginRequest.ct_date);
                String ct_status = jsonObject1.getString(userLoginRequest.ct_status);
                String ct_message = jsonObject1.getString(userLoginRequest.ct_message);

                tvRequestId.setText("Ticket Number : " + ct_serial_no);
                tvSubject.setText(ct_subject);
                tvDate.setText(ct_date);
                tvStatus.setText(ct_status);
                tvMessage.setText(ct_message);

                if (jsonObject1.has(userLoginRequest.trouble_status)) {
                    llNoAssigned.setVisibility(View.GONE);
                    JSONArray jsonArray = jsonObject1.getJSONArray(userLoginRequest.trouble_status);
                    List<SupportTrack> supportTrackArrayList = new ArrayList<>();
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        String agent_name = jsonObject.getString(userLoginRequest.agent_name);
                        String ts_comment_on = jsonObject.getString(userLoginRequest.ts_comment_on);
                        String ts_status = jsonObject.getString(userLoginRequest.ts_status);
                        String ts_comment = jsonObject.getString(userLoginRequest.ts_comment);
                        supportTrackArrayList.add(new SupportTrack(agent_name, ts_comment_on, ts_status, ts_comment));
                    }
                    SupportListDetailsAdapter supportListAdapter = new SupportListDetailsAdapter(supportTrackArrayList);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(supportListAdapter);
                } else {
                    String ts_status = jsonObject1.getString(userLoginRequest.ts_status);
                    tvStatus.setText(ts_status);

                    String ts_comments = jsonObject1.getString(userLoginRequest.ts_comments);
                    tvComments.setText(ts_comments);
                }

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
    }


    @SuppressLint("StaticFieldLeak")
    private class GetSupportDetails extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressUtil.showDialog(ViewSupportListDetails.this, getString(R.string.loading_message));
        }

        protected Void doInBackground(String... params) {
            try {
                String UserId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
                String imeiNumber = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "");
                serviceConnector.getSupportRequestDetails(getApplicationContext(), UserId, imeiNumber, ctId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
