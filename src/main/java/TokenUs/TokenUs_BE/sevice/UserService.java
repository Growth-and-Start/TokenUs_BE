package TokenUs.TokenUs_BE.sevice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.converter.UserConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;
import TokenUs.TokenUs_BE.repository.SubscribeRepository;
import TokenUs.TokenUs_BE.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;

    public List<UserResponseDTO.searchResultDTO> searchUsers(String searchFor, Long currentUserId) {
        if (searchFor == null || searchFor.trim().isEmpty()) {
            return Collections.emptyList(); // 검색어가 없으면 빈 리스트 반환
        }

        List<User> matchedUsers =
                userRepository.findByNicknameContainingIgnoreCase(searchFor.trim());

        return matchedUsers.stream()
                .map(
                        user -> {
                            boolean isSubscribed = false;
                            if (currentUserId != null) {
                                isSubscribed =
                                        subscribeRepository.existsBySubscriberIdAndTargetId(
                                                currentUserId, user.getId());
                            }
                            return UserConverter.toSearchResultDTO(user, isSubscribed);
                        })
                .collect(Collectors.toList());
    }
}
