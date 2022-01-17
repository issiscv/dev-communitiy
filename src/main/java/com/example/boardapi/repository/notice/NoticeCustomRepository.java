package com.example.boardapi.repository.notice;

import com.example.boardapi.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeCustomRepository {

    List<Notice> findByMemberId(Long memberId);

    Page<Notice> findByMemberIdWithPaging(Pageable pageable, Long memberId);

    void updateNoticeChecked(Long memberId);

    Long countByNotCheckedAndMemberId(Long memberId);
}
