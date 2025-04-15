package org.dows.rade.git.ai;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;

import java.io.IOException;

public class AICodeAnalyzer {
    private static final String API_KEY = "your-api-key";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String analyzeWithAI(String diffContent) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 构造提示词
        String prompt = "作为代码审查助手，请分析以下变更：\n"
                + "1. 业务逻辑变化\n"
                + "2. 潜在风险点\n"
                + "3. 优化建议\n\n"
                + diffContent;

        // 构造请求体
        JSONObject requestBody = JSONUtil.createObj()
                .put("model", "gpt-4-1106-preview")
                .put("temperature", 0.2)
                .put("max_tokens", 1500)
                .put("messages", JSONUtil.createArray().put(
                        JSONUtil.createObj()
                                .put("role", "user")
                                .put("content", prompt)
                ));

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject jsonResponse = JSONUtil.parseObj(response.body().string());
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
        }
    }
}
