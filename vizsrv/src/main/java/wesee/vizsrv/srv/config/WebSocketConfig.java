package wesee.vizsrv.srv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import wesee.vizsrv.srv.websocket.GraphOnlineWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/graphOnline");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new GraphOnlineWebSocketHandler();
    }

}