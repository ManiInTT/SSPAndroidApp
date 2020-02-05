package ssp.tt.com.ssp;

import android.net.Uri;

import com.theartofdev.edmodo.cropper.CropImageView;


public interface ViewInterface {

    void pickImage(int requestCode, CropImageView.CropShape cropShape, int ratioX, int ratioY);

    void onImageChosen(int requestCode, Uri uri);

}
