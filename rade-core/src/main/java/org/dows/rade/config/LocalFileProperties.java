package org.dows.rade.config;

import lombok.Data;
import org.dows.rade.util.PathUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * 文件
 */
@Data
//@Configuration
@ConfigurationProperties(prefix = "rade.upload.local")
public class LocalFileProperties {

    // 跟域名
    private String baseUrl;

    private String uploadPath = "assets/public/upload";

    public String getAbsoluteUploadFolder() {
        if (!PathUtils.isAbsolutePath(uploadPath)) {
            // 相对路径
            return System.getProperty("user.dir") + File.separator + uploadPath;
        }
        // 绝对路径
        return uploadPath;
    }
}
