package ssp.tt.com.ssp.model;

public class SupportRequest {

    private String requestId;
    private String requestSerialNo;

    public SupportRequest(String requestId, String requestSerialNo, String requestSubject, String supportDate, String supportStatus) {
        this.requestId = requestId;
        this.requestSerialNo = requestSerialNo;
        this.requestSubject = requestSubject;
        this.supportDate = supportDate;
        this.supportStatus = supportStatus;
    }

    private String requestSubject;
    private String supportDate;
    private String supportStatus;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestSerialNo() {
        return requestSerialNo;
    }

    public void setRequestSerialNo(String requestSerialNo) {
        this.requestSerialNo = requestSerialNo;
    }

    public String getRequestSubject() {
        return requestSubject;
    }

    public void setRequestSubject(String requestSubject) {
        this.requestSubject = requestSubject;
    }

    public String getSupportDate() {
        return supportDate;
    }

    public void setSupportDate(String supportDate) {
        this.supportDate = supportDate;
    }

    public String getSupportStatus() {
        return supportStatus;
    }

    public void setSupportStatus(String supportStatus) {
        this.supportStatus = supportStatus;
    }



}
