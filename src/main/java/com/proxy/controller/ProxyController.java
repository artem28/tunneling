package com.proxy.controller;

import com.proxy.model.RequestData;
import com.proxy.model.ResponseData;
import jdk.internal.joptsimple.internal.Strings;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Profile("entrance")
@RestController
public class ProxyController {

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    @Autowired
    ReplyingKafkaTemplate<String, RequestData, ResponseData> dataKafkaTemplate;

    @Value("${kafka.topic.request-topic-data}")
    String requestTopicData;

    @Value("${kafka.topic.requestreply-topic-data}")
    String requestReplyTopicData;

    @Value("${tunneling.target.server}")
    String serverName;

    @ResponseBody
    @RequestMapping(value = "/proxy/**")
    public ResponseEntity proxy(@RequestHeader HttpHeaders headers, Reader request, HttpServletRequest httpServletRequest) throws InterruptedException, ExecutionException {
        RequestData requestData = getRequestData(headers, request, httpServletRequest);
        ResponseData data = proxy(requestData);
        return new ResponseEntity<>(
                data.getData(),
                data.getHeaders(),
                HttpStatus.valueOf(data.getStatus()));
    }

    private RequestData getRequestData(HttpHeaders headers, Reader request, HttpServletRequest httpServletRequest) {
        RequestData requestData = new RequestData();
        BufferedReader br = new BufferedReader(request);
        String s = br.lines().collect(Collectors.joining());
        requestData.setData(s);
        LinkedMultiValueMap<String, String> hdrs = new LinkedMultiValueMap<>();
        hdrs.addAll(headers);
        requestData.setHeaders(hdrs);
        String url = httpServletRequest.getRequestURL().toString();
        String [] urlParts = url.split("/proxy");
        urlParts[0] = defineServer(urlParts[0]);
        url = String.join("", urlParts);
        String query = httpServletRequest.getQueryString();
        url = query == null ? url : url + "?" + query;
        requestData.setURL(url);
        String method = httpServletRequest.getMethod();
        requestData.setMethod(method);
        return requestData;
    }

    private String defineServer(String urlPart) {
        if(Strings.isNullOrEmpty(serverName)){
            return urlPart;
        }
        if (urlPart.startsWith(HTTP)){
            return HTTP+serverName;
        }
        if (urlPart.startsWith(HTTPS)){
            return HTTPS+serverName;
        }
        return HTTP+serverName;
    }

    private ResponseData proxy(RequestData request) throws ExecutionException, InterruptedException {
        // create producer record
        ProducerRecord<String, RequestData> record = new ProducerRecord<>(requestTopicData, request);
        // set reply topic in header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopicData.getBytes()));
        // post in kafka topic
        RequestReplyFuture<String, RequestData, ResponseData> sendAndReceive = dataKafkaTemplate.sendAndReceive(record);

        // confirm if producer produced successfully
        SendResult<String, RequestData> sendResult = sendAndReceive.getSendFuture().get();

        //print all headers
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

        // get consumer record
        ConsumerRecord<String, ResponseData> consumerRecord = sendAndReceive.get();
        // return consumer value
        return consumerRecord.value();
    }
}
