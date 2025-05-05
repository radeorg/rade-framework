package org.dows.oss.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.web.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Collection;

@Slf4j
public class OssRest {
    @Value("${spring.servlet.multipart.location:E:/data/upload}")
    private String tempDir;


    @PostMapping("/upload/chunk")
    public Response upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile multipartFile) throws Exception {
        String authorization = httpServletRequest.getHeader("Authorization");
        /*if (authorization.isEmpty()) {
            throw new AuthenticationException(AuthStatusCode.JWT_INVALID);
        }*/
        /**
         * todo 根据 authorization token 验证，并获取租户配置
         */
        // 分片文件需要
        Collection<Part> parts = httpServletRequest.getParts();
        Part md5 = httpServletRequest.getPart("md5");

//        log.info("客户:{},IP:{},上传文件：{},大小：{}", authorization, IpUtil.getIp(httpServletRequest),
//                multipartFile.getOriginalFilename(), multipartFile.getSize());

        /**
         *  RandomAccessFile randomAccessFile =
         *      new RandomAccessFile(tempDir + File.separator + multipartFile.getOriginalFilename(), "rw");
         *  FileChannel channel = randomAccessFile.getChannel();
         *  MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, multipartFile.getSize());
         *  byte[] bytes = multipartFile.getBytes();
         *  //顺序写入
         *  for (int i = 0; i < multipartFile.getSize(); i++) {
         *      mappedByteBuffer.put(bytes);
         *  }
         * zero-copy
         * multipartFile.transferTo(new File(tempDir + File.separator + multipartFile.getOriginalFilename()));
         * 此处采用了零copy
         */

        FileOutputStream fileOutputStream =
                new FileOutputStream(tempDir + File.separator + multipartFile.getOriginalFilename());
        FileChannel channel = fileOutputStream.getChannel();
        channel.transferFrom(Channels.newChannel(multipartFile.getInputStream()), 0, multipartFile.getSize());
        channel.close();
        fileOutputStream.close();
        /**
         * 异步通知处理
         */
//        final DeferredResult<Response> response = new DeferredResult<>();
//        ListenableFuture<Response<Resource>> future = copFileThreadPool.submitListenable(() -> fileCoding(multipartFile));
//        future.addCallback(new VideoResponseCallback(response));
        // 直接返回
        return Response.ok();
    }


    @PostMapping("/file")
    public Response upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        log.info("上传文件：{},大小：{}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        ;
        /**
         * 上传分片文件,多个文件
         *
         * @param data       分片数据
         * @param fileName   文件名
         * @param md5        文件md5
         * @param chunkMd5   分片数据Md5
         * @param chunkIndex 分片索引
         * @param chunkCount 分片总数
         * @return
         */
//        String md5Hex = Md5Util.getMd5Hex(multipartFile.getBytes());
//        Response response = fileClient.upload(multipartFile.getBytes(), multipartFile.getOriginalFilename(), md5Hex, md5Hex, 1, 1);
//
//        //
//        Response response1 = fileClient.uploadAndWrite(multipartFile.getBytes(), multipartFile.getOriginalFilename());

//        log.info("返回结果：{}", JSONUtil.toJsonStr(response));
        return Response.ok();
    }

//
//    @PostMapping("/callback")
//    public Response uploadCallback(@RequestBody FileInfo fileInfo) throws IOException {
//        log.info("完成转码文件：{}", JSONUtil.toJsonStr(fileInfo));
//        // todo 其他处理
//        return Response.ok();
//    }
}
