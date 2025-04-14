package org.dows.rade.git;

import org.dows.rade.git.dto.BaseCmdParam;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GitProcessor {

    /**
     * @param rootDir
     * @param commond all:0; commit:1,pull:2,push:3,checkout:4,pull:5,merge:6,push:7,checkout:8
     * @param index   跳过文件 12 1234  567
     */
    public static void processProjects(File rootDir, int commond, int... index) {
        SshFactory.init(null);
        List<ProjectInfo> projects = GitProjectUtil.findGitProjectsWithNumberedFiles(rootDir);
        List<ProjectInfo> runProjects = new ArrayList<>();
        int[] array = Arrays.stream(index).sorted().distinct().toArray();
        for (int i : array) {
            for (ProjectInfo project : projects) {
                if(project.getOrder() == i){
                    runProjects.add(project);
                    break;
                }
            }
        }
        if (runProjects.isEmpty()) {
            System.out.println("未找到任何包含数字.txt文件的Git项目");
            return;
        }
        System.out.println("找到 " + projects.size() + " 个项目需要处理:");
        runProjects.forEach(p -> System.out.println(p.getOrder() + ". " + p.getProjectDir().getName() +
                " (等待: " + p.getDelaySeconds() + "秒)"));

        for (ProjectInfo project : runProjects) {
            System.out.println("\n=====================================");
            System.out.println("正在处理项目[" + project.getOrder() + "]: " + project.getProjectDir().getName());
            System.out.println("等待时间: " + project.getDelaySeconds() + " 秒");
            System.out.println("=====================================");

            try (Repository repository = getRepository(project.getProjectDir())) {
                if (repository == null) {
                    System.out.println("错误: 不是有效的Git仓库 - " + project.getProjectDir().getAbsolutePath());
                    continue;
                }
                processGitRepository(new Git(repository), commond);
            } catch (Exception e) {
                System.out.println("处理项目 " + project.getProjectDir().getName() + " 时出错: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("操作完成，等待 " + project.getDelaySeconds() + " 秒后继续...");
            try {
                TimeUnit.SECONDS.sleep(project.getDelaySeconds());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("等待被中断");
                break;
            }
        }
        System.out.println("\n所有项目处理完成!");
    }


    private static void processGitRepository(Git git, int commond) throws GitAPIException, IOException {
        String currentBranch = git.getRepository().getBranch();
        System.out.println("当前分支: " + currentBranch);

        if ("master".equalsIgnoreCase(currentBranch) || "main".equalsIgnoreCase(currentBranch)) {
            System.out.println("跳过 master/main 分支");
            return;
        }

        String targetBranch = determineTargetBranch(currentBranch);
        if (targetBranch == null) {
            System.out.println("无法识别的分支格式: " + currentBranch);
            return;
        }

        if (!GitUtil.localBranchExists(git, targetBranch)) {
            System.out.println("目标分支不存在: " + targetBranch);
            return;
        }

        int[] intArray = null;
        if (commond > 0) {
            String numberStr = String.valueOf(commond);
            char[] charArray = numberStr.toCharArray();
            intArray = new int[charArray.length];

            for (int i = 0; i < charArray.length; i++) {
                intArray[i] = Character.getNumericValue(charArray[i]);
            }

        } else if (commond == 0) {
            intArray = GitCmdFactory.ALL_CMD;
        }
        if (intArray == null) {
            System.out.println("未指定操作命令");
            return;
        }
        BaseCmdParam baseCmdParam = new BaseCmdParam();
        baseCmdParam.setSourceBranch(currentBranch);
        baseCmdParam.setTargetBranch(targetBranch);
        //commit:1,pull:2,push:3,checkout:4,pull:5,merge:6,push:7,checkout:8
        for (int i : intArray) {
            GitCmd<BaseCmdParam> gitCmd = GitCmdFactory.getGitCmd(i);
            baseCmdParam.setIndex(i);
            gitCmd.execute(git, baseCmdParam);
        }
        // all:0,commit:1,pull:2,push:3,checkout:4,pull:5,merge:6,push:7,checkout:8
        /*// 1. 执行git commit
        GitUtil.gitCommit(git);
        // 2. 执行git pull
        GitUtil.gitPull(git);
        // 3. 执行git push
        GitUtil.gitPush(git);
        // 4. 执行git checkout
        GitUtil.gitCheckout(git, targetBranch);
        // 5. 执行git pull
        GitUtil.gitPull(git);
        // 6. 执行git merge
        GitUtil.gitMerge(git, currentBranch, targetBranch);
        // 7. 执行git push
        GitUtil.gitPush(git, targetBranch);
        // 8. 切换回原始分支
        GitUtil.gitCheckout(git, currentBranch);*/
    }

    private static String determineTargetBranch(String currentBranch) {
        if (currentBranch.startsWith("sit-")) {
            return "dev-" + currentBranch.substring(4);
        } else if (currentBranch.startsWith("dev-")) {
            return "sit-" + currentBranch.substring(4);
        }
        return null;
    }

    private static Repository getRepository(File projectDir) throws IOException {
        return new FileRepositoryBuilder()
                .setGitDir(new File(projectDir, ".git"))
                .readEnvironment()
                .findGitDir()
                .build();
    }
}