package ssp.tt.com.ssp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import ssp.tt.com.ssp.ViewInterface;
import ssp.tt.com.ssp.support.ConnectionDetector;
import ssp.tt.com.ssp.webservice.ServiceConnector;


public abstract class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, ServiceConnector.ServiceCallBack, ViewInterface {
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


    int requestCode;

    int REQ_WRITE_EXTERNAL_STORAGE = 2323;

    CropImageView.CropShape cropShape = CropImageView.CropShape.OVAL;
    int ratioX;
    int ratioY;

    void onAllowed(int requestCode) {
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE)
            pickImage(this.requestCode, cropShape, ratioX, ratioY);
    }

    public void pickImage(int requestCode, CropImageView.CropShape cropShape, int ratioX,
                          int ratioY) {
        this.requestCode = requestCode;
        this.cropShape = cropShape;
        this.ratioX = ratioX;
        this.ratioY = ratioY;
        if (isAllowed(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, REQ_WRITE_EXTERNAL_STORAGE)) {
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    public void onImageChosen(int requestCode, Uri uri) {
        for (Fragment fragment : getFragments()) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onImageChosen(requestCode, uri);
            }
        }
    }

    public List<Fragment> getFragments() {
        return getSupportFragmentManager().getFragments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            CropImage.activity(imageUri)
                    .setCropShape(cropShape)
                    .setFixAspectRatio(true)
                    .setAspectRatio(ratioX, ratioY)
                    .setRequestedSize(0, 512)
                    /*.setBackgroundColor(Color.BLACK)*/
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setOutputCompressQuality(40)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                onImageChosen(this.requestCode, result.getUri());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public static boolean isAllowed(AppCompatActivity activity, String permission,
                                    int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            onAllowed(requestCode);
      /*  else
            onCancelled(requestCode);*/
    }


}