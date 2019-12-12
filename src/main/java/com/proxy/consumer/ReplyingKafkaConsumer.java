package com.proxy.consumer;

import com.proxy.model.Tuple;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Profile("example")
@Component
public class ReplyingKafkaConsumer {

    @KafkaListener(topics = "${kafka.topic.request-topic}", containerFactory = "kafkaListenerContainerFactory")
    @SendTo
    public Tuple listen(Tuple request) throws InterruptedException {

        int sum = request.getFirstNumber() + request.getSecondNumber();
        int result = 0;
        for (int i = 0; i < sum; i++) {
            result++;
            Thread.sleep(10);
        }
        request.setAdditionalProperty("sum", result);
        return request;
    }
}
