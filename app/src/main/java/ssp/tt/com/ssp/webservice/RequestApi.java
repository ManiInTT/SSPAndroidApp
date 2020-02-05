package ssp.tt.com.ssp.webservice;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RequestApi {

    @FormUrlEncoded
    @POST("customer/savecustomer")
    Call<ResponseBody> addUserRegister(@Field("user_email") String user_email,
                                       @Field("user_mobile") String user_mobile,
                                       @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customer/chkcuspin")
    Call<ResponseBody> pinConfirmation(@Field("user_email") String user_email,
                                       @Field("user_pin") String user_pin,
                                       @Field("user_imei_number") String user_imei_number,
                                       @Field("pin_type") String pin_type);

    @FormUrlEncoded
    @POST("customer/resetpwd")
    Call<ResponseBody> resetPassword(@Field("user_email") String user_email,
                                     @Field("user_password") String user_password,
                                     @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customer/chkcusemail")
    Call<ResponseBody> userLoginEmail(@Field("user_email") String user_email,
                                      @Field("user_imei_number") String user_imei_number);


    @FormUrlEncoded
    @POST("customers/savedevice")
    Call<ResponseBody> updateIMEINumber(@Field("user_id") String userId,
                                        @Field("user_imei_number") String userIEMINumber);

    //@Headers("Content-Type: application/x-www-form-urlencoded")

    @FormUrlEncoded
    @POST("customer/chkcuspwd")
    Call<ResponseBody> userLoginPassword(@Field("user_password") String user_password,
                                         @Field("user_imei_number") String user_imei_number,
                                         @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("customer/forgetpwd")
    Call<ResponseBody> forgetPassword(@Field("user_email") String user_email,
                                      @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customers/saveuserprofile")
    Call<ResponseBody> saveProfile(@Field("user_id") String user_id,
                                   @Field("user_first_name") String user_first_name,
                                   @Field("user_last_name") String user_last_name,
                                   @Field("user_gender") String user_gender,
                                   @Field("user_dob") String user_dob,
                                   @Field("user_door_no") String user_door_no,
                                   @Field("user_street") String user_street,
                                   @Field("user_city") Integer user_city,
                                   @Field("user_state") Integer user_state,
                                   @Field("user_country") Integer user_country,
                                   @Field("user_pincode") String user_pincode,
                                   @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customers/savesecurity")
    Call<ResponseBody> saveSecurityQuestion(@Field("user_id") String user_id,
                                            @Field("user_imei_number") String user_imei_number,
                                            @Field("user_secret_question") String user_secret_question,
                                            @Field("user_secret_answer") String user_secret_answer);

    @GET("customers/getsecurity")
    Call<ResponseBody> getSecurityQuestion();

    @FormUrlEncoded
    @POST("customertrans/viewuserwallet")
    Call<ResponseBody> getWalletDetails(@Field("user_id") String user_id,
                                        @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customers/viewuserprofile")
    Call<ResponseBody> getUserDetails(@Field("user_id") String user_id,
                                      @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customertrans/viewusertrans")
    Call<ResponseBody> getTransactionList(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number,
                                          @Field("page_no") String page_no,@Field("approved") String approved);
    @FormUrlEncoded
    @POST("customertrans/withdrawrequesthistory")
    Call<ResponseBody> getWithdrawRequestHistory(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customertrans/viewusertransdet")
    Call<ResponseBody> getTransactionDetails(@Field("user_id") String user_id,
                                             @Field("user_imei_number") String user_imei_number,
                                             @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("ticket/viewtickettype")
    Call<ResponseBody> getTicketType(@Field("user_id") String user_id,
                                     @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("ticket/viewticketseries")
    Call<ResponseBody> getTicketSeries(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number,
                                       @Field("ltype_id") String ltype_id);

    @FormUrlEncoded
    @POST("ticket/viewticketserialno")
    Call<ResponseBody> getTicketSerial(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number,
                                       @Field("lt_series") String lt_series,
                                       @Field("lt_draw_id") String lt_draw_id);


    @FormUrlEncoded
    @POST("purchase/selectticket")
    Call<ResponseBody> updateTicketHold(@Field("user_id") String user_id,
                                        @Field("user_imei_number") String user_imei_number,
                                        @Field("lt_id") String lt_id);

    @FormUrlEncoded
    @POST("purchase/deselectbyid")
    Call<ResponseBody> updateTicketUnHold(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number,
                                          @Field("lt_id") String lt_id);

    @FormUrlEncoded
    @POST("purchase/getselectedticket")
    Call<ResponseBody> purchaseSummary(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("purchase/lotterypurchase")
    Call<ResponseBody> lotteryPurchase(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number,
                                       @FieldMap HashMap<String, String> lottery);

    @FormUrlEncoded
    @POST("purchase/confirmpurchase")
    Call<ResponseBody> confirmPurchase(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number,
                                       @FieldMap HashMap<String, String> lottery);

    @FormUrlEncoded
    @POST("customertrans/viewuserwallet")
    Call<ResponseBody> balanceDetails(@Field("user_id") String user_id,
                                      @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("result/viewblockresult")
    Call<ResponseBody> viewBlockResult(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("purchase/viewuserpurchase")
    Call<ResponseBody> viewMyPurchases(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number);


    @GET("customers/getcountry")
    Call<ResponseBody> getCountry(@Query("country_id") String country_id);

    @FormUrlEncoded
    @POST("customers/getstate")
    Call<ResponseBody> getState(@Field("country_id") String country_id,
                                @Field("state_id") String state_id);

    @FormUrlEncoded
    @POST("customers/getcity")
    Call<ResponseBody> getCity(@Field("state_id") String state_id,
                               @Field("city_id") String city_id);

    @FormUrlEncoded
    @POST("result/viewuserresult")
    Call<ResponseBody> getMyResult(@Field("user_id") String user_id,
                                   @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("result/viewuserresulthistory")
    Call<ResponseBody> getMyResultHistory(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number,
                                          @Field("month") int month,
                                          @Field("year") int year);

    @FormUrlEncoded
    @POST("customers/deletecustomer")
    Call<ResponseBody> deleteCustomer(@Field("user_id") String user_id,
                                      @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("blocks/getprevblock")
    Call<ResponseBody> getPrevBlocks(@Field("user_id") String user_id,
                                     @Field("user_imei_number") String user_imei_number,
                                     @Field("month") int month,
                                     @Field("year") int year);

    @FormUrlEncoded
    @POST("blocks/getcurblock")
    Call<ResponseBody> getCurrentBlocks(@Field("user_id") String user_id,
                                        @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("blocks/getuserblocks")
    Call<ResponseBody> getUserBlocks(@Field("user_id") String user_id,
                                        @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customers/saveuserphoto")
    Call<ResponseBody> saveProfileImage(@Field("user_id") String user_id,
                                     @Field("user_imei_number") String user_imei_number,
                                     @Field("user_photo_path") String user_photo_path);

    @GET("customertrans/getbank")
    Call<ResponseBody> getBankList();


    @FormUrlEncoded
    @POST("blocks/totalblockearn")
    Call<ResponseBody> getTotalBlocks(@Field("user_id") String user_id,
                                      @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("blocks/getblockbreakdown")
    Call<ResponseBody> getBreakDown(@Field("user_id") String user_id,
                                    @Field("user_imei_number") String user_imei_number,
                                    @Field("blk_id") String blk_id);

    @FormUrlEncoded
    @POST("blocks/prevblockearn")
    Call<ResponseBody> getPrevBlockEarning(@Field("user_id") String user_id,
                                           @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("blocks/curblockearn")
    Call<ResponseBody> getCurrBlockEarning(@Field("user_id") String user_id,
                                           @Field("user_imei_number") String user_imei_number);


    @Multipart
    @POST("customertrans/uploadtransimage")
    Call<ResponseBody> depositImage(@Part MultipartBody.Part depositPhoto);

    @Multipart
    @POST("customers/uploaduserphoto")
    Call<ResponseBody> profileImage(@Part MultipartBody.Part depositPhoto);

    @FormUrlEncoded
    @POST("customertrans/removeuploadimage")
    Call<ResponseBody> removeImage(@Field("user_id") String userid, @Field("user_imei_number") String user_imei_number,
                                   @Field("trans_image") String transImage);


    @FormUrlEncoded
    @POST("customertrans/saveuserdeposit")
    Call<ResponseBody> depositCash(@Field("user_id") String userid, @Field("user_imei_number") String user_imei_number,
                                   @Field("trans_date") String transDate, @Field("trans_ba_id") String transBaId,
                                   @Field("trans_mode") String transMode, @Field("trans_amount") String transAmount,
                                   @Field("trans_notes") String transNotes, @Field("trans_image") String transImage);


    @FormUrlEncoded
    @POST("customertrans/saveuserdeposit")
    Call<ResponseBody> depositCheque(@Field("user_id") String user_id, @Field("user_imei_number") String user_imei_number,
                                     @Field("trans_date") String transDate, @Field("trans_ba_id") String transBaId,
                                     @Field("trans_mode") String transMode, @Field("trans_amount") String transAmount,
                                     @Field("trans_notes") String transNotes, @Field("transc_cheque_date") String trefCheckDate,
                                     @Field("transc_cheque_no") String trefCheckNumber, @Field("transc_bank_id") String trefBankId,
                                     @Field("transc_bank_ifsc") String trefIFSCCode, @Field("trans_image") String transImage);

    @FormUrlEncoded
    @POST("customertrans/saveuserdeposit")
    Call<ResponseBody> depositNetbanking(@Field("user_id") String user_id, @Field("user_imei_number") String user_imei_number,
                                         @Field("trans_date") String transDate, @Field("trans_ba_id") String transBaId,
                                         @Field("trans_mode") String transMode, @Field("trans_amount") String transAmount,
                                         @Field("trans_notes") String transNotes, @Field("tref_acc_no") String trefAccountNumber,
                                         @Field("tref_acc_name") String transAccountName, @Field("tref_bank_id") String trefBankId,
                                         @Field("tref_bank_ifsc") String trefIFSCCode, @Field("tref_date") String trefDate,
                                         @Field("tref_ref_no") String transactionNumber);

    @FormUrlEncoded
    @POST("purchase/deselectticket")
    Call<ResponseBody> cancelTickets(@Field("user_id") String user_id,
                                     @Field("user_imei_number") String user_imei_number, @Field("all") String all);


    @FormUrlEncoded
    @POST("blocks/getblockbyid")
    Call<ResponseBody> getBlockDetails(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number,
                                       @Field("blk_id") String blk_id);

    @FormUrlEncoded
    @POST("result/viewresultbydate")
    Call<ResponseBody> getMyResultDetails(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number,
                                          @Field("ltype_id") String ltype_id,
                                          @Field("dr_date") String dr_date);

    @FormUrlEncoded
    @POST("customertrans/viewbeneficiaryacc")
    Call<ResponseBody> getBeneficiaryAccount(@Field("user_id") String user_id,
                                             @Field("user_imei_number") String user_imei_number);


    @FormUrlEncoded
    @POST("customertrans/getrequeststatement")
    Call<ResponseBody> getRequestStatement(@Field("user_id") String user_id,
                                           @Field("user_imei_number") String user_imei_number,
                                           @Field("opt") String opt);

    @FormUrlEncoded
    @POST("customers/getterms")
    Call<ResponseBody> getTermsCondition(  @Field("user_id") String user_id,
                                           @Field("user_imei_number") String user_imei_number,
                                           @Field("terms_for") String terms_for);

    @FormUrlEncoded
    @POST("customertrans/getrequeststatement")
    Call<ResponseBody> getRequestStatement(@Field("user_id") String user_id,
                                           @Field("user_imei_number") String user_imei_number,
                                           @Field("opt") String opt,
                                           @Field("start_date") String start_date,
                                           @Field("end_date") String end_date);


    @FormUrlEncoded
    @POST("customertrans/chkuserbank")
    Call<ResponseBody> getChkUserBank(@Field("user_id") String user_id,
                                      @Field("user_imei_number") String user_imei_number);

    @FormUrlEncoded
    @POST("customertrans/withdrawrequest")
    Call<ResponseBody> withDrawRequest(@Field("user_id") String user_id,
                                       @Field("user_imei_number") String user_imei_number,
                                       @Field("wr_amt") String wr_amt);

    @FormUrlEncoded
    @POST("customers/saveuserbank")
    Call<ResponseBody> saveAccountDetails(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number,
                                          @Field("ub_bank_id") String ub_bank_id,
                                          @Field("ub_branch_name") String ub_branch_name,
                                          @Field("ub_acc_no") String ub_acc_no,
                                          @Field("ub_acc_name") String ub_acc_name,
                                          @Field("ub_ifsc_code") String ub_ifsc_code);

    @FormUrlEncoded
    @POST("trouble/savetrouble")
    Call<ResponseBody> saveSupportRequest(@Field("user_id") String user_id,
                                          @Field("user_imei_number") String user_imei_number,
                                          @Field("ct_subject") String ct_subject,
                                          @Field("ct_message") String ct_message);


    @FormUrlEncoded
    @POST("trouble/viewtroublehistory")
    Call<ResponseBody> getRequestSupport(@Field("user_id") String user_id,
                                           @Field("user_imei_number") String user_imei_number,
                                           @Field("opt") String opt);

    @FormUrlEncoded
    @POST("trouble/viewtroubledetails")
    Call<ResponseBody> getRequestSupportDetails(@Field("user_id") String user_id,
                                         @Field("user_imei_number") String user_imei_number,
                                         @Field("ct_id") String ct_id);




}
