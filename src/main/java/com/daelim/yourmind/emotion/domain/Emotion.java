package com.daelim.yourmind.emotion.domain;

import com.daelim.yourmind.common.domain.BaseTimeEntity;
import com.daelim.yourmind.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emotion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long angry;

    @Column(nullable = false)
    private Long disgusted;

    @Column(nullable = false)
    private Long fearful;

    @Column(nullable = false)
    private Long happy;

    @Column(nullable = false)
    private Long neutral;

    @Column(nullable = false)
    private Long sad;

    @Column(nullable = false)
    private Long surprised;

    @Column(length = 1500)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User child;

    @ManyToOne(fetch = FetchType.LAZY)
    private User counselor;
}
