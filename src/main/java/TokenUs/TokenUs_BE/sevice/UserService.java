package TokenUs.TokenUs_BE.sevice;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.handler.GeneralHandler;
import TokenUs.TokenUs_BE.converter.UserConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.dto.UserRequestDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    // 회원가입
    public User joinUser(UserRequestDTO.joinRequestDTO request) {

        // 해당 email의 유저가 이미 존재
        if (userRepository.findByEmail((request.getEmail())).isPresent()) {
            throw new GeneralHandler(ErrorStatus.USER_ALREADY_EXIST);
        }

        User newUser = userConverter.toUser(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(newUser);
    }
}
