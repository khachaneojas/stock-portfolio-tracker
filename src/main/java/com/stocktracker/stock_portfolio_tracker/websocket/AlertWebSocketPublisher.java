package com.stocktracker.stock_portfolio_tracker.websocket;

import com.stocktracker.stock_portfolio_tracker.websocket.dto.AlertMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishAlert(AlertMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/alerts/" + message.userId(),
                message
        );
    }

}
