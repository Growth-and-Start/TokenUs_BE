package TokenUs.TokenUs_BE.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import TokenUs.TokenUs_BE.repository.NftRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Service
public class FlaskService {

    private final RestTemplate restTemplate;
    private final String flaskUrl;
    private final NftRepository nftRepository;
    private final VideoRepository videoRepository;

    public FlaskService(
            RestTemplate restTemplate,
            @Value("${flask.url}") String flaskUrl,
            NftRepository nftRepository,
            VideoRepository videoRepository) {
        this.restTemplate = restTemplate;
        this.flaskUrl = flaskUrl;
        this.videoRepository = videoRepository;
        this.nftRepository = nftRepository;
    }

    public String requestSimilarityCheck(String fileUrl) {

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("file_url", fileUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Flask 응답 본문: " + response.getBody());
            return response.getBody();
        } else {
            System.err.println("Flask 서버와 통신 실패: " + response.getStatusCode());
            return null;
        }
    }

    public void sendSimilarityRequestAsync(String fileUrl) {

        String url = flaskUrl + "/download";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("file_url", fileUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, requestEntity, String.class);
            System.out.println(" Flask 응답 상태: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Flask 요청 실패: " + e.getMessage());
        }
    }

    public String getFaissInfo() {
        String url = flaskUrl + "/faiss_info";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println(" FAISS info 요청 실패: " + e.getMessage());
            return null;
        }
    }

    public String resetFaissIndex() {
        String url = flaskUrl + "/reset_faiss_index";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println(" FAISS 초기화 요청 실패: " + e.getMessage());
            return null;
        }
    }

    public void enrichWithVideoData(VideoResponseDTO.similarityCheckResultDTO result) {
        if (result.getSimilarVideoUrl() == null || result.getSimilarVideoUrl().isEmpty()) return;

        videoRepository
                .findByFileUrl(result.getSimilarVideoUrl())
                .ifPresent(
                        video -> {
                            result.setSimilarVideoId(video.getId());
                            List<String> tokenIds =
                                    nftRepository.findTokenIdsByVideoId(video.getId());
                            result.setTokenIds(tokenIds);
                        });
    }
}
