package com.guet.ARC.common.enmu;

import lombok.Data;

/**
 * Author: Yulf
 * Date: 2023/12/15
 */
public enum WxMessageTemplateId {

    APPLY_NOTICE_TEMPLATE("Y12YmCT2wYbtSI38JGcuOmPQcFysZEfEiMnzYCfuJgI"),

    WITHDRAW_NOTICE_TEMPLATE("legIlVVfukDUkNavrILDAwWjy1H0upCXO00IontC8p4"),

    APPLY_SUCCESS_NOTICE_TEMPLATE("48xKmWfwx9x13ErSEcaN-Q74tBtmO8QOSSXKZTzdSI0"),

    APPLY_FAILED_NOTICE_TEMPLATE("1S8cwxpW5OqEb_iUwZfk1F6u3bm38jBhwnK3u_0juH8"),

    NEW_APPLICATION_NOTICE_TEMPLATE("RUkh4a-qXYLB89EtnzuoNfyvZjyMf2zqugk8JJgOuTE"),

    APPLICATION_RESULT_NOTICE_TEMPLATE("tYamJXEa6H100qxCznqug4d6FJPgfSfxyWyo0XE4jqg");

    private final String id;

    WxMessageTemplateId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
