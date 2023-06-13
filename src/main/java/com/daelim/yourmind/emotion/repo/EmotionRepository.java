package com.daelim.yourmind.emotion.repo;

import com.daelim.yourmind.emotion.domain.Emotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmotionRepository extends JpaRepository<Emotion, Long>{
    @Query(
            "SELECT e, e.counselor, e.child " +
            "FROM Emotion e " +
            "WHERE e.counselor.id=:id"
    )
    Page<Object[]> getEmotionAndChildAndCounselorByCounselor(Pageable pageable, @Param("id") Long id);

    @Query(
            "SELECT e, e.counselor, e.child " +
            "FROM Emotion e " +
            "WHERE e.child.id=:id"
    )
    Page<Object[]> getEmotionAndChildAndCounselorByChild(Pageable pageable, @Param("id") Long id);
}
