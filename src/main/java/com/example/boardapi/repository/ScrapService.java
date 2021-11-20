package com.example.boardapi.repository;

import com.example.boardapi.entity.Scrap;
import com.example.boardapi.exception.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;

    @Transactional
    public void save(Scrap scrap) {
        scrapRepository.save(scrap);
    }

    @Transactional
    public List<Scrap> retrieveByMemberId(Long memberId) {
        return scrapRepository.findByMemberId(memberId);
    }
}
