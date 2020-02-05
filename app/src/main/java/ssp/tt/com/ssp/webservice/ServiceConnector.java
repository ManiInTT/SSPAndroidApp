package ssp.tt.com.ssp.webservice;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ssp.tt.com.ssp.support.PreferenceConnector;

import static retrofit2.Retrofit.Builder;

public class ServiceConnector {

    String sessionToken = "";
    private ServiceCallBack serviceCallBack;

    public void registerCallback(ServiceCallBack callbackClass) {
        serviceCallBack = callbackClass;
    }

    Retrofit getRetrofit(Context context) {
        sessionToken = PreferenceConnector.readString(context, PreferenceConnector.SESSION_TOKEN, "");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        builder.connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        builder.build();
        OkHttpClient okHttpClient = builder.build();
        return new Builder().baseUrl(WebServiceUtil.getAppUrl(context)).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
    }

    private void onReadSuccess(Response<ResponseBody> response) {
        try {
            ResponseBody responseBody = response.body();
            serviceCallBack.callbackReturn(responseBody.string());
            Log.i("Response Body", responseBody.toString());
        } catch (Exception errorMessage) {
            onReadFail(errorMessage.toString());
        }
    }

    private void onReadFail(String errorMessage) {
        Log.i("Response", errorMessage);
        JSONObject json = new JSONObject();
        try {
            json.put("status", "502");
            json.put("desc", getErrorBody(errorMessage));
        } catch (JSONException e) {
            Log.i("Response", e.getMessage());
        }
        serviceCallBack.callbackReturn(json.toString());
    }

    private JSONObject getErrorBody(String errorMessage) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "Warning");
            json.put("description", errorMessage);
            json.put("title", "Warning");
        } catch (JSONException e) {
            Log.i("Response", e.getMessage());
        }
        return json;
    }


    private void onReadFail(Throwable response) {
        Log.i("Response on fail", response.toString());
        JSONObject json = new JSONObject();
        try {
            json.put("status", "502");
            json.put("desc", getErrorBody("Server error! Please try again"));
        } catch (JSONException e) {
            Log.i("Response", e.getMessage());
        }
        serviceCallBack.callbackReturn(json.toString());
        Log.i("Response Throwable", json.toString());
    }

    public void userRegister(String userEmail, String userMobile, String userImeiNumber, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.addUserRegister(userEmail, userMobile, userImeiNumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void userResetPassword(String userEmail, String userImeiNumber, String Password, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.resetPassword(userEmail, Password, userImeiNumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void userPinConfirmation(String userEmail, String userPin, String userImeiNumber, String pinType, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.pinConfirmation(userEmail, userPin, userImeiNumber, pinType);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getTicketType(String user_id, String user_imei_number, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getTicketType(user_id, user_imei_number);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getTicketSeries(String user_id, String user_imei_number, String ticketTypeId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getTicketSeries(user_id, user_imei_number, ticketTypeId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getTicketSerial(String user_id, String user_imei_number, String ticketseriesId, String drawId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getTicketSerial(user_id, user_imei_number, ticketseriesId, drawId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void updateTicketHold(String user_id, String user_imei_number, String ticketserialId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.updateTicketHold(user_id, user_imei_number, ticketserialId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.body();
                        try {
                            serviceCallBack.callbackReturn(jsonResponse.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void UpdateIMEILogin(String userId, String userIMEINumber, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.updateIMEINumber(userId, userIMEINumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void userLoginEmail(String userEmail, String userImeiNumber, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.userLoginEmail(userEmail, userImeiNumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void userForgetPassword(String userEmail, String userImeiNumber, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.forgetPassword(userEmail, userImeiNumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void userLoginPassword(String userPassword, String userImeiNumber, String userId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.userLoginPassword(userPassword, userImeiNumber, userId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        InputStream i = response.errorBody().byteStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(i));
                        StringBuilder errorResult = new StringBuilder();
                        String line;
                        try {
                            while ((line = r.readLine()) != null) {
                                errorResult.append(line).append('\n');
                            }
                            Log.i("error Body", errorResult.toString());
                            serviceCallBack.callbackReturn(errorResult.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getUserDetails(String userId, String imeiNumber, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getUserDetails(userId, imeiNumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }

                        serviceCallBack.callbackReturn(sb.toString());

                        Log.i("Response Body", sb.toString());
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getCountryDetails(String userId, String countryId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getCountry(countryId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }

                        serviceCallBack.callbackReturn(sb.toString());

                        Log.i("Response Body", sb.toString());
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getStateDetails(String countryId, String stateId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getState(countryId, stateId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }

                        serviceCallBack.callbackReturn(sb.toString());

                        Log.i("Response Body", sb.toString());
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getCityDetails(String stateId, String cityId, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getCity(stateId, cityId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }

                        serviceCallBack.callbackReturn(sb.toString());

                        Log.i("Response Body", sb.toString());
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void userSaveProfile(String userId, String firstName, String lastName, String gender, String dateOfBirth, String doorNo, String street, Integer city, Integer state, Integer country, String pincode, String userImeiNumber, Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.saveProfile(userId, firstName, lastName, gender, dateOfBirth, doorNo, street, city, state, country, pincode, userImeiNumber);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }

                        serviceCallBack.callbackReturn(sb.toString());

                        Log.i("Response Body", sb.toString());
                    } else {
                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void saveSecuritySettings(Context context, String user_id, String user_imei_number, String user_secret_question, String user_secret_answer) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.saveSecurityQuestion(user_id, user_imei_number, user_secret_question, user_secret_answer);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getRequestStatement(Context context, String user_id, String user_imei_number, String opt) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getRequestStatement(user_id, user_imei_number, opt);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void getCheckRegisterWithBank(Context context, String user_id, String user_imei_number) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getChkUserBank(user_id, user_imei_number);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void withDrawRequestAPI(Context context, String user_id, String user_imei_number, String amount) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.withDrawRequest(user_id, user_imei_number, amount);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void termsRequestAPI(Context context, String user_id, String user_imei_number, String terms) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getTermsCondition(user_id, user_imei_number, terms);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void saveBankAccount(Context context, String user_id, String user_imei_number,
                                String bankId, String branchName, String accountNumber, String accountName, String ifscCode) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.saveAccountDetails(user_id, user_imei_number, bankId,
                    branchName, accountNumber, accountName, ifscCode);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void saveSupport(Context context, String user_id, String user_imei_number,
                            String subject, String message) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.saveSupportRequest(user_id, user_imei_number, subject, message);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getSupportRequestDetails(Context context, String user_id,
                                         String user_imei_number,
                                         String ctId) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getRequestSupportDetails(user_id, user_imei_number, ctId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void getRequestSupport(Context context, String user_id, String user_imei_number, String opt) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getRequestSupport(user_id, user_imei_number, opt);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void getRequestStatement(Context context, String user_id, String user_imei_number, String opt, String startDate, String endDate) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getRequestStatement(user_id, user_imei_number, opt, startDate, endDate);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void getUserWalletDetails(Context context, String user_id, String user_imei_number) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getWalletDetails(user_id, user_imei_number);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getTransactionList(Context context, String user_id, String user_imei_number, String page_no, String approved) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getTransactionList(user_id, user_imei_number, page_no, approved);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getWithdrawRequestHistory(Context context, String user_id, String user_imei_number) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getWithdrawRequestHistory(user_id, user_imei_number);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getSecuritySettings(Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getSecurityQuestion();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful()) {
                        ResponseBody jsonResponse = response.errorBody();
                        BufferedReader reader = null;
                        StringBuilder sb = new StringBuilder();
                        if (jsonResponse != null) {
                            reader = new BufferedReader(new InputStreamReader(jsonResponse.byteStream()));
                        }
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch (IOException mJSONException) {
                            Log.i("JSONException", mJSONException.getMessage());
                        }
                        serviceCallBack.callbackReturn(sb.toString());
                        Log.i("Response Body", sb.toString());
                    } else {

                        onReadSuccess(response);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable response) {
                    onReadFail(response);
                }
            });

        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void updateHold(Context context, String userId, String imeiNumber, String ltId) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.updateTicketHold(userId, imeiNumber, ltId);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void updateUnHold(Context context, String userId, String imeiNumber, String ltId) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.updateTicketUnHold(userId, imeiNumber, ltId);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void purchaseSummary(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.purchaseSummary(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void cancelPurchase(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.cancelTickets(userId, imeiNumber, "0");
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void lotteryPurchase(Context context, String userId, String imeiNumber, HashMap<String, String> ticketArrayList) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.lotteryPurchase(userId, imeiNumber, ticketArrayList);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void confirmPurchase(Context context, String userId, String imeiNumber, HashMap<String, String> ticketArrayList) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.confirmPurchase(userId, imeiNumber, ticketArrayList);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void walletBalance(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.balanceDetails(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewBlockResult(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.viewBlockResult(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewMyPurchases(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.viewMyPurchases(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewResult(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getMyResult(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getBlockDetails(Context context, String userId, String imeiNumber, String blockId) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getBlockDetails(userId, imeiNumber, blockId);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getResultDetails(Context context, String userId, String imeiNumber, String lTypeId, String drDate) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getMyResultDetails(userId, imeiNumber, lTypeId, drDate);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewMyResultHistory(Context context, String userId, String imeiNumber, int month, int year) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getMyResultHistory(userId, imeiNumber, month, year);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewPrevBlocks(Context context, String userId, String imeiNumber, int month, int year) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getPrevBlocks(userId, imeiNumber, month, year);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewCurrentBlocks(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getCurrentBlocks(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewUserBlocks(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getUserBlocks(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void profileImage(Context context, File imageChallon) {
        try {
            Log.i("IMAGE", " File name: " + imageChallon.getName());
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageChallon);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageChallon.getName(), requestFile);
            Call<ResponseBody> call = request.profileImage(body);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void saveProfileImage(Context context, String userId, String imeiNumber,String profilePath) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.saveProfileImage(userId, imeiNumber,profilePath);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void depositImage(Context context, File imageChallon) {
        try {
            Log.i("IMAGE", " File name: " + imageChallon.getName());
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageChallon);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageChallon.getName(), requestFile);
            Call<ResponseBody> call = request.depositImage(body);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void removeImage(Context context, String userId, String imeiNumber, String transImage) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.removeImage(userId, imeiNumber, transImage);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getBeneficiaryAccount(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getBeneficiaryAccount(userId, imeiNumber);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void depositCash(Context context, String userId, String imeiNumber, String transDate, String transBaId,
                            String transMode, String transAmount, String transNotes, String transImage) {
        try {

            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.depositCash(userId, imeiNumber, transDate, transBaId, transMode, transAmount,
                    transNotes, transImage);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void depositCheque(Context context, String userId, String imeiNumber, String transDate,
                              String transBaId, String transMode, String transAmount, String transNotes,
                              String transChequeDate, String trefCheckNumber, String transAccountNumber,
                              String transBankId, String trefIFSCCode, String transImage) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.depositCheque(userId, imeiNumber, transDate, transBaId, transMode, transAmount, transNotes,
                    transChequeDate, trefCheckNumber, transBankId, trefIFSCCode, transImage);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void depositNetBanking(Context context, String userId, String imeiNumber, String transDate,
                                  String transBaId, String transMode, String transAmount, String transNotes,
                                  String trefAccountNumber, String transAccountName, String transBankId,
                                  String trefIFSCCode, String transChequeDate, String transactionNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.depositNetbanking(userId, imeiNumber, transDate,
                    transBaId, transMode, transAmount, transNotes, trefAccountNumber, transAccountName, transBankId,
                    trefIFSCCode, transChequeDate, transactionNumber);
            startAPI(call);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void getBankList(Context context) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getBankList();
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public void viewPreviousBlockEarning(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getPrevBlockEarning(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void viewCurrentBlockEarning(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getCurrBlockEarning(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void totalCurrentBlocks(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getTotalBlocks(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void earningBreakDown(Context context, String userId, String imeiNumber, String blockid) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.getBreakDown(userId, imeiNumber, blockid);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }

    public void deleteCustomers(Context context, String userId, String imeiNumber) {
        try {
            RequestApi request = getRetrofit(context).create(RequestApi.class);
            Call<ResponseBody> call = request.deleteCustomer(userId, imeiNumber);
            startAPI(call);
        } catch (NullPointerException errorMessage) {
            onReadFail(errorMessage);
        }
    }


    private void startAPI(Call<ResponseBody> call) {
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                int code = response.code();
                switch (response.code()) {
                    case 200:
                        onReadSuccess(response);
                        break;
                    case 201:
                        onReadSuccess(response);
                        break;
                    case 202:
                        onReadSuccess(response);
                        break;
                    default:
                        onReadFailure(response);
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable response) {
                onReadFail(response);
            }
        });
    }


    private void onReadFailure(Response<ResponseBody> response) {
        try {
            ResponseBody jsonResponse = response.errorBody();
            String apiResponse = jsonResponse.string();
            serviceCallBack.callbackReturn(apiResponse);
            Log.i("Reponse Body", apiResponse);
        } catch (Exception errorMessage) {
            onReadFail(errorMessage);
        }
    }


    public static interface ServiceCallBack {
        void callbackReturn(String string);
    }


}
