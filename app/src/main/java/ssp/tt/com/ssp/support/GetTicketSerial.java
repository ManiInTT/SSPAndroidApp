package ssp.tt.com.ssp.support;

/**
 * Created by Ecommerce on 27-Jun-18.
 */

public class GetTicketSerial {


    private String ticketNumber;
    private String ticketStatus;
    private String ticketId;

    public GetTicketSerial(String ticketNumber, String ticketStatus, String ticketId) {
        this.ticketNumber = ticketNumber;
        this.ticketStatus = ticketStatus;
        this.ticketId = ticketId;
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
}
