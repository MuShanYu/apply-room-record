package top.mushanyu.common.enums;

/**
 * @author Yulf
 * Date 2025/6/12
 */
public enum TokenType {
    /**
     * 普通身份令牌
     */
    NORMAL,

    /**
     * 刷新令牌
     */
    REFRESH;

    // 方法：更具枚举下标返回对应枚举
    public static TokenType valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return values()[ordinal];
    }
}
