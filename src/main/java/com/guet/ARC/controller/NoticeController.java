package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.Notice;
import com.guet.ARC.domain.dto.notice.NoticeQueryDTO;
import com.guet.ARC.service.NoticeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
@Api(tags = "公告模块")
@RestController
@ResponseBodyResult
@Validated
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/notice/save")
    public void saveNoticeApi(Notice notice) {
        noticeService.saveNotice(notice);
    }

    @PutMapping("/notice")
    public void updateNoticeApi(Notice notice) {
        noticeService.updateNotice(notice);
    }

    @PostMapping("/notice/query/list")
    public PageInfo<Notice> queryNoticeListApi(@Valid NoticeQueryDTO queryDTO) {
        return noticeService.queryNoticeList(queryDTO);
    }

    @DeleteMapping("/notice")
    public void deleteNoticeApi(String noticeId) {
        noticeService.setNoticeToDeleted(noticeId);
    }

    @PutMapping("/notice/reset")
    public void resetNoticeApi(String noticeId) {
        noticeService.resetNoticeToNormal(noticeId);
    }
}
