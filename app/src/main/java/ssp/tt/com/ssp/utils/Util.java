package ssp.tt.com.ssp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import ssp.tt.com.ssp.R;
import ssp.tt.com.ssp.activity.Deposit;
import ssp.tt.com.ssp.activity.Login;
import ssp.tt.com.ssp.support.PreferenceConnector;
import ssp.tt.com.ssp.webservice.WebServiceUtil;

public class Util {

    boolean isOpen = false;
    static WebServiceUtil apiRequest;

    public void keyboardDisable(Activity mActivity, View v) {
        InputMethodManager iManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (iManager.isAcceptingText()) {
            iManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : convert dp to px
     * Created By : 01/25/2018
     * Updated on : 01/25/2018
     **************************************************************************************/
    public static float dpToPx(Context ctx, float valueInDp) {
        try {
            DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : get Keyboard State
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    public boolean getKeyboardState(final Activity mActivity, final View mView) {
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = mView.getRootView().getHeight() - mView.getHeight();
                if (heightDiff > dpToPx(mActivity, 200)) {
                    //Keyboard Open
                    isOpen = true;
                } else {
                    //Keyboard Close
                    isOpen = false;
                }
            }
        });
        return isOpen;
    }


    /***********************************************************************************
     * Class      : Util
     * Use        : validate email
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    public static boolean validateRegister(Activity mActivity, TextInputLayout inputLayoutEmail, EditText inputEmail, TextInputLayout inputLayoutMobile, EditText inputMobile) {

        if (inputMobile.getText().toString().trim().isEmpty() || inputMobile.getText().toString().trim().length() < 10 || !Patterns.PHONE.matcher(inputMobile.getText().toString().trim()).matches()) {
            inputLayoutMobile.setError(mActivity.getString(R.string.err_msg_mobile_number));
            requestFocus(mActivity, inputMobile);
        }
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(mActivity.getString(R.string.err_msg_email));
            requestFocus(mActivity, inputEmail);
        }

        return true;
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : Validate Mobile Number
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/

    public static boolean validateMobileNumber(Activity mActivity, TextInputLayout inputLayoutMobile, EditText inputMobile) {
        if (inputMobile.getText().toString().trim().isEmpty() || inputMobile.getText().toString().trim().length() < 10 || !Patterns.PHONE.matcher(inputMobile.getText().toString().trim()).matches()) {
            inputLayoutMobile.setError(mActivity.getString(R.string.err_msg_mobile_number));
            requestFocus(mActivity, inputMobile);
            return false;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
        }

        return true;
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : validate email
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    public static boolean validateEmail(Activity mActivity, TextInputLayout inputLayoutEmail, EditText inputEmail) {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(mActivity.getString(R.string.err_msg_email));
            requestFocus(mActivity, inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    public static boolean validateResetPassword(Activity mActivity, TextInputLayout inputLayoutPassword, EditText inputPassword, TextInputLayout inputLayoutConfirmPassword, EditText inputConfirmPassword) {
        String password = inputPassword.getText().toString().trim();
        if (password.isEmpty() || password.length() < 5) {
            inputLayoutPassword.setError(mActivity.getString(R.string.err_msg_password));
            requestFocus(mActivity, inputPassword);
        }
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        if (confirmPassword.isEmpty() || confirmPassword.length() < 5) {
            inputLayoutConfirmPassword.setError(mActivity.getString(R.string.err_msg_valid_confirm_password));
            requestFocus(mActivity, inputConfirmPassword);
        }
        return true;
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : Validate Password
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    public static boolean validatePassword(Activity mActivity, TextInputLayout inputLayoutPassword, EditText inputPassword) {
        String password = inputPassword.getText().toString().trim();

        if (password.isEmpty() || password.length() < 5) {
            inputLayoutPassword.setError(mActivity.getString(R.string.err_msg_password));
            requestFocus(mActivity, inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : Edit Focus
     * Created on : 2018-03-23
     * Updated on : 2018-03-23
     **************************************************************************************/
    public static void requestFocus(Activity mActivity, View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : validate password
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    public static boolean validateConfirmPassword(Activity mActivity, TextInputLayout inputLayoutConfirmPassword, EditText inputPassword, EditText inputConfirmPassword) {
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();


        if (confirmPassword.length() == 0 || confirmPassword.length() < 5 || !password.equals(confirmPassword)) {
            inputLayoutConfirmPassword.setError(mActivity.getString(R.string.err_msg_confirm_password));
            requestFocus(mActivity, inputPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }

    /***********************************************************************************
     * Class      : Util
     * Use        : Validate Pin
     * Created on : 2018-03-28
     * Updated on : 2018-03-28
     **************************************************************************************/
    public static boolean validatePin(Activity mActivity, TextInputLayout inputLayoutPin, EditText inputPin) {
        String pin = inputPin.getText().toString().trim();

        if (pin.isEmpty() || pin.length() != 6) {
            inputLayoutPin.setError(mActivity.getString(R.string.err_msg_pin));
            requestFocus(mActivity, inputPin);
            return false;
        } else {
            inputLayoutPin.setErrorEnabled(false);
        }
        return true;
    }

    public static String convertMd5(String imei) {
        String convertedImei = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(imei.getBytes(), 0, imei.length());
            imei = new BigInteger(1, mdEnc.digest()).toString(16);
            while (imei.length() < 32) {
                imei = "0" + imei;
            }
            convertedImei = imei;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return convertedImei;
    }

    public static boolean validateString(Activity mActivity, TextInputLayout inputLayoutText, EditText text) {
        String alphaText = text.getText().toString().trim();

        if (alphaText.isEmpty() || alphaText.length() < 5) {
            inputLayoutText.setError(mActivity.getString(R.string.err_msg_password));
            requestFocus(mActivity, inputLayoutText);
            return false;
        } else {
            inputLayoutText.setErrorEnabled(false);
        }
        return true;
    }


    public static String convertLocalDate(String sourceDateTime) {
        String convertedTime = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsed = sourceFormat.parse(sourceDateTime); // => Date is in UTC now
            SimpleDateFormat destFormat = new SimpleDateFormat("dd/MM/yyyy");
            convertedTime = destFormat.format(parsed);
            Log.i("After Date", convertedTime);
        } catch (Exception e) {
            Log.e("convertLocalDate : ", e.toString());
        }
        return convertedTime;
    }

    public static String convertLocalDateTime(String sourceDateTime) {
        String convertedTime = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date parsed = sourceFormat.parse(sourceDateTime); // => Date is in UTC now
            SimpleDateFormat destFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
            convertedTime = destFormat.format(parsed);
            Log.i("After Date", convertedTime);
        } catch (Exception e) {
            Log.e("convertLocalDate : ", e.toString());
        }
        return convertedTime;
    }


    public static String convertLocalMonth(String sourceDateTime) {
        String convertedTime = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsed = sourceFormat.parse(sourceDateTime); // => Date is in UTC now
            SimpleDateFormat destFormat = new SimpleDateFormat("MMMM");
            convertedTime = destFormat.format(parsed);
            Log.i("After Month", convertedTime);
        } catch (Exception e) {
            Log.e("convertLocalMonth : ", e.toString());
        }
        return convertedTime;
    }

    public static String convertLocalMonthYear(String sourceDateTime) {
        String convertedTime = "";
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsed = sourceFormat.parse(sourceDateTime); // => Date is in UTC now
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM yyyy");
            convertedTime = destFormat.format(parsed);
            Log.i("After Month", convertedTime);
        } catch (Exception e) {
            Log.e("convertLocalMonth : ", e.toString());
        }
        return convertedTime;
    }

    public static boolean validateSecret(Activity mActivity, TextInputLayout inputLayoutText, EditText text) {
        String alphaText = text.getText().toString().trim();

        if (alphaText.isEmpty() || alphaText.length() < 2) {
            inputLayoutText.setError(mActivity.getString(R.string.err_msg_password));
            requestFocus(mActivity, inputLayoutText);
            return false;
        } else {
            inputLayoutText.setErrorEnabled(false);
        }
        return true;
    }

    public static boolean emptyString(Activity mActivity, TextInputLayout inputLayoutText, EditText text) {
        String alphaText = text.getText().toString().trim();

        if (alphaText.isEmpty()) {
            inputLayoutText.setError(mActivity.getString(R.string.err_msg_password));
            requestFocus(mActivity, inputLayoutText);
            return false;
        } else {
            inputLayoutText.setErrorEnabled(false);
        }
        return true;
    }

    public static boolean validAlphabets(Activity mActivity, TextInputLayout inputLayoutText, EditText text) {
        String alphaText = text.getText().toString().trim();

        if (!alphaText.toString().matches("[a-zA-Z ]+") || alphaText.isEmpty()) {
            requestFocus(mActivity, inputLayoutText);
            return false;
        } else {
            inputLayoutText.setErrorEnabled(false);
        }
        return true;
    }


    public static boolean ticketValidation(Activity mActivity) {
        boolean error = true;
        apiRequest = new WebServiceUtil();
        String purchaseHistory = PreferenceConnector.readString(mActivity, PreferenceConnector.PURCHASE_HISTORY, "");
        try {
            JSONObject jsonObject = new JSONObject(purchaseHistory);
            int code = jsonObject.getInt(apiRequest.statusCode);
            if (code == apiRequest.codeSuccess) {
                String data = jsonObject.getString(apiRequest.data);
                JSONArray jsonArray = new JSONArray(data);
                HashSet<String> set = new HashSet<>();
                ArrayList<String> result = new ArrayList<>();
                for (int tIndex = 0; tIndex < jsonArray.length(); tIndex++) {
                    jsonObject = jsonArray.getJSONObject(tIndex);
                    String date = jsonObject.getString(apiRequest.tmpls_selected_on);
                    // If String is not in set, add it to the list and the set.
                    if (!set.contains(date)) {
                        result.add(date);
                        set.add(date);
                    }
                }

                for (int i = 0; i < result.size(); i++) {
                    error = true;
                    String uniqueDate = result.get(i);
                    int count = 0;
                    for (int j = 0; j < jsonArray.length(); j++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String date = jsonObject.getString(apiRequest.tmpls_selected_on);
                        if (uniqueDate.equals(date)) {
                            count = count + 1;
                            if (count == 7) {
                                error = false;
                            } else {
                                error = true;
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {

        }

        return error;
    }

    public static void warningAlertDialog(final Activity context, String title, String message, final int code) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog_warning);
        dialog.setCancelable(false);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        tvTitle.setText(title);
        TextView noBtn = dialog.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if (code == 1) {
                    context.finish();
                } else if (code == 2) {
                    Intent i = new Intent(context, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);
                }

            }
        });
        dialog.show();
    }
    public static void successAlertDialog(final Activity context, String title, String message, final int code) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog_success);
        dialog.setCancelable(false);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        tvTitle.setText(title);
        TextView noBtn = dialog.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if (code == 1) {
                    context.finish();
                } else if (code == 2) {
                    Intent i = new Intent(context, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);
                }

            }
        });
        dialog.show();
    }


}