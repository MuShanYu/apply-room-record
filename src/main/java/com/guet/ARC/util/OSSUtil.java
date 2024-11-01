package com.guet.ARC.util;

import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.guet.ARC.ApplyRoomRecordConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

/**
 * @author Yulf
 * Date 2024/9/18
 */
@Slf4j
public class OSSUtil implements Serializable {

    private String endpoint;

    private String bucketName;

    private static OSSUtil ossUtil;

    private final OSS ossClient;

    private OSSUtil() {
        ossClient = getOSSClient();
    }

    private OSS getOSSClient() {
        if (ossClient != null) {
            return ossClient;
        }
        ApplyRoomRecordConfig globalConfig = SpringUtil.getBean("applyRoomRecordConfig", ApplyRoomRecordConfig.class);
        endpoint = globalConfig.getEndpoint();
        bucketName = globalConfig.getBucketName();
        // 从环境变量中获取访问凭证。运行本代码示例之前，请先配置环境变量。
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(globalConfig.getAccessKeyId(), globalConfig.getAccessKeySecret());
        // 创建OSSClient配置
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setSignatureVersion(SignVersion.V4);
        // 创建OSSClient实例。
        return OSSClientBuilder.create()
                .endpoint("https://" + endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(config)
                .region(globalConfig.getRegion())
                .build();
    }

    public synchronized static OSSUtil getInstance() {
        if (ossUtil == null) {
            ossUtil = new OSSUtil();
        }
        return ossUtil;
    }

    /**
     * 将文件上传到oss上
     *
     * @param filePath 文件路径加名称
     * @param fileByte 文件二进制数据
     * @return 上传成功的文件访问url
     */
    public String uploadFile(String filePath, byte[] fileByte) {
        try {
            ossClient.putObject(bucketName, filePath, new ByteArrayInputStream(fileByte));
            return getPublicReadFilePrefix() + filePath;
        } catch (OSSException e) {
            log.error("文件上传oss失败：{}，失败原因：{}，错误码：{}", filePath, e.getErrorMessage(), e.getErrorCode());
        }
        return "";
    }

    /**
     * 获取公共读文件的访问前缀
     *
     * @return 公共读文件访问前缀
     */
    public String getPublicReadFilePrefix() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    public boolean hasFile(String path) {
        return ossClient.doesObjectExist(bucketName, path);
    }

}
