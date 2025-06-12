package top.mushanyu.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author MuShanYu
 * Date 2025/6/3
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "arr-web.common", ignoreUnknownFields = false)
public class ArrCommonConfig implements InitializingBean {

    private String jwtSecret;

    private Integer jwtExpireTime;

    private Integer jwtRefreshExpireTime;

    /**
     * 名称: JWT签发RSA私钥与公钥封装，请勿在配置文件中配置该属性，由配置文件加载后自动创建
     */
    private RSA rsa;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 如果没有公钥私钥，用以下代码生成一对。
        if (!FileUtil.exist("classpath:private_base64.key")
                || !FileUtil.exist("classpath:public_base64.key")) {
            RSA rsa = new RSA();
            String privateKeyBase64 = rsa.getPrivateKeyBase64();
            String publicKeyBase64 = rsa.getPublicKeyBase64();
            log.warn("please use the following code to generate the key files at resources dir.");
            log.info("privateKeyBase64:{}", privateKeyBase64);
            log.info("publicKeyBase64:{}", publicKeyBase64);
        } else {
            // 项目启动时加载公钥和私钥进内存中，读取公钥私钥
            String privateKey = FileUtil.readString(FileUtil.file("classpath:private_base64.key"), StandardCharsets.UTF_8);
            String publicKey = FileUtil.readString(FileUtil.file("classpath:public_base64.key"), StandardCharsets.UTF_8);
            this.rsa = new RSA(privateKey, publicKey);
        }
        log.info("load common config is {}", this);
    }
}
