package TokenUs.TokenUs_BE.sevice;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.handler.GeneralHandler;
import TokenUs.TokenUs_BE.converter.VideoConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final VideoConverter videoconverter;
    private final VideoConverter videoConverter;

    public Video saveDetail(VideoRequestDTO.videoDetailRequestDTO request, User user) {

        // 해당 file_url의 영상이 이미 업로드 되었다면
        if (videoRepository.findByFileUrl((request.getFileUrl())).isPresent()) {
            throw new GeneralHandler(ErrorStatus.VIDEO_ALREADY_EXIST);
        }

        Video newVideo = videoConverter.toVideo(request, user);

        return videoRepository.save(newVideo);
    }
}
