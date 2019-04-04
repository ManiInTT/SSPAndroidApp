package ssp.tt.com.ssp.model;

public class MyBlockResultDao {


    private String blockId;
    private String drawId;
    private String blockType;
    private String ticketTypeName;
    private String blockName;
    private String drawCode;
    private String date;
    private String member;
    private String prize;
    private String ltSerious;
    private String breakPosition;
    private String month;
    private String ltNumber;



    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getDrawId() {
        return drawId;
    }

    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getDrawCode() {
        return drawCode;
    }

    public void setDrawCode(String drawCode) {
        this.drawCode = drawCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getLtSerious() {
        return ltSerious;
    }

    public void setLtSerious(String ltSerious) {
        this.ltSerious = ltSerious;
    }

    public String getBreakPosition() {
        return breakPosition;
    }

    public void setBreakPosition(String breakPosition) {
        this.breakPosition = breakPosition;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getLtNumber() {
        return ltNumber;
    }

    public void setLtNumber(String ltNumber) {
        this.ltNumber = ltNumber;
    }


    public MyBlockResultDao(String blockId, String drawId, String blockType, String ticketTypeName, String blockName, String drawCode, String date, String member, String prize, String ltSerious, String breakPosition, String month) {
        this.blockId = blockId;
        this.drawId = drawId;
        this.blockType = blockType;
        this.ticketTypeName = ticketTypeName;
        this.blockName = blockName;
        this.drawCode = drawCode;
        this.date = date;
        this.member = member;
        this.prize = prize;
        this.ltSerious = ltSerious;
        this.breakPosition = breakPosition;
        this.month = month;
    }

    public MyBlockResultDao(String blockId, String drawId, String blockType, String ticketTypeName, String blockName, String drawCode, String date, String member, String prize, String ltSerious, String breakPosition, String month, String ltNumber) {
        this.blockId = blockId;
        this.drawId = drawId;
        this.blockType = blockType;
        this.ticketTypeName = ticketTypeName;
        this.blockName = blockName;
        this.drawCode = drawCode;
        this.date = date;
        this.member = member;
        this.prize = prize;
        this.ltSerious = ltSerious;
        this.breakPosition = breakPosition;
        this.month = month;
        this.ltNumber = ltNumber;
    }


}
