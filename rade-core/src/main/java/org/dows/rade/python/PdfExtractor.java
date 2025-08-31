package org.dows.rade.python;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class PdfExtractor {

    /**
     * 调用本地 Python 清洗脚本，返回清洗后的 JSON
     *
     * @param pdfPath 本地 pdf 路径 或 http/https 预签名 URL
     * @param python  python 可执行文件路径
     *                Windows: "python" 或 "python3"（已在 PATH）
     *                Linux/Mac: "/usr/bin/python3"
     * @return Map<String,String> 对应 JSON {telephone, email, content}
     * @throws Exception 网络/进程/JSON 解析异常
     */
    public static <T> T extract(String pdfPath, String python,Class<T> clazz) throws Exception {
        if(StrUtil.isBlank(python)){
            python = "python";
        }
        ProcessBuilder pb = new ProcessBuilder(python, "pdf.py", pdfPath);
        pb.redirectErrorStream(true);           // 合并 stderr
        Process proc = pb.start();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(proc.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        int exitCode = proc.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python 脚本退出码=" + exitCode);
        }

        // 把 JSON 转成 Map
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(sb.toString(), clazz);
    }

    // Demo
//    public static void main(String[] args) throws Exception {
//        // 本地文件
//        Map<String, String> res = clean("D:/resume/4.pdf", "python");
//        // 或预签名 URL
//        // Map<String, String> res = clean(
//        //     "https://your-bucket.cos.ap-shanghai.myqcloud.com/4.pdf?sign=...", "python");
//
//        System.out.println("手机号: " + res.get("telephone"));
//        System.out.println("邮箱  : " + res.get("email"));
//        System.out.println("正文预览: " + res.get("content").substring(0, 200));
//    }
}