package metrics;

/**
 * @author Quan Kien Minh
 *
 */
public class HealthReport {
	// This is a class to summarize all information (metrics) of a github project
	private String org;
	private String repoName;
	private Double healthMetric;
	
	private Double numberOfCommit;
	private Double maximumNumberOfCommit;
	private Double metricForCommitCount;
	
	private Long averageDurationForOpenedIssue;
	private Long minDurationForOpenedIssue;
	private Double metricForOpenedIssue;
	
	private Long averageDurationForMergedPullRequest;
	private Long minDurationForMergedPullRequest;
	private Double metricForMergedPullRequest; 
	
	private Double developerRatio;
	private Double maximumDeveloperRatio;
	private Double metricForDeveloperRatio;
	
	public HealthReport(String org, String repoName) {
		this.org = org;
		this.repoName = repoName;
		this.healthMetric = 0.0;
	}
	
	public void addToHealthMetric(Double a) {
		this.healthMetric += a;
	}

	public String getOrg() {
		return org;
	}
	
	public void setOrg(String org) {
		this.org = org;
	}
	
	public String getRepoName() {
		return repoName;
	}
	
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	
	public Double getHealthMetric() {
		return healthMetric;
	}
	
	public void setHealthMetric(Double healthMetric) {
		this.healthMetric = healthMetric;
	}
	
	public Double getNumberOfCommit() {
		return numberOfCommit;
	}
	
	public void setNumberOfCommit(Double numberOfCommit) {
		this.numberOfCommit = numberOfCommit;
	}
	
	public Double getMaximumNumberOfCommit() {
		return maximumNumberOfCommit;
	}
	
	public void setMaximumNumberOfCommit(Double maximumNumberOfCommit) {
		this.maximumNumberOfCommit = maximumNumberOfCommit;
	}
	
	public Double getMetricForCommitCount() {
		return metricForCommitCount;
	}
	
	public void setMetricForCommitCount(Double metricForCommitCount) {
		this.metricForCommitCount = metricForCommitCount;
	}
	
	public Long getAverageDurationForOpenedIssue() {
		return averageDurationForOpenedIssue;
	}
	
	public void setAverageDurationForOpenedIssue(Long averageDurationForOpenedIssue) {
		this.averageDurationForOpenedIssue = averageDurationForOpenedIssue;
	}
	
	public Long getMinDurationForOpenedIssue() {
		return minDurationForOpenedIssue;
	}
	
	public void setMinDurationForOpenedIssue(Long minDurationForOpenedIssue) {
		this.minDurationForOpenedIssue = minDurationForOpenedIssue;
	}
	
	public Double getMetricForOpenedIssue() {
		return metricForOpenedIssue;
	}
	
	public void setMetricForOpenedIssue(Double metricForOpenedIssue) {
		this.metricForOpenedIssue = metricForOpenedIssue;
	}
	
	public Long getAverageDurationForMergedPullRequest() {
		return averageDurationForMergedPullRequest;
	}
	
	public void setAverageDurationForMergedPullRequest(Long averageDurationForMergedPullRequest) {
		this.averageDurationForMergedPullRequest = averageDurationForMergedPullRequest;
	}
	
	public Long getMinDurationForMergedPullRequest() {
		return minDurationForMergedPullRequest;
	}
	
	public void setMinDurationForMergedPullRequest(Long minDurationForMergedPullRequest) {
		this.minDurationForMergedPullRequest = minDurationForMergedPullRequest;
	}
	
	public Double getMetricForMergedPullRequest() {
		return metricForMergedPullRequest;
	}
	
	public void setMetricForMergedPullRequest(Double metricForMergedPullRequest) {
		this.metricForMergedPullRequest = metricForMergedPullRequest;
	}
	
	public Double getDeveloperRatio() {
		return developerRatio;
	}
	
	public void setDeveloperRatio(Double developerRatio) {
		this.developerRatio = developerRatio;
	}
	
	public Double getMaximumDeveloperRatio() {
		return maximumDeveloperRatio;
	}
	
	public void setMaximumDeveloperRatio(Double maximumDeveloperRatio) {
		this.maximumDeveloperRatio = maximumDeveloperRatio;
	}
	
	public Double getMetricForDeveloperRatio() {
		return metricForDeveloperRatio;
	}
	
	public void setMetricForDeveloperRatio(Double metricForDeveloperRatio) {
		this.metricForDeveloperRatio = metricForDeveloperRatio;
	}

	@Override
	public String toString() {
		return org + ", " + repoName + ", " + healthMetric
				+ ", " + numberOfCommit + ", " + maximumNumberOfCommit
				+ ", " + metricForCommitCount + ", " + averageDurationForOpenedIssue + ", " + minDurationForOpenedIssue
				+ ", " + metricForOpenedIssue + ", "
				+ averageDurationForMergedPullRequest + ", "
				+ minDurationForMergedPullRequest + ", " + metricForMergedPullRequest
				+ ", " + developerRatio + ", " + maximumDeveloperRatio
				+ ", " + metricForDeveloperRatio;
	}
}
