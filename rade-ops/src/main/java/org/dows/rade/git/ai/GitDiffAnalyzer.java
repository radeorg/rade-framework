package org.dows.rade.git.ai;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class GitDiffAnalyzer {

    // 获取最新提交的差异
    public static String getLatestDiff(String repoPath) throws Exception {
        try (Git git = Git.open(new File(repoPath));
             Repository repo = git.getRepository()) {

            RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
            RevCommit prevCommit = git.log().setMaxCount(2).call().iterator().next();

            ObjectReader reader = repo.newObjectReader();
            CanonicalTreeParser oldTree = new CanonicalTreeParser();
            oldTree.reset(reader, prevCommit.getTree());
            CanonicalTreeParser newTree = new CanonicalTreeParser();
            newTree.reset(reader, latestCommit.getTree());

            List<DiffEntry> diffs = git.diff()
                    .setOldTree(oldTree)
                    .setNewTree(newTree)
                    .call();

            StringBuilder diffContent = new StringBuilder();
            for (DiffEntry entry : diffs) {
                diffContent.append("File: ").append(entry.getNewPath()).append("\n");
                //diffContent.append(DiffFormatter.toDiffHeader(entry));
                // 获取完整差异内容
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    DiffFormatter formatter = new DiffFormatter(out);
                    formatter.setRepository(repo);
                    formatter.format(entry);
                    diffContent.append(formatter);
                    diffContent.append(out);
                }
            }
            return diffContent.toString();
        }
    }
}