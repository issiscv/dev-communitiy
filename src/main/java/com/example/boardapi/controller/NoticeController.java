package com.example.boardapi.controller;

import com.example.boardapi.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    //알림 조회
    @PutMapping("/notices/{noticeId}")
    public ResponseEntity updateReadNotice(@PathVariable Long noticeId, @RequestParam(defaultValue = "1") int page) {

        noticeService.updateNoticeOne(noticeId);

        return ResponseEntity.noContent().build();
    }
}
