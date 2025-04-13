package org.dows.rade.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SshFactory {

    // 配置您的 GitHub 凭据或 SSH 密钥信息
    private static final String SSH_PRIVATE_KEY_PATH = System.getProperty("user.home") + "/.ssh/id_ecdsa";
    private static final String SSH_PUBLIC_KEY_PATH = System.getProperty("user.home") + "/.ssh/id_ecdsa.pub";

    public static void init(String passphrase) {
        // 初始化 SSH 会话工厂
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                // 配置 SSH 参数
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch jsch = super.createDefaultJSch(fs);
                try {
                    // 添加 SSH 私钥
                    byte[] privateKey = Files.readAllBytes(new File(SSH_PRIVATE_KEY_PATH).toPath());
                    byte[] publicKey = Files.readAllBytes(new File(SSH_PUBLIC_KEY_PATH).toPath());
                    jsch.addIdentity("github-ssh-key", privateKey, publicKey, passphrase != null ? passphrase.getBytes() : null);
                } catch (IOException e) {
                    throw new JSchException("无法读取 SSH 私钥文件: " + SSH_PRIVATE_KEY_PATH, e);
                }
                return jsch;
            }
        });
    }
}
