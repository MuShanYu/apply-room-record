package com.guet.ARC.service;

import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.NoticeRepository;
import com.guet.ARC.domain.Notice;
import com.guet.ARC.domain.dto.notice.NoticeQueryDTO;
import com.guet.ARC.domain.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public PageInfo<Notice> queryNoticeList(NoticeQueryDTO queryDTO) {
        PageRequest pageRequest = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), Sort.Direction.DESC, "createTime");
        return new PageInfo<>(noticeRepository.findAll(pageRequest));
    }

    public void updateNotice(Notice notice) {
        noticeRepository.saveAndFlush(notice);
    }

    public void setNoticeToDeleted(String noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            notice.setState(State.DELETED);
            noticeRepository.saveAndFlush(notice);
        }
    }

    public void resetNoticeToNormal(String noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isPresent()) {
            Notice notice = noticeOptional.get();
            notice.setState(State.NORMAL);
            noticeRepository.saveAndFlush(notice);
        }
    }

    public void saveNotice(Notice notice) {
        noticeRepository.saveAndFlush(notice);
    }
}
