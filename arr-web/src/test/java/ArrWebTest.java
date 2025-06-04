import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MuShanYu
 * Date 2025/5/30
 */
public class ArrWebTest {
    public static void main(String[] args) {
        String s = SecureUtil.sha256("123456");
        System.out.println(s);
        // 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4
        // 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4
    }
}
