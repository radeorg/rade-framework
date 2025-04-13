package org.dows.rade.git;

import lombok.Data;

import java.io.File;

@Data
public class ProjectInfo {
    private final int order;
    private final int delaySeconds;
    private final File projectDir;

    public ProjectInfo(int order, int delaySeconds, File projectDir) {
        this.order = order;
        this.delaySeconds = delaySeconds;
        this.projectDir = projectDir;
    }
}