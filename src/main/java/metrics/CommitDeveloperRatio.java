package metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Quan Kien Minh
 *
 */
public class CommitDeveloperRatio {
	// Map used to store the number of commits based on org and repoName
	private Map<List<String>, Double> commitDeveloperMap;
	
	// Map used to store the number of distinct developers in each project
	private Map<List<String>, HashSet<String>> developerMap;
	
	// The maximum ratio
	private Double maximumRatio;
	
	// Map used to store the health of each project
	private Map<List<String>, Double> commitHealthMetricMap;
	
	public CommitDeveloperRatio() {
		this.commitDeveloperMap = new HashMap<List<String>, Double>();
		this.maximumRatio = 0.0;
		this.commitHealthMetricMap = new HashMap<List<String>, Double>();
		this.developerMap = new HashMap<>();
	}
	
	public void putIntoCommitDeveloper(String org, String repoName, String actor) {
		String[] parameters = {org, repoName};
		List<String> key = Arrays.asList(parameters);
		
		// Check whether the key exists in commitDeveloperMap
		if (this.commitDeveloperMap.containsKey(key)) {
			// Yes, increase one unit
			Double numberOfCommits = commitDeveloperMap.get(key) + 1.0;
			commitDeveloperMap.put(key, numberOfCommits);
			
			// Add the new developer to developerMap
			developerMap.get(key).add(actor);
		} else {
			// No, put the new instance
			this.commitDeveloperMap.put(key, 1.0);
			HashSet<String> actors = new HashSet<>();
			actors.add(actor);
			developerMap.put(key, actors);
		}
	}
	
	public void calculateHealthByMetric() {
		// Find the maximum ratio 
		commitDeveloperMap.forEach((key, value) -> {
			Double temp = value / (developerMap.get(key).size()*1.0);
			maximumRatio = maximumRatio < temp ? temp : maximumRatio;
		});
		
		// Calculate the health based on the metric: (number of commits / number of distinct developers) / maximum ratio
		commitDeveloperMap.forEach((key, value) -> {
			Double temp = value / (developerMap.get(key).size()*1.0);
			commitHealthMetricMap.put(key, temp/maximumRatio);
		});
	}

	public Map<List<String>, Double> getCommitDeveloperMap() {
		return commitDeveloperMap;
	}

	public void setCommitDeveloperMap(Map<List<String>, Double> commitDeveloperMap) {
		this.commitDeveloperMap = commitDeveloperMap;
	}

	public Double getMaximumRatio() {
		return maximumRatio;
	}

	public void setMaximumRatio(Double maximumRatio) {
		this.maximumRatio = maximumRatio;
	}

	public Map<List<String>, Double> getCommitHealthMetricMap() {
		return commitHealthMetricMap;
	}

	public void setCommitHealthMetricMap(Map<List<String>, Double> commitHealthMetricMap) {
		this.commitHealthMetricMap = commitHealthMetricMap;
	}
	
	public Map<List<String>, HashSet<String>> getDeveloperMap() {
		return developerMap;
	}

	public void setDeveloperMap(Map<List<String>, HashSet<String>> developerMap) {
		this.developerMap = developerMap;
	}

	public String getHealthByOrgAndRepoName(String org, String repoName) {
		String result = null;
		String[] parameters = {org, repoName};
		List<String> key = Arrays.asList(parameters);
		
		if (commitDeveloperMap.containsKey(key)) {
			Double temp = commitDeveloperMap.get(key) / (developerMap.get(key).size()*1.0);
			result = org + ", " + repoName + ", " + temp + ", " + maximumRatio + ", " + commitHealthMetricMap.get(key);
		}
		return result;
	}
	
	public String getHealthByOrgAndRepoName(List<String> key) {
		String result = null;
		
		if (commitDeveloperMap.containsKey(key)) {
			Double temp = commitDeveloperMap.get(key) / (developerMap.get(key).size()*1.0);
			result = key.get(0) + ", " + key.get(1) + ", " + temp + ", " + maximumRatio + ", " + commitHealthMetricMap.get(key);
		}
		return result;
	}
	
	public List<Object> getHealthByOrgAndRepoNameReturnList(List<String> key) {
		List<Object> result = null;
		
		if (commitDeveloperMap.containsKey(key)) {
			Double temp = commitDeveloperMap.get(key) / (developerMap.get(key).size()*1.0);
			result = new ArrayList<>();
			result.add(temp); // the ratio of this project
			result.add(commitHealthMetricMap.get(key)); // the actual health for this metric
		} 
		return result;
	}

	public List<String> getResult() {
		List<String> result = new ArrayList<String>();
		commitDeveloperMap.forEach((key, value) -> {
			result.add(this.getHealthByOrgAndRepoName(key));
		});
		return result;
	}
}
