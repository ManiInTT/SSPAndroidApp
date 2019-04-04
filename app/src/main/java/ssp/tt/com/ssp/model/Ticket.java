package ssp.tt.com.ssp.model;

public class Ticket {


    private String ticketId;
    private String ticketName;
    private String ticketType;
    private String ticketRate;
    private String ticketDrawDate;
    private String drawCode;

    public String getTicketDrawDate() {
        return ticketDrawDate;
    }

    public void setTicketDrawDate(String ticketDrawDate) {
        this.ticketDrawDate = ticketDrawDate;
    }

    public String getGrandPrize() {
        return grandPrize;
    }

    public void setGrandPrize(String grandPrize) {
        this.grandPrize = grandPrize;
    }

    private String grandPrize;
    private int selectedTicket;


    public Ticket(String ticketId, String ticketName, String ticketType, String ticketRate, String ticketDrawDate, String grandPrize, String drawCode, int selectedTicket) {
        this.ticketId = ticketId;
        this.ticketName = ticketName;
        this.ticketType = ticketType;
        this.ticketRate = ticketRate;
        this.ticketDrawDate = ticketDrawDate;
        this.grandPrize = grandPrize;
        this.drawCode = drawCode;
        this.selectedTicket = selectedTicket;
    }


    public String getDrawCode() {
        return drawCode;
    }

    public void setDrawCode(String drawCode) {
        this.drawCode = drawCode;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketRate() {
        return ticketRate;
    }

    public void setTicketRate(String ticketRate) {
        this.ticketRate = ticketRate;
    }

    public int getSelectedTicket() {
        return selectedTicket;
    }

    public void setSelectedTicket(int selectedTicket) {
        this.selectedTicket = selectedTicket;
    }


}
