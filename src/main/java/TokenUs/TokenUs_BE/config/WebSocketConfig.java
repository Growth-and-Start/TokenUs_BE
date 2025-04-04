package TokenUs.TokenUs_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // ✅ WebSocket 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // ✅ 메시지를 보낼 경로
        config.setApplicationDestinationPrefixes("/app"); // ✅ 클라이언트가 메시지를 보낼 때 접두사
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ WebSocket 엔드포인트
                .setAllowedOriginPatterns("*") // 배포
                //                .setAllowedOriginPatterns("http://localhost:5500") // 개발
                .withSockJS(); // ✅ SockJS 지원
    }
}
