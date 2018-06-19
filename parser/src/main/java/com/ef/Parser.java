package com.ef;

import com.ef.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Parser {

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

    private void loadFile() throws IOException, NullPointerException {
        InputStream inputStream = null;
        Scanner scanner = null;
        try {
            inputStream = getClass().getResourceAsStream("assets/access.log");
            System.out.println(Utils.getBytes(inputStream).length);
            scanner = new Scanner(inputStream);

            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }

            int count = 0;

            while (scanner.hasNextLine() && count < 6) {
                String line = scanner.nextLine();
                System.out.println(line);
                count++;
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Map opts = Parser.getOpts(args);
            Parser parser = new Parser();
            parser.loadFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
