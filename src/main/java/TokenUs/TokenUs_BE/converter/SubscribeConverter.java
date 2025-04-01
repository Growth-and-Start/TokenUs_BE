package TokenUs.TokenUs_BE.converter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.mapping.Subscribe;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;

@Component
@RequiredArgsConstructor
public class SubscribeConverter {
    public static UserResponseDTO.subscribeResultDTO toResponseDTO(
            Subscribe subscribe, boolean isSubscribed) {
        return UserResponseDTO.subscribeResultDTO
                .builder()
                .subscriberId(subscribe.getSubscriber().getId())
                .targetId(subscribe.getTarget().getId())
                .isSubscribed(isSubscribed)
                .build();
    }
}
