package models.pullrequest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Quan Kien Minh
 *
 */
public class PrDetailModel {
    private Boolean merged;
    private Timestamp created_at;
    private Timestamp merged_at;

    public PrDetailModel(Boolean merged, String created_at, String merged_at) throws ParseException {
        this.merged = merged;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        this.created_at = new Timestamp(sdf.parse(created_at).getTime());
        this.merged_at = new Timestamp(sdf.parse(merged_at).getTime());
    }

    public Boolean getMerged() {
        return merged;
    }

	public Timestamp getCreated_at() {
		return created_at;
	}

	public Timestamp getMerged_at() {
		return merged_at;
	}
}
