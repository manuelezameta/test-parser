package com.ef;

import com.ef.domain.IpBlockLine;
import com.ef.domain.LogLine;
import com.ef.persistence.ParserPersistence;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Parser {

    private static final String DURATION_ENTER_IS_NOT_SUPPORTED = "Duration enter is not supported";
    private static final String INITIAL_DATE_SHOULD_NOT_BE_NULL = "Initial date shouldn't be empty";
    private static final String THRESHOLD_SHOULD_NOT_BE_NULL = "Threshold shouldn't be empty";
    private final static String START_DATE = "startDate";
    private final static String DURATION = "duration";
    private final static String THRESHOLD = "threshold";

    private static Map<String, Object> getOpts(String[] optsToParse) {
        Map<String, Object> optsMap = new HashMap<>();

        for (int i = 0; i < optsToParse.length; i++) {
            switch (optsToParse[i].charAt(0)) {
                case '-':
                    if (optsToParse[i].length() < 2)
                        throw new IllegalArgumentException("Not a valid argument: " + optsToParse[i]);
                    if (optsToParse[i].charAt(1) == '-') {
                        if (optsToParse[i].length() < 3) {
                            throw new IllegalArgumentException("Not a valid argument: " + optsToParse[i]);
                        }
                        int equalsPos = optsToParse[i].indexOf("=");
                        optsMap.put(optsToParse[i].substring(2, equalsPos), optsToParse[i].substring(equalsPos + 1, optsToParse[i].length()));
                    }
                    break;
                default:
                    break;
            }
        }


        return optsMap;
    }

    private void processFile(LocalDateTime dateTimeFrom,
                             LocalDateTime dateTimeTo,
                             int threshold) throws IOException, NullPointerException {
        InputStream inputStream = null;
        Scanner scanner = null;
        ParserPersistence parserPersistence = new ParserPersistence();

        try {
            inputStream = getClass().getResourceAsStream("assets/access.log");
            scanner = new Scanner(inputStream);
            Map<String, Integer> ipProcessed = new HashMap<>();
            List<LogLine> logLines = new ArrayList<>();
            List<IpBlockLine> ipBlockLines = new ArrayList<>();

            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split("\\|");
                String logDate = data[0];
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                LocalDateTime dateTimeInput = LocalDateTime.parse(logDate, formatter);

                if (isBetweenDates(dateTimeFrom, dateTimeTo, dateTimeInput)) {
                    Integer counter = ipProcessed.get(data[1]);

                    if (counter != null) {
                        ipProcessed.put(data[1], counter + 1);
                    } else {
                        ipProcessed.put(data[1], 1);
                    }

                    LogLine logLine = new LogLine();
                    logLine.setDate(dateTimeInput);
                    logLine.setIp(data[1]);
                    logLine.setRequest(data[2]);
                    logLine.setStatus(Integer.valueOf(data[3]));
                    logLine.setUserAgent(data[4]);

                    logLines.add(logLine);
                }
            }

            // save logs into database
            parserPersistence.saveLogInDb(logLines);

            ipProcessed.forEach((k, v) -> {
                if (v >= threshold) {
                    String blockMessage = k + " is blocked to have more than " + threshold + " requests in " +
                            Duration.between(dateTimeFrom, dateTimeTo).toHours() + " hour(s).";

                    IpBlockLine ipBlockLine = new IpBlockLine();
                    ipBlockLine.setIp(k);
                    ipBlockLine.setBlockMessage(blockMessage);
                    ipBlockLines.add(ipBlockLine);

                    System.out.println(blockMessage);
                }
            });

            // save block message into database
            parserPersistence.saveIpBlockInDb(ipBlockLines);

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static LocalDateTime getInitialDate(String dateFromStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateFromStr.replace(".", " "), formatter);
    }

    private static LocalDateTime getFinalDate(String duration,
                                              LocalDateTime initialDate) throws Exception {
        switch (duration) {
            case "hourly":
                return initialDate.plusHours(1);
            case "daily":
                return initialDate.plusDays(1);
            default:
                throw new Exception(DURATION_ENTER_IS_NOT_SUPPORTED);

        }
    }

    private static boolean isBetweenDates(LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, LocalDateTime dateTimeInput) {
        if ((dateTimeInput.isAfter(dateTimeFrom) && dateTimeInput.isBefore(dateTimeTo)) ||
                (dateTimeInput.isEqual(dateTimeFrom) || dateTimeInput.isEqual(dateTimeTo))) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            // Parsing the parameters
            Map opts = Parser.getOpts(args);
            String dateFromStr = (String) opts.get(START_DATE);
            String duration = (String) opts.get(DURATION);
            String threshold = (String) opts.get(THRESHOLD);

            if (dateFromStr != null) {
                // parsing initial date
                LocalDateTime dateTimeFrom = getInitialDate(dateFromStr);
                // getting final date according to the duration enter
                LocalDateTime dateTimeTo = getFinalDate(duration, dateTimeFrom);

                if(threshold != null && !threshold.equals("")) {
                    Integer thresholdInt = Integer.valueOf(threshold);
                    Parser parser = new Parser();
                    parser.processFile(dateTimeFrom, dateTimeTo, thresholdInt);
                } else {
                    throw new Exception(THRESHOLD_SHOULD_NOT_BE_NULL);
                }

            } else {
                throw new Exception(INITIAL_DATE_SHOULD_NOT_BE_NULL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
