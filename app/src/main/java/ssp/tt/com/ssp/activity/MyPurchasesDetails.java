package ssp.tt.com.ssp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.utils.Util;
import ssp.tt.com.ssp.webservice.ServiceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class MyPurchasesDetails extends BaseActivity {
    Util util;
    ServiceConnector serviceConnector;
    ProgressDialog dialog;
    LinearLayout linearLayout;
    WebServiceUtil apiRequest;
    String transactionDetails;
    @BindView(R.id.tv_trans_id)
    TextView tv_trans_id;
    @BindView(R.id.tv_trans_date)
    TextView tv_trans_date;
    @BindView(R.id.tv_trans_type)
    TextView tv_trans_type;
    @BindView(R.id.tv_trans_amount)
    TextView tv_trans_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases_details);
        getWidgetConfig();
        registerBaseActivityReceiver();
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        transactionDetails = extras.getString("myPurchasesDetails");
        try {
            JSONObject transactionObject = new JSONObject(transactionDetails.toString());
            Log.i("Info", transactionObject.toString());
            tv_trans_id.setText(transactionObject.getString("pur_id"));
            tv_trans_date.setText(transactionObject.getString("pur_date"));
            tv_trans_type.setText(transactionObject.getString("no_of_units"));
            tv_trans_amount.setText(transactionObject.getString("trans_amount"));
        } catch (Exception e) {
            Toast.makeText(MyPurchasesDetails.this, "" + e.toString(),
                    Toast.LENGTH_SHORT).show();

        }

    }

    /************************************************************************************
     * Class      : TransactionDetails
     * Use        : Method call widget configure
     * Created on : 2018-04-20
     * Updated on : 2018-20-20
     **************************************************************************************/
    private void getWidgetConfig() {
        util = new Util();
        serviceConnector = new ServiceConnector();
        apiRequest = new WebServiceUtil();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_my_purchases_details));
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
    public void callbackReturn(String string) {

    }
}
