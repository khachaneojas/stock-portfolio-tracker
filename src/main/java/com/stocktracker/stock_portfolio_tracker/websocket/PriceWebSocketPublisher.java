package com.stocktracker.stock_portfolio_tracker.websocket;

import com.stocktracker.stock_portfolio_tracker.websocket.dto.PriceUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishPriceUpdate(PriceUpdateMessage message) {
        messagingTemplate.convertAndSend("/topic/prices", message);
        messagingTemplate.convertAndSend("/topic/prices/" + message.symbol(), message);
    }

}
