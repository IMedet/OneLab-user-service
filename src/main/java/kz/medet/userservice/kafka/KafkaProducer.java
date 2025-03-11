package kz.medet.userservice.kafka;

import kz.medet.userservice.dto.CreateProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Object message){
        kafkaTemplate.send("Orders",message);
    }

    public void sendMessage2(Object message){
        kafkaTemplate.send("user.to.order.requests",message);
    }

    public void sendMessage3(CreateProductDto createProductDto){
        kafkaTemplate.send("user.to.order.createProduct", createProductDto);
    }
}
