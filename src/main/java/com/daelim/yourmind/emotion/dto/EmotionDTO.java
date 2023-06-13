package com.daelim.yourmind.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionDTO {
    private Long id;
    private Long angry, disgusted, fearful, happy, neutral, sad, surprised;
    private String child;
    private String counselor;
    private LocalDateTime regDate,modDate;
    private String memo;
}
