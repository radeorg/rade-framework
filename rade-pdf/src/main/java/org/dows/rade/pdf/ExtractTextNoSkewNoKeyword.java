package org.dows.rade.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 PDFBox 3.0.5：
 * 1. 剔除旋转角度 > 10° 的文字
 * 2. 剔除含指定关键字的整行
 * 3. 删除多余空行
 */
public class ExtractTextNoSkewNoKeyword extends PDFTextStripper {

    /**
     * 需要剔除的关键字（大小写不敏感）
     */
    private final Set<String> keywords;

    public ExtractTextNoSkewNoKeyword(Collection<String> keywords) throws IOException {
        super();
        this.keywords = keywords.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        // 关闭 stripper 自带的分页空白
        // pageSeparator("");   // 去掉默认换页符
    }


    /* =============== 页首/页尾去空行 =============== */
    @Override
    protected void writePageStart() throws IOException {
        // 什么都不写，杜绝页首 \n
    }

    @Override
    protected void writePageEnd() throws IOException {
        // 只保留一个换行作为“页与页”之间的唯一分隔
        output.write('\n');
    }

    /* ===== 核心：丢弃倾斜文字 & 含关键字整行 ===== */
    @Override
    protected void writeString(String text, List<TextPosition> positions) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (TextPosition tp : positions) {
            double rad = Math.atan2(tp.getTextMatrix().getShearY(), tp.getTextMatrix().getScaleY());
            double deg = Math.toDegrees(rad);
            // 只保留 <=1°
            if (Math.abs(deg) <= 1) {
                sb.append(tp.getUnicode());
            }
        }
        String line = sb.toString().trim();
        // 空行直接丢弃
        if (line.isEmpty()) {
            return;
        }

        // 关键字过滤（大小写不敏感）
        String lineLower = line.toLowerCase();
        for (String kw : keywords) {
            // 命中关键字 -> 丢弃整行
            if (lineLower.contains(kw)) {
                return;
            }
        }

        /* 3. 压缩段落内部多余换行 -> 单空格 */
        line = line.replaceAll("\\s+", " ").trim().replaceAll("\\r\\n","");
        // 输出无空行纯文本
        output.write(line);
        /*if (!line.isEmpty()) {
            output.write(line);
        }*/
        //output.write('\n');
        //line = sb.toString().trim();

    }

    /* ================= 入口 ================= */
    public static void main(String[] args) throws IOException {
        //PDDocument doc = Loader.loadPDF(new File("D:\\download\\51job_张斌_后端技术合伙人_上海(4537107).pdf"));
        PDDocument doc = Loader.loadPDF(new File("E:\\download\\download\\resume\\后端\\51job_程柏松_后端技术合伙人_上海(5826206).pdf"));

        String out = "E:\\download\\download\\resume\\后端\\51job_程柏松_后端技术合伙人_上海(5826206).txt";
        //Writer w = new OutputStreamWriter(new FileOutputStream("D:\\download\\51job_张斌_后端技术合伙人_上海(4537107)_clean.txt"), "UTF-8");
        Writer w = new OutputStreamWriter(new FileOutputStream(out), "UTF-8");

        List<String> list = Arrays.asList("仅供招聘专用，企业应尽保密义务，禁止外传", "一经发现我司有权采取一切必要措施，包括但不限于暂停或终止服务。");

        ExtractTextNoSkewNoKeyword stripper = new ExtractTextNoSkewNoKeyword(list);

        stripper.writeText(doc, w);
        /*StringWriter sw = new StringWriter();
        stripper.setSortByPosition(true);
        stripper.writeText(doc, sw);
        String oneLine = sw.toString().replaceAll("\\rn", "");
        Files.writeString(Path.of(out), oneLine, StandardCharsets.UTF_8);*/
    }
}