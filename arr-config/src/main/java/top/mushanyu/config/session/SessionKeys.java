package top.mushanyu.config.session;

public interface SessionKeys {
    String KEY_PREFIX = "X-App-";

    // 全部使用标准http请求头格式
    String APP_USER_ID = "X-App-User-Id";
    String APP_USER_NAME = "X-App-User-Name";
    String APP_LAN = "X-App-Language";

}
