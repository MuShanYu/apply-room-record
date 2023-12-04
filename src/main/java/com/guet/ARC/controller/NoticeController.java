package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.common.constant.CommonConstant;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.domain.Notice;
import com.guet.ARC.domain.dto.notice.NoticeQueryDTO;
import com.guet.ARC.domain.vo.notice.NoticeVo;
import com.guet.ARC.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation(value = "保存公告")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public void saveNoticeApi(@RequestBody Notice notice) {
        noticeService.saveNotice(notice);
    }

    @PutMapping("/notice")
    @ApiOperation(value = "修改公告")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public void updateNoticeApi(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
    }

    @PostMapping("/notice/query/admin/list")
    @ApiOperation(value = "管理员公告列表")
    @SaCheckRole(value = {CommonConstant.ADMIN_ROLE, CommonConstant.SUPER_ADMIN_ROLE}, mode = SaMode.OR)
    public PageInfo<NoticeVo> queryNoticeListAdminApi(@Valid @RequestBody NoticeQueryDTO queryDTO) {
        return noticeService.queryNoticeListAdmin(queryDTO);
    }

    @GetMapping("/notice/query/user/list")
    @ApiOperation(value = "用户公告列表")
    public PageInfo<NoticeVo> queryNoticeListApi(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return noticeService.queryNoticeList(page, size);
    }

    @DeleteMapping("/notice")
    @ApiOperation(value = "删除公告")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public void deleteNoticeApi(@RequestParam("noticeId") String noticeId) {
        noticeService.setNoticeToDeleted(noticeId);
    }

    @PutMapping("/notice/reset")
    @ApiOperation(value = "重置公告状态")
    @SaCheckRole(value = {CommonConstant.SUPER_ADMIN_ROLE})
    public void resetNoticeApi(@RequestParam("noticeId") String noticeId) {
        noticeService.resetNoticeToNormal(noticeId);
    }
}
