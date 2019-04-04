package ssp.tt.com.ssp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

/*******************************************************************************
 * Class Name   :   Splash
 * Description  :   Show Splash Screen
 * Created on   :   2018-03-23
 * Updated on   :   2018-03-23
 *******************************************************************************/
public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 900;
    WebServiceUtil loginRequest = new WebServiceUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USER_ID, "11");
        // PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.IMEI_NUMBER, "NmF3cGpDU09wdEhvU0JQdytEZkowQT09");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //  String userId = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USER_ID, "");
//                if (userId.equals("")) {
                Intent intent = new Intent(Splash.this, Login.class);
                intent.putExtra("pageRequestFlag", loginRequest.LOGIN);
                startActivityForResult(intent, 1);
                finish();
//                } else {
//                    Intent intent = new Intent(Splash.this, Dashboard.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        }, SPLASH_TIME_OUT);
    }
}