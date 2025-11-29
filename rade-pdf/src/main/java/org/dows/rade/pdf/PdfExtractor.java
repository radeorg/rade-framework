//package org.dows.rade.pdf;
//
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class PdfExtractor {
//    // 用于确保线程安全的锁
//    private static final ReentrantLock lock = new ReentrantLock();
//    // 缓存临时文件路径
//    private static volatile Path cachedTempScript = null;
//
//    /**
//     * 调用本地 Python 清洗脚本，返回清洗后的 JSON
//     *
//     * @param pdfPath 本地 pdf 路径 或 http/https 预签名 URL
//     * @param python  python 可执行文件路径
//     *                Windows: "python" 或 "python3"（已在 PATH）
//     *                Linux/Mac: "/usr/bin/python3"
//     * @return Map<String,String> 对应 JSON {telephone, email, content}
//     * @throws Exception 网络/进程/JSON 解析异常
//     */
//    public static ResumePdfData extract(String pdfPath, String python) throws Exception {
//        if (StrUtil.isBlank(python)) {
//            python = "python3";
//        }
//
//        // 获取或创建临时脚本文件
//        Path tempScript = getOrCreateTempScript();
//
//        // 构建 Python 脚本执行命令
//        ProcessBuilder pb = new ProcessBuilder(python, tempScript.toString(), pdfPath);
//        pb.redirectErrorStream(true); // 合并 stderr
//        // 设置环境变量，确保Python使用UTF-8编码
//        pb.environment().put("PYTHONIOENCODING", "utf-8");
//        pb.environment().put("LANG", "en_US.UTF-8");
//        pb.environment().put("LC_ALL", "en_US.UTF-8");
//        Process proc = pb.start();
//
//        StringBuilder sb = new StringBuilder();
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//        }
//
//        int exitCode = proc.waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("Python 脚本退出码=" + exitCode);
//        }
//
//        // 把 JSON 转成指定类型
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.readValue(sb.toString(), ResumePdfData.class);
//    }
//
//    /**
//     * 获取或创建临时脚本文件
//     *
//     * @return 临时脚本文件路径
//     * @throws Exception IO异常
//     */
//    private static Path getOrCreateTempScript() throws Exception {
//        // 双重检查锁定模式确保线程安全
//        if (cachedTempScript == null || !Files.exists(cachedTempScript)) {
//            lock.lock();
//            try {
//                // 再次检查，防止重复创建
//                if (cachedTempScript == null || !Files.exists(cachedTempScript)) {
//                    cachedTempScript = createTempScript();
//                }
//            } finally {
//                lock.unlock();
//            }
//        }
//        return cachedTempScript;
//    }
//
//    /**
//     * 创建临时脚本文件
//     *
//     * @return 临时脚本文件路径
//     * @throws Exception IO异常
//     */
//    private static Path createTempScript() throws Exception {
//        ClassPathResource resource = new ClassPathResource("/org/dows/radepdf/pdf.py");
//        if (!resource.exists()) {
//            // 如果在包路径下找不到，尝试从根路径查找
//            resource = new ClassPathResource("pdf.py");
//        }
//
//        // 创建临时文件来执行 Python 脚本
//        Path tempScript = Files.createTempFile("pdf", ".py");
//        try (InputStream is = resource.getInputStream();
//             OutputStream os = Files.newOutputStream(tempScript)) {
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = is.read(buffer)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//        }
//
//        return tempScript;
//    }
//
//    /**
//     * 清理临时文件（可选）
//     */
//    public static void cleanup() {
//        if (cachedTempScript != null) {
//            try {
//                Files.deleteIfExists(cachedTempScript);
//                cachedTempScript = null;
//            } catch (Exception e) {
//                // 忽略删除失败
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            ResumePdfData extract = PdfExtractor.extract("E:\\logs\\7.pdf", "E:\\workspace\\python\\java\\python.exe");
//            System.out.println(extract);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}