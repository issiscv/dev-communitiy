package com.example.boardapi.service;

import com.example.boardapi.entity.Board;
import com.example.boardapi.entity.Member;
import com.example.boardapi.entity.Scrap;
import com.example.boardapi.exception.AlreadyScrapedException;
import com.example.boardapi.exception.BoardNotFoundException;
import com.example.boardapi.exception.message.BoardExceptionMessage;
import com.example.boardapi.repository.board.BoardRepository;
import com.example.boardapi.repository.scrap.ScrapRepository;
import com.example.boardapi.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapService {

    private final JwtTokenProvider jwtTokenProvider;
    private final ScrapRepository scrapRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void save(Long boardId, String token) {
        //토큰을 해석하여 회원 엔티티 조회
        Member member = jwtTokenProvider.getMember(token);

        //스크랩하고자 하는 게시글 조회
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> {
                    throw new BoardNotFoundException(BoardExceptionMessage.BOARD_NOT_FOUND);
                }
        );

        //회원의 스크랩 조회
        List<Scrap> scraps = scrapRepository.findByMemberId(member.getId());

        //이미 스크랩 했으면 에러
        for (Scrap scrap : scraps) {
            if (scrap.getBoard() == board) {
                throw new AlreadyScrapedException("이미 스크랩 하셨습니다.");
            }
        }

        Scrap scrap = Scrap.builder()
                .board(board)
                .member(member)
                .build();

        scrapRepository.save(scrap);
    }

    public List<Scrap> retrieveByMemberId(Long memberId) {
        return scrapRepository.findByMemberId(memberId);
    }


}
