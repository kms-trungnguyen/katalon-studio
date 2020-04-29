package com.kms.katalon.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class ConsoleCommandExecutor {
    private ConsoleCommandExecutor() {
    }

    public static List<String> runConsoleCommandAndCollectResults(String[] command,
            Map<String, String> addtionalEnvironmentVariables) throws IOException, InterruptedException {
        return runConsoleCommandAndCollectResults(command, addtionalEnvironmentVariables, StringUtils.EMPTY, false);
    }

    public static List<String> runConsoleCommandAndCollectResults(String[] command,
            Map<String, String> addtionalEnvironmentVariables, boolean redirectErrorStream) throws IOException, InterruptedException {
        return runConsoleCommandAndCollectResults(command, addtionalEnvironmentVariables, StringUtils.EMPTY, redirectErrorStream);
    }

    public static List<String> runConsoleCommandAndCollectResults(String[] command,
            Map<String, String> addtionalEnvironmentVariables, String directory, boolean redirectErrorStream)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (StringUtils.isNotEmpty(directory)) {
            pb.directory(new File(directory));
        }
        Map<String, String> existingEnvironmentVariables = pb.environment();
        existingEnvironmentVariables.putAll(addtionalEnvironmentVariables);
        pb.redirectErrorStream(redirectErrorStream);

        Process process = pb.start();
        List<String> resultLines = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                resultLines.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        process.waitFor();

        return resultLines;
    }

    public static List<String> runConsoleCommandError(String[] command,
            Map<String, String> addtionalEnvironmentVariables, String directory) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (StringUtils.isNotEmpty(directory)) {
            pb.directory(new File(directory));
        }
        Map<String, String> existingEnvironmentVariables = pb.environment();
        existingEnvironmentVariables.putAll(addtionalEnvironmentVariables);

        Process process = pb.start();
        process.waitFor();
        List<String> resultLines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                resultLines.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return resultLines;
    }
    
    public static String runConsoleCommandAndCollectFirstResult(String[] command,
            Map<String, String> addtionalEnvironmentVariables,
            String directory) throws IOException, InterruptedException {
        List<String> resultLines = runConsoleCommandAndCollectResults(command, addtionalEnvironmentVariables,
                directory, false);
        if (!resultLines.isEmpty()) {
            return resultLines.get(0);
        }
        return "";
    }

    public static String runConsoleCommandAndCollectFirstResult(String[] command,
            Map<String, String> addtionalEnvironmentVariables) throws IOException, InterruptedException {
        return runConsoleCommandAndCollectFirstResult(command, addtionalEnvironmentVariables, StringUtils.EMPTY);
    }

    public static List<String> runConsoleCommandAndCollectResults(String[] command)
            throws IOException, InterruptedException {
        return runConsoleCommandAndCollectResults(command, new HashMap<String, String>());
    }

    public static String runConsoleCommandAndCollectFirstResult(String[] command)
            throws IOException, InterruptedException {
        List<String> resultLines = runConsoleCommandAndCollectResults(command);
        if (!resultLines.isEmpty()) {
            return resultLines.get(0);
        }
        return "";
    }
    
    public static String runConsoleCommandAndCollectFirstResult(String[] command, boolean redirectErrorStream)
            throws IOException, InterruptedException {
        List<String> resultLines = runConsoleCommandAndCollectResults(command, new HashMap<String, String>(), StringUtils.EMPTY, redirectErrorStream);
        if (!resultLines.isEmpty()) {
            return resultLines.get(0);
        }
        return "";
    }
}
