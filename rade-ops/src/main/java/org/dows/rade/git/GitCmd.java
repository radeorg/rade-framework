package org.dows.rade.git;

import org.eclipse.jgit.api.Git;

import java.util.List;

public interface GitCmd<T extends CmdParam> {

    default List<Integer> value() {
        throw new UnsupportedOperationException();
    }

    void execute(Git git, T param);
}
