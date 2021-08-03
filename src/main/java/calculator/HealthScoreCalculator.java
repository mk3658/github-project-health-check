package calculator;

import data.*;
import utils.FileHandling;
import utils.TimeHandling;

import java.io.*;
import java.sql.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * @author Quan Kien Minh
 *
 */
public class HealthScoreCalculator {

    public static void main(String[] args) throws IOException, SQLException {
        System.out.println("Welcome to Health Calculator for Github Project!");
        Instant isDateFrom;
        Instant isDateTo;

        // Input arguments handling
        if (args.length == 0) {
            // Instant.now() gets current time in UTC
            Instant now = Instant.now();
            isDateFrom = now.plus(-2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
            isDateTo = now.plus(-1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);

        } else if (args.length == 2) {
            // Validate input arguments
            String dateFrom = args[0];
            String dateTo = args[1];
            if (!TimeHandling.validateInputDate(dateFrom, dateTo)) return;

            isDateFrom = Instant.parse(dateFrom);
            isDateTo = Instant.parse(dateTo);

        } else {
            System.out.println("Please use \"gradle run\" to extract last one hour data.\n"
                    + "Otherwise, \"gradle run --args='dateFrom dateTo'\" to extract metric from dateFrom to dateTo.\n"
                    + "Note: Date must be in ISO 8601 format, eg. 2019-08-01T00:00:00Z.");
            return;
        }

        // Get hour list to build an array of file names to download
        ArrayList<String> hourList = TimeHandling.getHourNameList(isDateFrom, isDateTo);

        // Download and extract json by gz file named by hour (yyyy-MM-dd-H.json.gz)
        FileHandling.downloadAndDecompress(hourList);

        // Process metrics
        double numberOfDays = ((int) Math.ceil((Duration.between(isDateFrom, isDateTo).toMinutes() * 1.0 / 60))) * 1.0 / 24;
        long toDateTick = isDateTo.toEpochMilli();
        
        // Initialize all of the model for calculate the health
        DataManagement.init(numberOfDays, toDateTick);
        
        // Read the json file and store it into fact map
        DataManagement.parseJsonToFactMap(hourList);

        // Calculate health based on the metric for four criteria
        DataManagement.calculateHealthForCommitCount();
        DataManagement.calculateHealthForOpenedIsse();
        DataManagement.calculateHealthForMergedPullRequest();
        DataManagement.calculateHealthForCommitDeveloperRatio();

        // Extract health metric result
        DataManagement.exportFinalResult();

        System.out.println("Application was finished!");
    }

}
