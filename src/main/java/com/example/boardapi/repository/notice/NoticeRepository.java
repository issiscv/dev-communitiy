package com.example.boardapi.repository.notice;

import com.example.boardapi.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
