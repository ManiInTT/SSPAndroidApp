package ssp.tt.com.ssp.model;

public class PurchaseHistory {

    private String tmplsLtId;
    private String tmplsSelectedOn;

    public PurchaseHistory(String tmplsLtId, String tmplsSelectedOn, String lTypeName, String ltSeries, String ltNumber, String ltDrawCode, String ltRate) {
        this.tmplsLtId = tmplsLtId;
        this.tmplsSelectedOn = tmplsSelectedOn;
        this.lTypeName = lTypeName;
        this.ltSeries = ltSeries;
        this.ltNumber = ltNumber;
        this.ltDrawCode = ltDrawCode;
        this.ltRate = ltRate;
    }

    private String lTypeName;
    private String ltSeries;
    private String ltNumber;
    private String ltDrawCode;
    private String ltRate;

    public String getTmplsLtId() {
        return tmplsLtId;
    }

    public void setTmplsLtId(String tmplsLtId) {
        this.tmplsLtId = tmplsLtId;
    }

    public String getTmplsSelectedOn() {
        return tmplsSelectedOn;
    }

    public void setTmplsSelectedOn(String tmplsSelectedOn) {
        this.tmplsSelectedOn = tmplsSelectedOn;
    }

    public String getlTypeName() {
        return lTypeName;
    }

    public void setlTypeName(String lTypeName) {
        this.lTypeName = lTypeName;
    }

    public String getLtSeries() {
        return ltSeries;
    }

    public void setLtSeries(String ltSeries) {
        this.ltSeries = ltSeries;
    }

    public String getLtNumber() {
        return ltNumber;
    }

    public void setLtNumber(String ltNumber) {
        this.ltNumber = ltNumber;
    }

    public String getLtDrawCode() {
        return ltDrawCode;
    }

    public void setLtDrawCode(String ltDrawCode) {
        this.ltDrawCode = ltDrawCode;
    }

    public String getLtRate() {
        return ltRate;
    }

    public void setLtRate(String ltRate) {
        this.ltRate = ltRate;
    }


}
