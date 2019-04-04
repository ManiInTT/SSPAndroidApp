package ssp.tt.com.ssp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import ssp.tt.com.ssp.support.ConnectionDetector;
import ssp.tt.com.ssp.webservice.ServiceConnector;

public abstract class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ServiceConnector.ServiceCallBack {
    public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "ssp.tt.com.ssp.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
    boolean isInternetPresent = false;
    public static final IntentFilter INTENT_FILTER = createIntentFilter();
    boolean isPermissionPhoneState = false;
    ConnectionDetector mConnectionDetector = new ConnectionDetector(this);
    private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
    /************************************************************************************
     * Class      : Base Activity
     * Use        : closeAllActivities
     * Created on :10/04/2018
     * Updated on : 10/04/2018
     **************************************************************************************/
    protected void closeAllActivities() {
        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
    }
    protected void unRegisterBaseActivityReceiver() {
        unregisterReceiver(baseActivityReceiver);
    }

    public class BaseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
                finish();
            }
        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    protected void registerBaseActivityReceiver() {
        registerReceiver(baseActivityReceiver, INTENT_FILTER);
    }
    private static IntentFilter createIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
        return filter;
    }
}