package com.proxy.controller;

import com.proxy.model.Tuple;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Profile("example")
@RestController
public class ExampleController {

    @Autowired
    ReplyingKafkaTemplate<String, Tuple, Tuple> kafkaTemplate;

    @Value("${kafka.topic.request-topic}")
    String requestTopic;

    @Value("${kafka.topic.requestreply-topic}")
    String requestReplyTopic;

    @ResponseBody
    @GetMapping(value = "/sumA", produces = MediaType.APPLICATION_JSON_VALUE)
    public Tuple sumA(@RequestParam(name = "val") String request) throws InterruptedException, ExecutionException {
        Tuple m = new Tuple();
        int v = Integer.parseInt(request);
        m.setFirstNumber(v);
        m.setSecondNumber(v);
        return doIt(m);
    }

    @ResponseBody
    @GetMapping(value = "/sumB", produces = MediaType.APPLICATION_JSON_VALUE)
    public Tuple sumB(@RequestParam(name = "val") String request) throws InterruptedException, ExecutionException {
        Tuple m = new Tuple();
        int v = Integer.parseInt(request);
        Thread.sleep(v);
        m.setAdditionalProperty("sum", (v / 1000));
        return (m);
    }

    @ResponseBody
    @PostMapping(value = "/sum", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Tuple sum(@RequestBody Tuple request) throws InterruptedException, ExecutionException {
        return doIt(request);
    }

    private Tuple doIt(Tuple request) throws ExecutionException, InterruptedException {
        // create producer record
        ProducerRecord<String, Tuple> record = new ProducerRecord<String, Tuple>(requestTopic, request);
        // set reply topic in header
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
        // post in kafka topic
        RequestReplyFuture<String, Tuple, Tuple> sendAndReceive = kafkaTemplate.sendAndReceive(record);

        // confirm if producer produced successfully
        SendResult<String, Tuple> sendResult = sendAndReceive.getSendFuture().get();

        //print all headers
        sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

        // get consumer record
        ConsumerRecord<String, Tuple> consumerRecord = sendAndReceive.get();
        // return consumer value
        return consumerRecord.value();
    }
}
