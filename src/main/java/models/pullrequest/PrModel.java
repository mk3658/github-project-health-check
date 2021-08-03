package models.pullrequest;

/**
 * @author Quan Kien Minh
 *
 */
public class PrModel {
    private String action;
    private Long number;
    private PrDetailModel pull_request;

    public PrModel(String action, Long number, PrDetailModel pull_request) {
        this.action = action;
        this.number = number;
        this.pull_request = pull_request;
    }

    public String getAction() {
        return action;
    }

    public Long getNumber() {
        return number;
    }

    public PrDetailModel getPullRequest() {
        return pull_request;
    }
}
