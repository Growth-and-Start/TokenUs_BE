package TokenUs.TokenUs_BE.sevice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlaskService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendFileUrlToFlask(String fileUrl) {
        String flaskUrl = "http://127.0.0.1:5000/download"; // 로컬 개발 환경
        //        String flaskUrl = "http://tokenus-flask:5000/download"; // ec2환경

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("file_url", fileUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Flask 서버로 URL 전송 성공: " + fileUrl);
        } else {
            System.err.println("Flask 서버로 URL 전송 실패: " + response.getStatusCode());
        }
    }
}
