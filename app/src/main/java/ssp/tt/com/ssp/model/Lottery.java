package ssp.tt.com.ssp.model;

/**
 * Created by Ecommerce on 27-Jun-18.
 */

public class Lottery {

    private String ticketId;
    private String ticketNumber;
    private String ticketStatus;
    private boolean ticketSelected;

    public Lottery(String ticketId, String ticketNumber, String ticketStatus, boolean ticketSelected) {
        this.ticketNumber = ticketNumber;
        this.ticketStatus = ticketStatus;
        this.ticketId = ticketId;
        this.ticketSelected = ticketSelected;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public boolean isTicketSelected() {
        return ticketSelected;
    }

    public void setTicketSelected(boolean ticketSelected) {
        this.ticketSelected = ticketSelected;
    }
}
