package metrics;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Quan Kien Minh
 *
 */
public class OpenedIssue {
	// Map used to store the opened issue that grouped by its org, repoName, and payload number
	private Map<List<String>, List<Object>> openedIssueMap;
	
	// The minimum opened duration 
	private Long minOpenedDuration;
	
	// Map used to store the health metric of each project based on its org, repoName
	private Map<List<String>, List<Object>> openedIssueMetricMap;
	
	// The current time tick
	private Long nowTick;

	public OpenedIssue(Long nowTick) {
		this.openedIssueMap = new HashMap<List<String>, List<Object>>();
		this.openedIssueMetricMap = new HashMap<>();
		this.nowTick = nowTick;
		this.minOpenedDuration = Long.MAX_VALUE;
	}

	public void putIntoOpenedIssue(String org, String repoName, String payLoadNumber, String payLoadAction,
			Timestamp createdAt) {
		String[] parameters = { org, repoName, payLoadNumber };
		List<String> key = Arrays.asList(parameters);

		if (openedIssueMap.containsKey(key)) {
			List<Object> values = openedIssueMap.get(key);
			String action = (String) values.get(0);
			Timestamp openedAt = (Timestamp) values.get(1);
			Timestamp reopenedAt = (Timestamp) values.get(2);
			Timestamp closedAt = (Timestamp) values.get(3);
			
			// If the payload action is opened, update opened_at = MIN(opened_at, created_at)
			if ((action.compareTo("opened") == 0 && openedAt == null)
					|| (action.compareTo("opened") == 0 && openedAt.compareTo(createdAt) > 0)) {
				openedAt = createdAt;
			}

			// If the payload action is reopened, update reopened_at = MAX(reopened_at, created_at)
			if ((action.compareTo("reopened") == 0 && reopenedAt == null)
					|| (action.compareTo("reopened") == 0 && reopenedAt.compareTo(createdAt) < 0)) {
				reopenedAt = createdAt;
			}

			// If the payload action is closed, update closed_at = MAX(closed_at, created_at)
			if ((action.compareTo("closed") == 0 && closedAt == null)
					|| (action.compareTo("closed") == 0 && closedAt.compareTo(createdAt) < 0)) {
				closedAt = createdAt;
			}

			Object[] newValues = { action, openedAt, reopenedAt, closedAt };

			openedIssueMap.put(key, Arrays.asList(newValues));
		} else {
			Object[] values = { payLoadAction, payLoadAction.compareTo("opened") == 0 ? createdAt : null,
					payLoadAction.compareTo("reopened") == 0 ? createdAt : null,
					payLoadAction.compareTo("closed") == 0 ? createdAt : null };
			openedIssueMap.put(key, Arrays.asList(values));
		}
	}

	public void calculatHealthMetric() {
		Map<List<String>, List<Object>> temp = new HashMap<>();
		openedIssueMap.forEach((key, values) -> {
			String org = key.get(0);
			String repoName = key.get(1);
			String[] parameters = { org, repoName };
			List<String> keyInMetric = Arrays.asList(parameters);

			Timestamp openedAt = (Timestamp) values.get(1);
			Timestamp reopenedAt = (Timestamp) values.get(2);
			Timestamp closedAt = (Timestamp) values.get(3);

			// Check if the current project is opened or not
			if (openedAt != null && ((reopenedAt == null && closedAt == null)
					|| (reopenedAt != null && closedAt != null && reopenedAt.compareTo(closedAt) > 0))) {
				Long duration = nowTick - openedAt.getTime();
				
				// Find the minimum opened duration
				minOpenedDuration = minOpenedDuration > duration ? duration : minOpenedDuration;
				
				// Check whether the key is in openedIssueMetricMap
				if (openedIssueMetricMap.containsKey(keyInMetric)) {
					// Yes, accumulate the duration, and increase the count
					Long sumDuration = (Long) openedIssueMetricMap.get(keyInMetric).get(0) + duration;
					Integer no = (Integer) openedIssueMetricMap.get(keyInMetric).get(1) + 1;
					Object[] valuesInMetricMap = { sumDuration, no };
					openedIssueMetricMap.put(keyInMetric, Arrays.asList(valuesInMetricMap));
				} else {
					// No, add a new instance
					Object[] valuesInMetricMap = { duration, 1 };
					openedIssueMetricMap.put(keyInMetric, Arrays.asList(valuesInMetricMap));
				}
			}
		});

		openedIssueMetricMap.forEach((key, values) -> {
			// Calculate the average duration for the opened issue
			Long averageDuration = (Long) (values.get(0)) / (Integer) values.get(1);
			
			// The health metric: minimum duration / average duration
			Double metric = (minOpenedDuration * 1.0) / (averageDuration * 1.0);
			Object[] valuesInMetricMap = { averageDuration, metric };
			temp.put(key, Arrays.asList(valuesInMetricMap));
		});
		
		openedIssueMetricMap = temp;
	}

	public Map<List<String>, List<Object>> getOpenedIssueMap() {
		return openedIssueMap;
	}

	public void setOpenedIssueMap(Map<List<String>, List<Object>> openedIssueMap) {
		this.openedIssueMap = openedIssueMap;
	}

	public Map<List<String>, List<Object>> getOpenedIssueMetricMap() {
		return openedIssueMetricMap;
	}

	public void setOpenedIssueMetricMap(Map<List<String>, List<Object>> openedIssueMetricMap) {
		this.openedIssueMetricMap = openedIssueMetricMap;
	}

	public Long getNowTick() {
		return nowTick;
	}

	public void setNowTick(Long nowTick) {
		this.nowTick = nowTick;
	}

	public String getHealthByOrgAndRepoName(String org, String repoName) {
		String result = null;
		String[] parameters = { org, repoName };
		List<String> key = Arrays.asList(parameters);

		if (openedIssueMetricMap.containsKey(key)) {
			result = org + ", " + repoName + ", " + minOpenedDuration + ", " + openedIssueMetricMap.get(key).get(0)
					+ ", " + openedIssueMetricMap.get(key).get(1);
		}
		return result;
	}

	public String getHealthByOrgAndRepoName(List<String> key) {
		String result = null;

		if (openedIssueMetricMap.containsKey(key)) {
			result = key.get(0) + ", " + key.get(1) + ", " + minOpenedDuration + ", "
					+ openedIssueMetricMap.get(key).get(0) + ", " + openedIssueMetricMap.get(key).get(1);
		}
		return result;
	}
	
	public List<Object> getHealthByOrgAndRepoNameReturnList(List<String> key) {
		List<Object> result = null;
		
		if (openedIssueMetricMap.containsKey(key)) {
			result = new ArrayList<>();
			result.add(openedIssueMetricMap.get(key).get(0)); // the average duration for the opened issue
			result.add(openedIssueMetricMap.get(key).get(1)); // the actual health metric for this opened issue
		}
		return result;
	}

	public List<String> getResult() {
		List<String> result = new ArrayList<String>();
		openedIssueMetricMap.forEach((key, value) -> {
			result.add(this.getHealthByOrgAndRepoName(key));
		});
		return result;
	}
}
