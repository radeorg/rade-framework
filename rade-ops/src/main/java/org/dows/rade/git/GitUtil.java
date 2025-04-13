package org.dows.rade.git;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import java.io.IOException;

public class GitUtil {

    public static void gitPull(Git git) throws GitAPIException, IOException {
        // 执行 git pull
        PullCommand pullCommand = git.pull();
        PullResult pullResult = pullCommand.call();
        if (pullResult.isSuccessful()) {
            System.out.printf("拉取成功，当前分支: %s%n", git.getRepository().getBranch());
        } else {
            System.err.println("拉取失败: " + pullResult.getMergeResult().getMergeStatus());
        }
    }


    public static void gitCommit(Git git) throws GitAPIException, IOException {
        Status status = git.status().call();
        if (!status.getAdded().isEmpty() || !status.getChanged().isEmpty() || !status.getModified().isEmpty()) {
            System.out.println("提交当前更改...");
            git.add().addFilepattern(".").call();
            git.commit().setMessage("自动提交: 合并前的更改").call();
        } else {
            System.out.println("没有需要提交的更改,或已提交...");
        }
    }


    public static void gitMerge(Git git, String sourceBranch, String targetBranch) throws GitAPIException {
        System.out.println("切换到目标分支 " + targetBranch + "...");
        //gitCheckout(git, targetBranch);
        // 检查远程分支是否存在
        boolean remoteBranchExists = remoteBranchExists(git, targetBranch);
        if (!remoteBranchExists) {
            System.out.println("远程分支 " + targetBranch + " 不存在，跳过 pull 操作");
        } else {
            // 执行 git pull
            PullCommand pullCommand = git.pull();
            PullResult pullResult = pullCommand.call();
            if (pullResult.isSuccessful()) {
                System.out.println("拉取成功");
            } else {
                System.err.println("拉取失败: " + pullResult.getMergeResult().getMergeStatus());
            }
        }

        System.out.println("合并 " + sourceBranch + " 到 " + targetBranch + "...");
        try {
            MergeResult mergeResult = git.merge()
                    .include(git.getRepository().findRef("refs/heads/" + sourceBranch))
                    .setCommit(true)
                    .setMessage("自动合并: 从 " + sourceBranch + " 合并到 " + targetBranch)
                    .call();

            if (mergeResult.getMergeStatus().isSuccessful()) {
                System.out.println("合并成功，可以推送更改...");
                //pushToGitHub(git, targetBranch);
            } else {
                System.out.println("合并失败，状态: " + mergeResult.getMergeStatus());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void gitCheckout(Git git, String branchName) throws GitAPIException {
        git.checkout()
                .setName(branchName)
                .call();
        System.out.println("切换到分支 " + branchName + "...");
    }


    public static void gitPush(Git git, String branchName) throws GitAPIException {
        // 执行 git push
        PushCommand pushCommand = git.push()
                .setRemote("origin")
                //.setRefSpecs(new RefSpec(String.format("refs/heads/%s:refs/heads/%s", branchName, branchName)))
                .add(branchName)
                .setForce(false); // 谨慎使用强制推送
        // 如果使用 HTTPS 而非 SSH，需要设置凭据
        //pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(GITHUB_USERNAME, GITHUB_PASSWORD));
        try {
            Iterable<PushResult> results = pushCommand.call();
            for (PushResult result : results) {
                for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                    System.out.println("推送状态: " + update.getStatus());
                    if (update.getStatus() != RemoteRefUpdate.Status.OK) {
                        System.out.println("推送失败原因: " + update.getMessage());
                    }
                }
            }
            System.out.println("推送成功完成");
        } catch (Exception e) {
            System.out.println("推送失败: " + e.getMessage());
            throw e;
        }
    }

    public static boolean remoteBranchExists(Git git, String branchName) throws GitAPIException {
        return git.lsRemote().call().stream()
                .anyMatch(ref -> ref.getName().equals("refs/heads/" + branchName));
    }

    public static boolean localBranchExists(Git git, String branchName) throws GitAPIException {
        return git.branchList().call().stream()
                .anyMatch(ref -> ref.getName().equals("refs/heads/" + branchName));
    }
}
