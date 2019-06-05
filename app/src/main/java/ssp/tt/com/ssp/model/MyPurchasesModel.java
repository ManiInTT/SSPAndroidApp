package ssp.tt.com.ssp.model;

public class MyPurchasesModel {


    private String pur_id;
    private String pur_date;
    private String trans_amount;
    private String no_of_units;




    public String getPur_id() {
        return pur_id;
    }

    public void setPur_id(String pur_id) {
        this.pur_id = pur_id;
    }

    public String getPur_date() {
        return pur_date;
    }

    public void setPur_date(String pur_date) {
        this.pur_date = pur_date;
    }

    public String getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(String trans_amount) {
        this.trans_amount = trans_amount;
    }

    public String getNo_of_units() {
        return no_of_units;
    }

    public void setNo_of_units(String no_of_units) {
        this.no_of_units = no_of_units;
    }



    public MyPurchasesModel(String pur_id, String pur_date, String trans_amount, String no_of_units) {
        this.pur_id = pur_id;
        this.pur_date = pur_date;
        this.trans_amount = trans_amount;
        this.no_of_units = no_of_units;

    }




}
