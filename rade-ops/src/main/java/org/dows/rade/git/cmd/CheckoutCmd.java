package org.dows.rade.git.cmd;

import org.dows.rade.git.GitCmd;
import org.dows.rade.git.GitUtil;
import org.dows.rade.git.dto.BaseCmdParam;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckoutCmd implements GitCmd<BaseCmdParam> {
    // all:0,commit:1,pull:2,push:3,checkout:4,pull:5,merge:6,push:7,checkout:8
    @Override
    public List<Integer> value() {
        return List.of(4, 8);
    }

    @Override
    public void execute(Git git, BaseCmdParam param) {
        try {
            int index = param.getIndex();
            if (index == 4) {
                GitUtil.gitCheckout(git,param.getTargetBranch());
            } else if (index == 8) {
                GitUtil.gitCheckout(git,param.getSourceBranch());
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }




}
