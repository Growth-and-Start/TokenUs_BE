package TokenUs.TokenUs_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TokenUs.TokenUs_BE.service.FlaskService;

@RestController
@RequestMapping("/flask")
public class FlaskController {
    private final FlaskService flaskService;

    public FlaskController(FlaskService flaskService) {
        this.flaskService = flaskService;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getFaissInfo() {
        String result = flaskService.getFaissInfo();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetFaissIndex() {
        String result = flaskService.resetFaissIndex();
        return ResponseEntity.ok(result);
    }
}
