package top.mushanyu.system.dto;

import lombok.*;

/**
 * @author MuShanYu
 * Date 2025/6/4
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {

    private String accessToken;

    private String refreshToken;
}
