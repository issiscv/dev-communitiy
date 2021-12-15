package com.example.boardapi.repository.notice;

import com.example.boardapi.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeCustomRepository {

    List<Notice> findByMemberId(Long memberId);

    @Query(value = "select n from Notice n where n.member.id = :memberId",
    countQuery = "select count(n) from Notice n where n.member.id = :memberId")
    Page<Notice> findByMemberIdWithPaging(Pageable pageable, @Param("memberId") Long memberId);

    @Query(value = "update Notice n set n.isChecked = true where n.member.id = :memberId")
    @Modifying(clearAutomatically = true)
    void updateNoticeChecked(@Param("memberId") Long memberId);
}
