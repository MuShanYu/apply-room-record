package top.mushanyu.message.api;

class Info {
    public static final String NAME = "message-service";
    public static final String URL = "${arr.url." + NAME + "}";
    public static final String JAX_RS_PATH = NAME;
    public static final String CONTEXT_ID = "${arr.feign-context-id.message-service:jaxrs}";
}