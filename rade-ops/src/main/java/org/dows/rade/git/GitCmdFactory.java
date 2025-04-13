package org.dows.rade.git;

import org.dows.rade.git.cmd.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitCmdFactory {

    private final static Map<Integer, GitCmd> gitCmdMap = new HashMap<>();

    //all:0,commit:1,pull:2,push:3,checkout:4,pull:5,merge:6,push:7,checkout:8
    public final static int[] ALL_CMD = new int[]{1, 2, 3, 4, 5, 6, 7, 8};

    private static void init() {
        CheckoutCmd checkoutCmd = new CheckoutCmd();
        CommitCmd commitCmd = new CommitCmd();
        MergeCmd mergeCmd = new MergeCmd();
        PullCmd pullCmd = new PullCmd();
        PushCmd pushCmd = new PushCmd();
        List<GitCmd<?>> gitCmds = List.of(checkoutCmd, commitCmd, mergeCmd, pullCmd, pushCmd);

        gitCmds.forEach(cmd -> {
            List<Integer> value = cmd.value();
            for (Integer v : value) {
                gitCmdMap.put(v, cmd);
            }
        });
    }

    public static GitCmd getGitCmd(Integer cmd) {
        if (gitCmdMap.isEmpty()) {
            init();
        }
        return gitCmdMap.get(cmd);
    }
}
