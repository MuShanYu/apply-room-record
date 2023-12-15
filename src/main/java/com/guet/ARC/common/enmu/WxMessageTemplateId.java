package com.guet.ARC.common.enmu;

import lombok.Data;

/**
 * Author: Yulf
 * Date: 2023/12/15
 */
public enum WxMessageTemplateId {

    APPLY_NOTICE_TEMPLATE("Y12YmCT2wYbtSI38JGcuOqTjlqoyUOuWMaoqc_X4slU"),

    WITHDRAW_NOTICE_TEMPLATE("VmPW-Qbm9nVfGU5mvSunjjW9ekd518mY029zd812xnA"),

    APPLY_RESULT_NOTICE_TEMPLATE("baXQdlZqZoYowKZEmVpocG1_4LTZZ1Ar_rRzlD2CJuU"),

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
