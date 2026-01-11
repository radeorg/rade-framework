package org.dows.tool;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class GitHubSuperInitializer {

    private static final String GITHUB_TOKEN = "你的_GITHUB_TOKEN"; // 需具备 repo 和 workflow 权限
    private static final String OWNER = "你的用户名";
    private static final String EMAIL_RECEIVER = "your-email@example.com"; // 接收邮件的邮箱
    private static final String SMTP_AUTH_CODE = "你的邮箱授权码";
    
    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {
        try {
            // 1. 获取所有仓库
            List<String> repos = GitHubRepoFetcher.fetchAllRepositoryNames();
            System.out.println("🚀 开始批量配置，共检测到 " + repos.size() + " 个仓库...");

            // 工作流文件内容（这里的 YAML 内容就是之前整合的全能版内容）
            String workflowYaml = getWorkflowTemplate();

            for (String repo : repos) {
                System.out.println("\n[处理仓库: " + repo + "]");
                try {
                    // 2. 配置 Secrets (用户名)
                    updateSecret(repo, "MAIL_USERNAME", OWNER + "@example.com");
                    // 3. 配置 Secrets (授权码)
                    updateSecret(repo, "MAIL_PASSWORD", SMTP_AUTH_CODE);
                    
                    // 4. 自动推送或更新 Workflow 文件
                    upsertWorkflowFile(repo, ".github/workflows/super-notifier.yml", workflowYaml);
                    
                    System.out.println("✅ " + repo + " 配置完成");
                } catch (Exception e) {
                    System.err.println("❌ " + repo + " 失败: " + e.getMessage());
                }
            }
            System.out.println("\n✨ 所有任务已执行完毕！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动判断“创建”或“更新” Workflow 文件
     */
    public static void upsertWorkflowFile(String repo, String path, String content) throws IOException {
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", OWNER, repo, path);
        String sha = null;

        // --- A. 先尝试获取文件，看它是否存在 ---
        Request getRequest = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + GITHUB_TOKEN)
                .build();

        try (Response response = client.newCall(getRequest).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // 文件存在，解析出它的 sha
                JsonObject json = JsonParser.parseString(response.body().string()).getAsJsonObject();
                sha = json.get("sha").getAsString();
            }
        }

        // --- B. 执行写入 (PUT) ---
        String base64Content = Base64.getEncoder().encodeToString(content.getBytes());
        JsonObject body = new JsonObject();
        body.addProperty("message", "chore: automated update of notification workflow");
        body.addProperty("content", base64Content);
        if (sha != null) {
            body.addProperty("sha", sha); // 如果文件存在，必须带上 sha
        }

        Request putRequest = new Request.Builder()
                .url(url)
                .put(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .header("Authorization", "Bearer " + GITHUB_TOKEN)
                .build();

        try (Response response = client.newCall(putRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("无法更新文件: " + response.code() + " " + response.body().string());
            }
        }
    }

    // 辅助方法：更新 Secret（详见之前提供的代码逻辑）
    public static void updateSecret(String repo, String name, String value) throws Exception {
        // 此处省略之前已提供的 updateSecret 具体实现代码，保持逻辑一致即可
    }

    private static String getWorkflowTemplate() {
        return "name: Super Monitor\n" +
               "on:\n" +
               "  push:\n" +
               "    branches: [ main, master ]\n" +
               "  watch:\n" +
               "    types: [started]\n" +
               "  fork:\n" +
               "jobs:\n" +
               "  notify:\n" +
               "    runs-on: ubuntu-latest\n" +
               "    steps:\n" +
               "      - uses: dawidd6/action-send-mail@v3\n" +
               "        with:\n" +
               "          server_address: smtp.qq.com\n" + // 根据你的邮箱改
               "          server_port: 465\n" +
               "          username: ${{secrets.MAIL_USERNAME}}\n" +
               "          password: ${{secrets.MAIL_PASSWORD}}\n" +
               "          subject: 'Repo Alert: ${{github.actor}}'\n" +
               "          to: " + EMAIL_RECEIVER + "\n" +
               "          from: 'GitHub Bot'\n" +
               "          body: 'User ${{github.actor}} triggered ${{github.event_name}} on ${{github.repository}}'";
    }
}