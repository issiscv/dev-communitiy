package com.example.boardapi.repository;

import com.example.boardapi.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Query("select s from Scrap s join fetch s.board b where s.member.id = :memberId")
    List<Scrap> findByMemberId(@Param("memberId") Long memberId);

}
