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
public class MergedPullRequest {
	// Map used to store the merged pull request that grouped by its org, repoName, and payload number
	private Map<List<String>, List<Object>> mergedPullRequestMap;
	
	// The minimum merged duration: merged duration = merged_at - created_at
	private Long minMergedDuration;
	
	// Map used to store the health metric that grouped by its org, repoName
	private Map<List<String>, List<Object>> mergedPullRequestMetricMap;

	public MergedPullRequest() {
		this.mergedPullRequestMap = new HashMap<List<String>, List<Object>>();
		this.mergedPullRequestMetricMap = new HashMap<>();
		this.minMergedDuration = Long.MAX_VALUE;
	}

	public void putIntomergedPullRequest(String org, String repoName, String payLoadNumber, String payLoadAction,
			Timestamp createdAt, Boolean isMerged, Timestamp mergedAt) {
		String[] parameters = { org, repoName, payLoadNumber };
		List<String> key = Arrays.asList(parameters);
		
		// Check whether it is merged and 'closed' status before moving forward
		if (isMerged && payLoadAction.compareTo("closed") == 0) {
			// Check if the key exists or not
			if (mergedPullRequestMap.containsKey(key)) {
				// Yes, update the opened_at and merged_at.
				List<Object> values = mergedPullRequestMap.get(key);
				String action = (String) values.get(0);
				Timestamp openedAt = (Timestamp) values.get(1);
				Timestamp mergedAtTemp = (Timestamp) values.get(2);

				// Try to find min(opened_at)
				if ((action.compareTo("opened") == 0 && openedAt == null)
						|| (action.compareTo("opened") == 0 && openedAt.compareTo(createdAt) > 0)) {
					openedAt = createdAt;
				}

				// Try to find max(merged_at)
				if ((action.compareTo("closed") == 0 && mergedAtTemp == null)
						|| (action.compareTo("closed") == 0 && mergedAtTemp.compareTo(mergedAt) < 0)) {
					mergedAtTemp = mergedAt;
				}

				Object[] newValues = { action, openedAt, mergedAtTemp, isMerged };

				mergedPullRequestMap.put(key, Arrays.asList(newValues));
			} else {
				// No, add a new instance
				Object[] values = { 
						payLoadAction, 
						createdAt,
						mergedAt,
						isMerged};
				mergedPullRequestMap.put(key, Arrays.asList(values));
			}
		}
	}

	public void calculatHealthMetric() {
		// Initalize the temp map used to copy items in map
		Map<List<String>, List<Object>> temp = new HashMap<>();
		
		// Loop to look for the minimum merged duration
		mergedPullRequestMap.forEach((key, values) -> {
			String org = key.get(0);
			String repoName = key.get(1);
			String[] parameters = { org, repoName };
			List<String> keyInMetric = Arrays.asList(parameters);

			Timestamp openedAt = (Timestamp) values.get(1);
			Timestamp mergedAt = (Timestamp) values.get(2);
			
			// Check whether the opened_at is less than merged_at
			if (openedAt != null && mergedAt != null && mergedAt.compareTo(openedAt) >= 0) {
				Long duration = mergedAt.getTime() - openedAt.getTime();
				// Find the minimum merged duration
				minMergedDuration = minMergedDuration > duration ? duration : minMergedDuration;
				
				// Check the key is in mergedPullRequestMetric Map
				if (mergedPullRequestMetricMap.containsKey(keyInMetric)) {
					// Yes, accumulate the duration, and increase the count
					Long sumDuration = (Long) mergedPullRequestMetricMap.get(keyInMetric).get(0) + duration;
					Integer no = (Integer) mergedPullRequestMetricMap.get(keyInMetric).get(1) + 1;
					Object[] valuesInMetricMap = { sumDuration, no };
					mergedPullRequestMetricMap.put(keyInMetric, Arrays.asList(valuesInMetricMap));
				} else {
					Object[] valuesInMetricMap = { duration, 1 };
					mergedPullRequestMetricMap.put(keyInMetric, Arrays.asList(valuesInMetricMap));
				}
			}
		});

		mergedPullRequestMetricMap.forEach((key, values) -> {
			// Calculate the average duration for each project was merged
			Long averageDuration = (Long) (values.get(0)) / (Integer) values.get(1);
			
			// Calculate the health metric based on: minimum merged duration / average duration
			Double metric = (minMergedDuration * 1.0) / (averageDuration * 1.0);
			Object[] valuesInMetricMap = { averageDuration, metric };
			temp.put(key, Arrays.asList(valuesInMetricMap));
		});
		
		mergedPullRequestMetricMap = temp;
	}

	public Map<List<String>, List<Object>> getmergedPullRequestMap() {
		return mergedPullRequestMap;
	}

	public void setmergedPullRequestMap(Map<List<String>, List<Object>> mergedPullRequestMap) {
		this.mergedPullRequestMap = mergedPullRequestMap;
	}

	public Map<List<String>, List<Object>> getmergedPullRequestMetricMap() {
		return mergedPullRequestMetricMap;
	}

	public void setmergedPullRequestMetricMap(Map<List<String>, List<Object>> mergedPullRequestMetricMap) {
		this.mergedPullRequestMetricMap = mergedPullRequestMetricMap;
	}

	public String getHealthByOrgAndRepoName(String org, String repoName) {
		String result = null;
		String[] parameters = { org, repoName };
		List<String> key = Arrays.asList(parameters);

		if (mergedPullRequestMetricMap.containsKey(key)) {
			result = org + ", " + repoName + ", " + minMergedDuration + ", " + mergedPullRequestMetricMap.get(key).get(0)
					+ ", " + mergedPullRequestMetricMap.get(key).get(1);
		}
		return result;
	}

	public String getHealthByOrgAndRepoName(List<String> key) {
		String result = null;

		if (mergedPullRequestMetricMap.containsKey(key)) {
			result = key.get(0) + ", " + key.get(1) + ", " + minMergedDuration + ", "
					+ mergedPullRequestMetricMap.get(key).get(0) + ", " + mergedPullRequestMetricMap.get(key).get(1);
		}
		return result;
	}
	
	public List<Object> getHealthByOrgAndRepoNameReturnList(List<String> key) {
		List<Object> result = null;
		
		if (mergedPullRequestMetricMap.containsKey(key)) {
			result = new ArrayList<>();
			result.add(mergedPullRequestMetricMap.get(key).get(0)); // the average merged duration
			result.add(minMergedDuration); // the minimum merged duration
			result.add(mergedPullRequestMetricMap.get(key).get(1)); // the actual health metric for merged pull request
		}
		return result;
	}

	public List<String> getResult() {
		List<String> result = new ArrayList<String>();
		mergedPullRequestMetricMap.forEach((key, value) -> {
			result.add(this.getHealthByOrgAndRepoName(key));
		});
		return result;
	}
}
