package org.dows.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GitHubRepoFetcher {

    private static final String GITHUB_TOKEN = "你的_GITHUB_TOKEN";
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 获取当前用户作为 Owner 的所有公开及私有仓库名
     */
    public static List<String> fetchAllRepositoryNames() throws IOException {
        List<String> repoNames = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = String.format("https://api.github.com/user/repos?visibility=all&affiliation=owner&per_page=100&page=%d", page);
            
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + GITHUB_TOKEN)
                    .header("Accept", "application/vnd.github+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("获取仓库失败: " + response);

                String jsonData = response.body().string();
                JsonArray jsonArray = JsonParser.parseString(jsonData).getAsJsonArray();

                // 如果这一页没有数据了，说明抓完了
                if (jsonArray.isEmpty()) break;

                for (JsonElement element : jsonArray) {
                    repoNames.add(element.getAsJsonObject().get("name").getAsString());
                }
                page++;
            }
        }
        return repoNames;
    }


}