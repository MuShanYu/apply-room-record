package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.NoticeRepository;
import com.guet.ARC.dao.mybatis.NoticeQueryRepository;
import com.guet.ARC.dao.mybatis.query.NoticeQuery;
import com.guet.ARC.domain.Notice;
import com.guet.ARC.domain.dto.notice.NoticeQueryDTO;
import com.guet.ARC.domain.enums.State;
import com.guet.ARC.domain.vo.notice.NoticeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeQueryRepository noticeQueryRepository;

    @Autowired
    private NoticeQuery noticeQuery;

    public PageInfo<NoticeVo> queryNoticeListAdmin(NoticeQueryDTO queryDTO) {
        Page<NoticeVo> pageResult = PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        noticeQueryRepository.selectNoticeVo(noticeQuery.queryNoticeListAdminSql(queryDTO.getTitle()));
        return new PageInfo<>(pageResult);
    }

    public PageInfo<NoticeVo> queryNoticeList(Integer page, Integer size) {
        Page<NoticeVo> pageResult = PageHelper.startPage(page, size);
        noticeQueryRepository.selectNoticeVo(noticeQuery.queryNoticeListUserSql());
        return new PageInfo<>(pageResult);
    }

    public void updateNotice(Notice notice) {
        notice.setUpdateTime(System.currentTimeMillis());
        noticeRepository.saveAndFlush(notice);
    }

    public void setNoticeToDeleted(String noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            notice.setState(State.NEGATIVE);
            notice.setUpdateTime(System.currentTimeMillis());
            noticeRepository.saveAndFlush(notice);
        }
    }

    public void resetNoticeToNormal(String noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            notice.setState(State.ACTIVE);
            notice.setUpdateTime(System.currentTimeMillis());
            noticeRepository.saveAndFlush(notice);
        }
    }

    public void saveNotice(Notice notice) {
        long now = System.currentTimeMillis();
        notice.setId(IdUtil.fastSimpleUUID());
        notice.setPublishUserId(StpUtil.getSession().getString("userId"));
        notice.setUpdateTime(now);
        notice.setCreateTime(now);
        noticeRepository.saveAndFlush(notice);
    }
}
