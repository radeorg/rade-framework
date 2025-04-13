package org.dows.rade.git.cmd;

import org.dows.rade.git.GitCmd;
import org.dows.rade.git.GitUtil;
import org.dows.rade.git.dto.BaseCmdParam;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CommitCmd implements GitCmd<BaseCmdParam> {
    // all:0,commit:1,pull:2,push:3,checkout:4,pull:5,merge:6,push:7,checkout:8
    @Override
    public List<Integer> value() {
        return List.of(1);
    }

    @Override
    public void execute(Git git, BaseCmdParam param) {
        try {
            GitUtil.gitCommit(git);
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
