import org.dows.rade.git.GitProcessor;

import java.io.File;

public class TestGit {

    public static void main(String[] args) {
        File projectDir = new File("D:/workspaces/java/projects/rade");
        //processProjects(projectDir, 1234, 1, 2);
        GitProcessor.processProjects(projectDir, 0,1);
    }
}
