package utils;

import metrics.HealthReport;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FileHandling {
	private static final String BASE_PATH = System.getProperty("user.dir");
	private static final String GH_ARCHIVE_LINK = "https://data.gharchive.org/";

	public static final String DATA_PATH = BASE_PATH + "/data/";
	public static final String SQLITE_DB_PATH = DATA_PATH + "gharchive.db";

	public static final String OUTPUT_PATH = BASE_PATH + "/output/";
	public static final String HEALTH_SCORE_OUTPUT_PATH = OUTPUT_PATH + "health_scores.csv";

	public static void initDirectory() {

	}

	public static void downloadAndDecompress(ArrayList<String> hourList) {
		new File(DATA_PATH).mkdirs();
		new File(OUTPUT_PATH).mkdirs();
		download(hourList);
		decompress(hourList);
	}

	private static void decompress(ArrayList<String> hourList) {
		for (String hour : hourList) {
			try {
				String zipFile = DATA_PATH + hour + ".json.gz";
				String jsonFile = DATA_PATH + hour + ".json";
				System.out.println("Start extracting file " + zipFile);
				decompressGzip(new File(zipFile), new File(jsonFile));
				System.out.println("Complete extracting file " + zipFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void download(ArrayList<String> hourList) {
		deleteFiles(DATA_PATH, new String[] { "gz", "json" });
		for (String hour : hourList) {
			try {
				String linkDownload = GH_ARCHIVE_LINK + hour + ".json.gz";
				System.out.println("Start downloading file " + linkDownload);
				downloadWithJavaIO(linkDownload, DATA_PATH + hour + ".json.gz");
				System.out.println("Complete downloading file " + linkDownload);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void downloadWithJavaIO(String url, String localFilename) {
		System.setProperty("http.agent", "Chrome");
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(localFilename)) {

			byte[] dataBuffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void decompressGzip(File input, File output) throws IOException {
		try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(input))) {
			try (FileOutputStream out = new FileOutputStream(output)) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
			}
		}
	}

	public static void extractCsv(List<HealthReport> report) throws IOException {
		FileWriter writer = new FileWriter(HEALTH_SCORE_OUTPUT_PATH);
		writer.write("org, repoName, healthMetric, numberOfCommit, metricForCommitCount, averageDurationForOpenedIssue, "
				+ "metricForOpenedIssue, averageDurationForMergedPullRequest, metricForMergedPullRequest, developerRatio, "
				+ "metricForDeveloperRatio\n");
		for (HealthReport r : report) {
			writer.write(r.toString());
			writer.write("\n"); // newline
		}

		writer.close();
	}

	private static void deleteFiles(String path, String[] extensions) {
		try {
			for (String extension : extensions) {
				Arrays.stream(Objects.requireNonNull(new File(path).listFiles((f, p) -> p.endsWith(extension))))
						.forEach(File::delete);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
