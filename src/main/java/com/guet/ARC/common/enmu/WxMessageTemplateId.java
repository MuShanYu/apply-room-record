package com.guet.ARC.common.enmu;

import lombok.Data;
import lombok.Getter;

/**
 * Author: Yulf
 * Date: 2023/12/15
 */
@Getter
public enum WxMessageTemplateId {

    APPLY_NOTICE_TEMPLATE("Y12YmCT2wYbtSI38JGcuOmPQcFysZEfEiMnzYCfuJgI"),

    WITHDRAW_NOTICE_TEMPLATE("legIlVVfukDUkNavrILDAwWjy1H0upCXO00IontC8p4"),

    APPLY_SUCCESS_NOTICE_TEMPLATE("48xKmWfwx9x13ErSEcaN-Q74tBtmO8QOSSXKZTzdSI0"),

    APPLY_FAILED_NOTICE_TEMPLATE("1S8cwxpW5OqEb_iUwZfk1NpO8rxcWJgFirAQWdYd-Ro"),

    NEW_APPLICATION_NOTICE_TEMPLATE("RUkh4a-qXYLB89EtnzuoNa4VmOskGqDkTth7sPWPs24"),

    APPLICATION_RESULT_NOTICE_TEMPLATE("KDq-tcXy2GiJAE37geDtN5Ta-G5wJ2szj0U_4dgHSro");

    private final String id;

    WxMessageTemplateId(String id) {
        this.id = id;
    }

}
