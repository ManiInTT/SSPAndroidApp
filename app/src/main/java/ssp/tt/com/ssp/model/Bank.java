package ssp.tt.com.ssp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bank implements Parcelable {

    protected Bank(Parcel in) {
        bankId = in.readString();
        bankName = in.readString();
    }

    public static final Creator<Bank> CREATOR = new Creator<Bank>() {
        @Override
        public Bank createFromParcel(Parcel in) {
            return new Bank(in);
        }

        @Override
        public Bank[] newArray(int size) {
            return new Bank[size];
        }
    };

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Bank(String bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }

    private String bankId;
    private String bankName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bankId);
        dest.writeString(bankName);
    }
}
