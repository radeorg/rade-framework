package org.dows.tool;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.interfaces.Box;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class GitHubSecretManager {

    private static final String GITHUB_TOKEN = "你的_GITHUB_TOKEN";
    private static final String OWNER = "你的用户名";
    private static final OkHttpClient client = new OkHttpClient();
    private static final LazySodiumJava ls = new LazySodiumJava(new SodiumJava());

    public static void main(String[] args) {
        // 需要批量操作的仓库列表
        List<String> repos = List.of("repo1", "repo2", "repo3");

        for (String repo : repos) {
            System.out.println("正在为仓库 " + repo + " 配置 Secrets...");
            try {
                updateSecret(repo, "MAIL_USERNAME", "your-email@example.com");
                updateSecret(repo, "MAIL_PASSWORD", "your-email-auth-code");
                System.out.println("✅ " + repo + " 配置成功！");
            } catch (Exception e) {
                System.err.println("❌ " + repo + " 失败: " + e.getMessage());
            }
        }
    }


    public static void upsertWorkflowFile(String repo, String content) throws IOException {
        String path = ".github/workflows/super-notifier.yml";
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", OWNER, repo, path);

        // 转换内容为 Base64（GitHub 要求）
        String base64Content = java.util.Base64.getEncoder().encodeToString(content.getBytes());

        JsonObject body = new JsonObject();
        body.addProperty("message", "chore: update notification workflow [automated]");
        body.addProperty("content", base64Content);

        // 注意：如果是更新已有文件，需要获取文件的 sha，这里简单处理，假设是新建
        // 实际生产中建议先 GET 一下获取 sha 填入 body 中，避免 409 冲突

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .header("Authorization", "Bearer " + GITHUB_TOKEN)
                .build();

        client.newCall(request).execute().close();
    }

    /**
     * 更新或创建 Secret 的核心方法
     */
    public static void updateSecret(String repo, String secretName, String plainValue) throws Exception {
        // 1. 获取公钥信息
        JsonObject publicKeyInfo = getPublicKey(repo);
        String publicKeyBase64 = publicKeyInfo.get("key").getAsString();
        String keyId = publicKeyInfo.get("key_id").getAsString();

        // 2. 加密 Secret 值
        String encryptedValue = encryptSecret(publicKeyBase64, plainValue);

        // 3. 提交到 GitHub
        String url = String.format("https://api.github.com/repos/%s/%s/actions/secrets/%s", OWNER, repo, secretName);

        JsonObject body = new JsonObject();
        body.addProperty("encrypted_value", encryptedValue);
        body.addProperty("key_id", keyId);

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .header("Authorization", "Bearer " + GITHUB_TOKEN)
                .header("Accept", "application/vnd.github+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        }
    }

    private static JsonObject getPublicKey(String repo) throws IOException {
        String url = String.format("https://api.github.com/repos/%s/%s/actions/secrets/public-key", OWNER, repo);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + GITHUB_TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return JsonParser.parseString(response.body().string()).getAsJsonObject();
        }
    }

//    private static String encryptSecret(String publicKeyBase64, String plainValue) throws Exception {
//        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
//        byte[] plainValueBytes = plainValue.getBytes();
//        byte[] encryptedBytes = new byte[plainValueBytes.length + ls.cryptoBoxSealbytes()];
//
//        // 使用 libsodium 的 sealed box 加密
//        ls.cryptoBoxSeal(encryptedBytes, plainValueBytes, plainValueBytes.length, publicKeyBytes);
//
//        return Base64.getEncoder().encodeToString(encryptedBytes);
//    }

    private static String encryptSecret(String publicKeyBase64, String plainValue) {
        // 初始化 LazySodium (5.2.0 标准写法)
        LazySodiumJava ls = new LazySodiumJava(new SodiumJava());

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        byte[] plainValueBytes = plainValue.getBytes();

        // 5.2.0 中使用 Box.SEALBYTES 获取固定的 48 字节额外开销
        byte[] encryptedBytes = new byte[plainValueBytes.length + Box.SEALBYTES];

        // 调用底层密封箱加密
        boolean success = ls.cryptoBoxSeal(
                encryptedBytes,
                plainValueBytes,
                plainValueBytes.length,
                publicKeyBytes
        );
        if (!success) throw new RuntimeException("Encryption failed via Libsodium");
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}