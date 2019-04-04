package ssp.tt.com.ssp.model;

import org.json.JSONArray;

public class LotterySeries {


    private String lotteryId;

    public LotterySeries(String lotteryId, String lotterySeries, String lotteryDrawCode, String lotteryDrawDate, int lotteryCount, float lotteryRate, float seriousAmount, JSONArray jsonArray) {
        this.lotteryId = lotteryId;
        this.lotterySeries = lotterySeries;
        this.lotteryDrawCode = lotteryDrawCode;
        this.lotteryDrawDate = lotteryDrawDate;
        this.lotteryCount = lotteryCount;
        this.lotteryRate = lotteryRate;
        this.seriousAmount = seriousAmount;
        this.jsonArray = jsonArray;
    }

    private String lotterySeries;
    private String lotteryDrawCode;
    private String lotteryDrawDate;
    private int lotteryCount;
    private float lotteryRate;
    private float seriousAmount;
    private JSONArray jsonArray;


    public String getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(String lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotterySeries() {
        return lotterySeries;
    }

    public void setLotterySeries(String lotterySeries) {
        this.lotterySeries = lotterySeries;
    }

    public String getLotteryDrawCode() {
        return lotteryDrawCode;
    }

    public void setLotteryDrawCode(String lotteryDrawCode) {
        this.lotteryDrawCode = lotteryDrawCode;
    }

    public String getLotteryDrawDate() {
        return lotteryDrawDate;
    }

    public void setLotteryDrawDate(String lotteryDrawDate) {
        this.lotteryDrawDate = lotteryDrawDate;
    }

    public int getLotteryCount() {
        return lotteryCount;
    }


    public float getLotteryRate() {
        return lotteryRate;
    }

    public void setLotteryRate(float lotteryRate) {
        this.lotteryRate = lotteryRate;
    }

    public void setLotteryCount(int lotteryCount) {
        this.lotteryCount = lotteryCount;
    }

    public float getSeriousAmount() {
        return seriousAmount;
    }

    public void setSeriousAmount(float seriousAmount) {
        this.seriousAmount = seriousAmount;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }


}
