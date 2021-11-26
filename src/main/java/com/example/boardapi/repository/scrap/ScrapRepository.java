package com.example.boardapi.repository.scrap;

import com.example.boardapi.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long>, ScrapCustomRepository {
//
//    @Query("select s from Scrap s join fetch s.board b where s.member.id = :memberId")
//    List<Scrap> findByMemberId(@Param("memberId") Long memberId);
//
//    @Query("delete from Scrap s where s.board.id = :boardId")
//    @Modifying(clearAutomatically = true)
//    void deleteByBoardId(@Param("boardId") Long boardId);
//
//    @Query("delete from Scrap s where s.member.id = :memberId")
//    @Modifying(clearAutomatically = true)
//    void deleteByMemberId(@Param("memberId") Long memberId);
}
