package org.dows.rade.func;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

interface ResourceCaller {
    Map<String, Object> call(String uri) throws Exception;
}

class JdbcResourceCaller implements ResourceCaller {
    private static final String JDBC_URL_FORMAT = "jdbc:mysql://%s/%s";
    private static final String USER = "your_username"; // replace with your username
    private static final String PASSWORD = "your_password"; // replace with your password

    @Override
    public Map<String, Object> call(String uri) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        String[] parts = uri.split("\\?");
        String path = parts[0].substring("jdbc://".length());
        String params = parts.length > 1 ? parts[1] : "";

        String[] dbParts = path.split("/");
        String dataSource = dbParts[1];
        String database = dbParts[2];
        String table = dbParts[3];
        String operation = dbParts[4];

        String jdbcUrl = String.format(JDBC_URL_FORMAT, dataSource, database);

        try (Connection conn = DriverManager.getConnection(jdbcUrl, USER, PASSWORD)) {
            StringBuilder queryBuilder = new StringBuilder();
            if ("select".equals(operation)) {
                queryBuilder.append("SELECT ").append(params.split("=")[1]).append(" FROM ").append(table);
            }

            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String key = params.split(":")[0].split("=")[1];
                    Object value = rs.getObject(key);
                    result.put(key, value);
                }
            }
        }
        return result;
    }
}

class HttpResourceCaller implements ResourceCaller {
    @Override
    public Map<String, Object> call(String uri) throws Exception {
        Map<String, Object> result = new HashMap<>();
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Assuming the response is a simple JSON string like {"amount": 100}
            String jsonResponse = content.toString();
            String[] keyValuePairs = jsonResponse.substring(1, jsonResponse.length() - 1).split(",");
            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                String key = entry[0].trim().replaceAll("\"", "");
                String value = entry[1].trim().replaceAll("\"", "");
                result.put(key, Integer.parseInt(value));
            }
        } else {
            throw new RuntimeException("Failed to get HTTP response: " + responseCode);
        }
        return result;
    }
}

class ClassResourceCaller implements ResourceCaller {
    @Override
    public Map<String, Object> call(String uri) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String[] parts = uri.split("\\?");
        String className = parts[0].substring("class://".length()).replace('/', '.');
        String params = parts.length > 1 ? parts[1] : "";

        Class<?> clazz = Class.forName(className);
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Assuming the method name is 'add' and it takes parameters as specified in the URI
        String methodName = "add";
        String[] paramPairs = params.split("&");
        Object[] args = new Object[paramPairs.length];
        Class<?>[] argTypes = new Class<?>[paramPairs.length];

        for (int i = 0; i < paramPairs.length; i++) {
            String[] kv = paramPairs[i].split(":");
            String type = kv[1].split(",")[0];
            String value = kv[1].split(",")[1];

            switch (type) {
                case "string":
                    args[i] = value;
                    argTypes[i] = String.class;
                    break;
                case "long":
                    args[i] = Long.parseLong(value);
                    argTypes[i] = long.class;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported parameter type: " + type);
            }
        }

        java.lang.reflect.Method method = clazz.getMethod(methodName, argTypes);
        Object methodResult = method.invoke(instance, args);

        // Assuming the method returns an integer
        result.put("result", (Integer) methodResult);
        return result;
    }
}

public class ResourceCallerFactory {
    public static ResourceCaller getResourceCaller(String uri) {
        if (uri.startsWith("jdbc://")) {
            return new JdbcResourceCaller();
        } else if (uri.startsWith("http://") || uri.startsWith("https://")) {
            return new HttpResourceCaller();
        } else if (uri.startsWith("class://")) {
            return new ClassResourceCaller();
        } else {
            throw new IllegalArgumentException("Unknown URI scheme: " + uri);
        }
    }

    public static void main(String[] args) {
        try {
            String jdbcUri = "jdbc://mysql/ds1/db1/tb1/select?out=score:integer";
            String httpUri = "http://xxxx.com/api1?in=score:integer&out=amount:integer";
            String classUri = "class://org/dows/exam/question/add?in=p1:string,p2:long";

            System.out.println(ResourceCallerFactory.getResourceCaller(jdbcUri).call(jdbcUri));
            System.out.println(ResourceCallerFactory.getResourceCaller(httpUri).call(httpUri));
            System.out.println(ResourceCallerFactory.getResourceCaller(classUri).call(classUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}