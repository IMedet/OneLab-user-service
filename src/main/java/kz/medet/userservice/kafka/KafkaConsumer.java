package kz.medet.userservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.userservice.dto.OrderResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KafkaConsumer {

    Map<Long, OrderResponse> orderResponses = new ConcurrentHashMap<>();

    BlockingQueue blockingQueue;

    ObjectMapper objectMapper;


    @KafkaListener(topics = "OrderResponse", groupId = "my_consumer")
    public void listen(String message) {
        log.info("Message: {}", message);
        blockingQueue.offer(message);
    }


    @KafkaListener(topics = "order.to.user.responses", groupId = "user-group")
    public void listenOrderResponse(String response) {
        log.info("Message: {}", response);

        try {
            OrderResponse orderResponse = objectMapper.readValue(response, OrderResponse.class);
            blockingQueue.offer(response);
        }catch (Exception e){
            log.error("Error deserializing OrderResponse: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order.to.user.responsesProductCreated", groupId = "user-group")
    public void listenOrderResponseProductCreated(String response) {
        log.info("Message: {}", response);

        try {
            blockingQueue.offer(response);
        }catch (Exception e){
            log.error("Error deserializing OrderResponse: {}", e.getMessage());
        }
    }



    public OrderResponse getOrderResponse(String response){
        try {
            return objectMapper.readValue(response, OrderResponse.class);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        return null;
    }
}
