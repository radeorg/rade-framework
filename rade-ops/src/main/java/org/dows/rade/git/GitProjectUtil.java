package org.dows.rade.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitProjectUtil {
    private static final Pattern NUMBERED_FILE_PATTERN = Pattern.compile("^\\d+\\.txt$");
    private static final int DEFAULT_DELAY_SECONDS = 200;

    public static List<ProjectInfo> findGitProjectsWithNumberedFiles(File rootDir) {
        try {
            return Files.walk(rootDir.toPath(), 1)
                    .filter(path -> {
                        File dir = path.toFile();
                        return dir.isDirectory() && new File(dir, ".git").exists();
                    })
                    .flatMap(projectDir -> {
                        try {
                            return Files.list(projectDir)
                                    .filter(file -> NUMBERED_FILE_PATTERN.matcher(file.getFileName().toString()).matches())
                                    .map(file -> {
                                        int order = Integer.parseInt(file.getFileName().toString().replace(".txt", ""));
                                        int delay = readDelayTime(file.toFile());
                                        return new ProjectInfo(order, delay, projectDir.toFile());
                                    });
                        } catch (IOException e) {
                            return Stream.empty();
                        }
                    })
                    .sorted(Comparator.comparingInt(ProjectInfo::getOrder))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("读取目录失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static int readDelayTime(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            System.out.println("读取等待时间失败，使用默认值 " + DEFAULT_DELAY_SECONDS + " 秒");
        }
        return DEFAULT_DELAY_SECONDS;
    }
}
