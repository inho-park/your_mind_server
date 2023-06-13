package com.daelim.yourmind.emotion.service;

import com.daelim.yourmind.emotion.domain.Emotion;
import com.daelim.yourmind.emotion.repo.EmotionRepository;
import com.daelim.yourmind.emotion.dto.*;
import com.daelim.yourmind.user.domain.User;
import com.daelim.yourmind.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;


@Log4j2
@Service
@RequiredArgsConstructor
public class EmotionServiceImpl implements EmotionService {

    private final EmotionRepository emotionRepository;
    private final UserRepository userRepository;

    @Override
    public StatusDTO saveEmotion(EmotionDTO emotionDTO) {
        try {
            Optional<User> childOption = userRepository.findByUsername(emotionDTO.getChild());
            Optional<User> counselorOption = userRepository.findByUsername(emotionDTO.getCounselor());
            if (childOption.isPresent() && counselorOption.isPresent()) {
                User child = childOption.get();
                User counselor = counselorOption.get();
                Emotion emotion = dtoToEntity(emotionDTO, child, counselor);
                emotionRepository.save(emotion);
                StatusDTO statusDTO = StatusDTO.builder().status("success").build();
                return statusDTO;
            } else {
                throw new RuntimeException("This account doesn't exist");
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    @Override
    public EmotionDTO getEmotion(Long emotionId, String username) {
        Optional<Emotion> emotionOption = emotionRepository.findById(emotionId);
        Emotion emotion;
        if (emotionOption.isPresent()) {
            emotion = emotionOption.get();
        } else {
            throw new RuntimeException("Not found");
        }
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            if (emotion.getChild().getUsername().equals(username) || emotion.getCounselor().getUsername().equals(username)) {
                User child = emotion.getChild();
                User counselor = emotion.getCounselor();
                return entityToDTO(emotion, child, counselor);
            } else {
                throw new RuntimeException("No permission");
            }
        } else {
            throw new RuntimeException("This account doesn't exist");
        }
    }

    @Override
    public PageResultDTO<EmotionDTO, Object[]> getEmotions(PageRequestDTO pageRequestDTO) {
        log.info(pageRequestDTO);
        Function<Object[], EmotionDTO> fn = (
                entity -> entityToDTO(
                        (Emotion)entity[0],
                        (User)entity[1],
                        (User)entity[2])
        );
        Page<Object[]> result;
        String username = pageRequestDTO.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            if (isCounselor(username)) {
                result = emotionRepository.getEmotionAndChildAndCounselorByCounselor(
                    pageRequestDTO.getPageable(Sort.by("id").descending()),
                    userRepository.findByUsername(username).get().getId()
                );
            } else {
                result = emotionRepository.getEmotionAndChildAndCounselorByChild(
                    pageRequestDTO.getPageable(Sort.by("id").descending()),
                    userRepository.findByUsername(username).get().getId()
                );
            }
            return new PageResultDTO<>(result, fn);
        } else {
            throw new RuntimeException("This account doesn't exist");
        }
    }

    @Override
    public StatusDTO deleteEmotion(Long emotionId, String username) {
        Optional<Emotion> emotionOption = emotionRepository.findById(emotionId);
        if (emotionOption.isPresent()) {
            Emotion emotion = emotionRepository.findById(emotionId).get();
            if (emotion.getChild().getUsername().equals(username) || emotion.getCounselor().getUsername().equals(username)) {
                emotionRepository.deleteById(emotionId);
                StatusDTO dto = StatusDTO.builder().status("success").build();
                return dto;
            } else {
                throw new RuntimeException("No permission");
            }
        } else {
            throw new RuntimeException("This account doesn't exist");
        }
    }

    public boolean isCounselor(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.get().isCounselor();
    };

}
