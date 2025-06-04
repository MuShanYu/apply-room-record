package top.mushanyu.config.session;

public interface SessionKeys {
    String KEY_PREFIX = "x-app-";

    // 全部使用标准http请求头格式
    String APP_USER_ID = "x-app-user-id";
//    String APP_USER_NAME = "x-app-user-name";
    String APP_LAN = "x-app-language";

}
