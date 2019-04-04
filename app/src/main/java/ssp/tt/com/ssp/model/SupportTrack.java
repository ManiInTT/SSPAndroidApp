package ssp.tt.com.ssp.model;

public class SupportTrack {

    private String agentName;
    private String tsCommentOn;
    private String tsStatus;
    private String tsComment;


    public SupportTrack(String agentName, String tsCommentOn, String tsStatus, String tsComment) {
        this.agentName = agentName;
        this.tsCommentOn = tsCommentOn;
        this.tsStatus = tsStatus;
        this.tsComment = tsComment;
    }


    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getTsCommentOn() {
        return tsCommentOn;
    }

    public void setTsCommentOn(String tsCommentOn) {
        this.tsCommentOn = tsCommentOn;
    }

    public String getTsStatus() {
        return tsStatus;
    }

    public void setTsStatus(String tsStatus) {
        this.tsStatus = tsStatus;
    }

    public String getTsComment() {
        return tsComment;
    }

    public void setTsComment(String tsComment) {
        this.tsComment = tsComment;
    }


}
