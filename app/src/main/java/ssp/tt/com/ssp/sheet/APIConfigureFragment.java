package ssp.tt.com.ssp.sheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.support.PreferenceConnector;

public class APIConfigureFragment extends BottomSheetDialogFragment {

    @BindView(R.id.rbBeta)
    RadioButton rbBeta;

    @BindView(R.id.rbDevelopment)
    RadioButton rbDevelopment;

    @BindView(R.id.yes_btn)
    TextView yes_btn;

    @BindView(R.id.no_btn)
    TextView no_btn;


    private BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, R.style.BottomSheetTheme);
        View contentView = View.inflate(getContext(), R.layout.sheet_configuration, null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        int urlId = PreferenceConnector.readInteger(getContext(), PreferenceConnector.APP_URL, 1);
        if (urlId == 1) {
            rbBeta.setChecked(true);
        } else {
            rbDevelopment.setChecked(true);
        }
    }

    @OnClick(R.id.yes_btn)
    public void setYesBtn() {
        PreferenceConnector.writeInteger(getContext(), PreferenceConnector.APP_URL, rbBeta.isChecked() ? 1 : 2);
        dismiss();
    }

    @OnClick(R.id.no_btn)
    public void setNoButton() {
        dismiss();
    }



}

