package com.guet.ARC.domain.vo.notice;

import com.guet.ARC.domain.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class NoticeVo extends Notice {
    private String publishUserName;
}
