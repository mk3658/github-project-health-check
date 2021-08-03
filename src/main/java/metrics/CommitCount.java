package metrics;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Quan Kien Minh
 *
 */
public class CommitCount {
	// Map used to store the number of commits of each Github project. (Github project is identified by its organization and repository's name)
	private Map<List<String>, Double> commitCountMap;
	
	// The maximum number of commits
	private Double maxNumberOfCommits;
	
	// Map used to store the health metric after groupbying the Github projects based on its organzation and repository's name
	private Map<List<String>, Double> commitHealthMetricMap;
	
	// The number of days
	private Double numberOfDays;
	
	public CommitCount(Double numberOfDays) {
		this.setNumberOfDays(numberOfDays);
		this.commitCountMap = new HashMap<List<String>, Double>();
		this.maxNumberOfCommits = 0.0;
		this.commitHealthMetricMap = new HashMap<List<String>, Double>();
	}
	
	public void putIntoCommitCount(String org, String repoName) {
		String[] parameters = {org, repoName};
		List<String> key = Arrays.asList(parameters);
		
		// Check if the project exists before?
		if (this.commitCountMap.containsKey(key)) {
			// Yes, increase the number of commits and compare to the maximum number of commits
			Double numberOfCommits = commitCountMap.get(key) + 1.0/numberOfDays;
			maxNumberOfCommits = maxNumberOfCommits < numberOfCommits ? numberOfCommits : maxNumberOfCommits;
			
			// Update the number of commit
			commitCountMap.put(key, numberOfCommits);
		} else {
			// No, put it to the map
			this.commitCountMap.put(key, 1.0/numberOfDays);
		}
	}
	
	public void calculateHealthByMetric() {
		commitCountMap.forEach((key, value) -> {
			// Calculate health by the metric: number of commits / maximum number of commits
			commitHealthMetricMap.put(key, value/maxNumberOfCommits);
		});
	}

	public Map<List<String>, Double> getCommitCountMap() {
		return commitCountMap;
	}

	public void setCommitCountMap(Map<List<String>, Double> commitCountMap) {
		this.commitCountMap = commitCountMap;
	}

	public Double getMaxNumberOfCommits() {
		return maxNumberOfCommits;
	}

	public void setMaxNumberOfCommits(Double maxNumberOfCommits) {
		this.maxNumberOfCommits = maxNumberOfCommits;
	}

	public Map<List<String>, Double> getCommitHealthMetricMap() {
		return commitHealthMetricMap;
	}

	public void setCommitHealthMetricMap(Map<List<String>, Double> commitHealthMetricMap) {
		this.commitHealthMetricMap = commitHealthMetricMap;
	}

	public Double getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(Double numberOfDays) {
		this.numberOfDays = numberOfDays;
	}
	
	public String getHealthByOrgAndRepoName(String org, String repoName) {
		String result = null;
		String[] parameters = {org, repoName};
		List<String> key = Arrays.asList(parameters);
		
		if (commitCountMap.containsKey(key)) {
			result = org + ", " + repoName + ", " + commitCountMap.get(key) + ", " + maxNumberOfCommits + ", " + commitHealthMetricMap.get(key);
		}
		return result;
	}
	
	public String getHealthByOrgAndRepoName(List<String> key) {
		String result = null;
		
		if (commitCountMap.containsKey(key)) {
			result = key.get(0) + ", " + key.get(1) + ", " + commitCountMap.get(key) + ", " + maxNumberOfCommits + ", " + commitHealthMetricMap.get(key);
		}
		return result;
	}
	
	public List<Object> getHealthByOrgAndRepoNameReturnList(List<String> key) {
		List<Object> result = null;
		
		if (commitCountMap.containsKey(key)) {
			result = new ArrayList<>();
			result.add(commitCountMap.get(key)); // Number of commits
			result.add(maxNumberOfCommits); // Maximum of commits
			result.add(commitHealthMetricMap.get(key)); // actual health of this project based on this metric
		}
		return result;
	}

	public List<String> getResult() {
		List<String> result = new ArrayList<String>();
		commitCountMap.forEach((key, value) -> {
			result.add(this.getHealthByOrgAndRepoName(key));
		});
		return result;
	}
}
