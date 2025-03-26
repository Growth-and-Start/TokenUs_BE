package TokenUs.TokenUs_BE.sevice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlaskService {

    private final RestTemplate restTemplate;
    private final String flaskUrl;

    public FlaskService(RestTemplate restTemplate, @Value("${flask.url}") String flaskUrl) {
        this.restTemplate = restTemplate;
        this.flaskUrl = flaskUrl;
    }

    public String requestSimilarityCheck(String fileUrl) {

        // 1. requestBody 설정
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("file_url", fileUrl);

        // 2. header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. requestBody+Header
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 4. 요청 보냄
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
}
