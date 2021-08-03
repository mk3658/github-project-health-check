package models.issue;

/**
 * @author Quan Kien Minh
 *
 */
public class IssueDetailModel {
    private Long number;

    public IssueDetailModel(Long number) {
        this.number = number;
    }

    public Long getNumber() {
        return number;
    }
}
