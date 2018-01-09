package wesee.vizsrv.srv.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import wesee.vizsrv.graph.models.graphOnline.GraphOnlineDataConvertor;
import wesee.vizsrv.repository.NotifierRepository;
import wesee.vizsrv.repository.live_notificator.INewDataNotifier;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class GraphOnlineWebSocketHandler implements WebSocketHandler, INewDataNotifier {
    private boolean _isActive = false;
    private WebSocketSession session;
    private static Semaphore sendMessagePermission = new Semaphore(1);
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        System.out.print("Connection created");
        session = webSocketSession;
        NotifierRepository.getSingleRepository().subscribe(this);
        _isActive = true;
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        NotifierRepository.getSingleRepository().unsubscribe(this);
        System.out.print("Connection closed");
        _isActive = false;
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void onDataReceived(RepositorySaveResult saveResult) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(GraphOnlineDataConvertor.fromRepositorySaveResult(saveResult));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            if (sendMessagePermission.tryAcquire())
            {
                session.sendMessage(new TextMessage(json));
                sendMessagePermission.release();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isActive() {
        return _isActive;
    }
}
