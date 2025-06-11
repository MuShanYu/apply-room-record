package top.mushanyu.system.dto;

import cn.hutool.core.date.DateTime;
import lombok.*;

import java.util.Date;

/**
 * @author MuShanYu
 * Date 2025/6/4
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {

    private String accessToken;

    private DateTime expiresIn;

    private String refreshToken;

    private DateTime refreshExpiresIn;
}
