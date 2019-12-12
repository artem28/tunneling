package com.proxy.consumer;

import com.proxy.model.RequestData;
import com.proxy.model.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Profile("router")
@Component
public class ProxyKafkaConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "${kafka.topic.request-topic-data}", containerFactory = "kafkaDataListenerContainerFactory")
    @SendTo
    public ResponseData listenData(RequestData request) throws InterruptedException {
        try {
            return getResponseData(request);
        } catch (Exception e) {
            return getResponseDataError(e);
        }
    }

    private ResponseData getResponseDataError(Exception e) {
        ResponseData response = new ResponseData();
        LinkedMultiValueMap<String, String> hd = new LinkedMultiValueMap<>();
        hd.add("Content-Type", "text/html;charset=UTF-8");
        response.setHeaders(hd);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setData(e.getMessage());
        return response;
    }

    private ResponseData getResponseData(RequestData request) {
        //headers
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(request.getHeaders());
        HttpEntity<String> entity = new HttpEntity<>(request.getData(), headers);
        //exchange
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                request.getURL(),
                HttpMethod.resolve(request.getMethod()),
                entity,
                String.class
        );
        //prepare response
        ResponseData response = new ResponseData();
        response.setHeaders(new LinkedMultiValueMap<>(responseEntity.getHeaders()));
        response.setStatus(responseEntity.getStatusCode().value());
        response.setData(responseEntity.getBody());
        return response;
    }

    private String showMap(LinkedMultiValueMap<String, String> map) {
        return map.entrySet().stream().map(k -> k.getKey() + ": " + k.getValue().stream()
                .collect(Collectors.joining(","))).collect(Collectors.joining("\n"));
    }
}
