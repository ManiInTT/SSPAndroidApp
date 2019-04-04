package ssp.tt.com.ssp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import ssp.tt.com.ssp.R;


public class ProgressUtil {
    public static ProgressDialog showDialog(Activity mActivity, String mMessage) {
        ProgressDialog mProgressDialog = ProgressDialog.show(mActivity, null, null, true);
        mProgressDialog.setContentView(R.layout.dialog_progress);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.show();
        return mProgressDialog;
    }
}