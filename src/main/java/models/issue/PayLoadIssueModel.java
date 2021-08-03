package models.issue;

/**
 * @author Quan Kien Minh
 *
 */
public class PayLoadIssueModel {
    private IssueModel payload;

    public PayLoadIssueModel(IssueModel payload) {
        this.payload = payload;
    }

    public IssueModel getPayload() {
        return payload;
    }
}

