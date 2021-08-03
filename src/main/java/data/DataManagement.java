/**
 * 
 */
package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;

import models.pullrequest.PayloadPrModel;
import utils.FileHandling;
import models.issue.PayLoadIssueModel;
import metrics.CommitCount;
import metrics.CommitDeveloperRatio;
import metrics.HealthReport;
import metrics.MergedPullRequest;
import metrics.OpenedIssue;
import models.FactModel;

import static utils.FileHandling.DATA_PATH;

/**
 * @author Quan Kien Minh
 *
 */
public class DataManagement {
	private static CommitCount commitCount;
	private static OpenedIssue openedIssue;
	private static MergedPullRequest mergedPullRequest;
	private static CommitDeveloperRatio commitDeveloperRatio;
	private static List<HealthReport> report;
	private static HashSet<List<String>> checkExist;
	
	public static void init(Double numberOfDays, Long toDateTick) {
		commitCount = new CommitCount(numberOfDays);
		openedIssue = new OpenedIssue(toDateTick);
		mergedPullRequest = new MergedPullRequest();
		commitDeveloperRatio = new CommitDeveloperRatio();
		report = new ArrayList<>();
		checkExist = new HashSet<>();
	}
	
	public static void calculateHealthForCommitCount() {
		commitCount.calculateHealthByMetric();
	}
	
	public static void calculateHealthForOpenedIsse() {
		openedIssue.calculatHealthMetric();
	}
	
	public static void calculateHealthForMergedPullRequest() {
		mergedPullRequest.calculatHealthMetric();
	}
	
	public static void calculateHealthForCommitDeveloperRatio() {
		commitDeveloperRatio.calculateHealthByMetric();
	}
	
	public static List<String> getResultOfCommitCount() {
		return commitCount.getResult();
	}
	
	public static List<String> getResultOfOpenedIssue() {
		return openedIssue.getResult();
	}
	
	public static List<String> getResultOfMergedPullRequest() {
		return mergedPullRequest.getResult();
	}
	
	public static List<String> getResultOfCommitDeveloperRatio() {
		return commitDeveloperRatio.getResult();
	}

	public static void exportFinalResult() throws IOException {
		for (HealthReport r : report) {
			String[] key = { r.getOrg(), r.getRepoName() };
			List<Object> commitCountResult = commitCount.getHealthByOrgAndRepoNameReturnList(Arrays.asList(key));
			List<Object> openedIssueResult = openedIssue.getHealthByOrgAndRepoNameReturnList(Arrays.asList(key));
			List<Object> mergedPullRequestResult = mergedPullRequest.getHealthByOrgAndRepoNameReturnList(Arrays.asList(key));
			List<Object> commitDeveloperRatioResult = commitDeveloperRatio.getHealthByOrgAndRepoNameReturnList(Arrays.asList(key));
			
			if (commitCountResult != null) {
				r.setNumberOfCommit((Double) commitCountResult.get(0));
				r.setMaximumNumberOfCommit((Double) commitCountResult.get(1));
				r.addToHealthMetric((Double) commitCountResult.get(2)*0.25);
			}
			if (openedIssueResult != null) {
				r.setAverageDurationForOpenedIssue((Long) openedIssueResult.get(0));
				r.setMinDurationForOpenedIssue((Long) openedIssueResult.get(1));
				r.addToHealthMetric((Double) openedIssueResult.get(2)*0.25);
			}
			if (mergedPullRequestResult != null) {
				r.setAverageDurationForMergedPullRequest((Long) mergedPullRequestResult.get(0));
				r.setMinDurationForMergedPullRequest((Long) mergedPullRequestResult.get(1));
				r.addToHealthMetric((Double) mergedPullRequestResult.get(2)*0.25);
			}
			if (commitDeveloperRatioResult != null) {
				r.setDeveloperRatio((Double) commitDeveloperRatioResult.get(0));
				r.setMaximumDeveloperRatio((Double) commitDeveloperRatioResult.get(1));
				r.addToHealthMetric((Double) commitDeveloperRatioResult.get(2)*0.25);
			}
		}
		report.sort(Comparator.comparing(HealthReport::getHealthMetric).reversed());
		int maxSize = report.size() > 1000 ? 1000 : report.size();
		FileHandling.extractCsv(report.subList(0, maxSize));
	}
	
	public static void addToReport(String org, String repoName) {
		String[] key = { org, repoName };
		if (!checkExist.contains(Arrays.asList(key))) {
			report.add(new HealthReport(org, repoName));
			checkExist.add(Arrays.asList(key));
		}
	}
	
	public static void parseJsonToFactMap(ArrayList<String> hourList) {
		for (String hour: hourList) {
			String jsonPath = DATA_PATH + hour + ".json";
			System.out.println(jsonPath);
			
			try {
				FileReader fileReader = new FileReader(jsonPath);
		        BufferedReader bufferedReader = new BufferedReader(fileReader);
		        String jsonLine;
		        
		        while ((jsonLine = bufferedReader.readLine()) != null) {
		        	FactModel fact = new Gson().fromJson(jsonLine, FactModel.class);
		        	String org = fact.getRepo().getName().split("/")[0];
		            String repoName = fact.getRepo().getName().split("/")[1];
		            String actor = fact.getActor().getLogin();
		            Timestamp createdAt = fact.getCreated_at();

		        	if (fact.getType().compareTo("PushEvent") == 0) {
		        		commitCount.putIntoCommitCount(org, repoName);
		        		commitDeveloperRatio.putIntoCommitDeveloper(org, repoName, actor);
		        		addToReport(org, repoName);
		        	} else if (fact.getType().compareTo("IssuesEvent") == 0) {
		        		PayLoadIssueModel issue = new Gson().fromJson(jsonLine, PayLoadIssueModel.class);
		        		String payLoadNumber = issue.getPayload().getIssue().getNumber().toString();
		        		String payLoadAction = issue.getPayload().getAction();
		        		openedIssue.putIntoOpenedIssue(org, repoName, payLoadNumber, payLoadAction, createdAt);
		        		addToReport(org, repoName);
		        	} else if (fact.getType().compareTo("PullRequestEvent") == 0) {
		        		PayloadPrModel pr = new Gson().fromJson(jsonLine, PayloadPrModel.class);
		        		String payLoadNumber = pr.getPayload().getNumber().toString();
		        		String payLoadAction = pr.getPayload().getAction();
		        		Boolean isMerged = pr.getPayload().getPullRequest().getMerged();
		        		Timestamp mergedAt = pr.getPayload().getPullRequest().getMerged_at();
		        		Timestamp openedAt = pr.getPayload().getPullRequest().getCreated_at();
		        		mergedPullRequest.putIntomergedPullRequest(org, repoName, payLoadNumber, payLoadAction, openedAt, isMerged, mergedAt);
		        		addToReport(org, repoName);
		        	}
		        }
		        bufferedReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
