package com.proxy.config;

import com.proxy.model.RequestData;
import com.proxy.model.ResponseData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Profile("router")
@Configuration
public class KafkaServerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.requestreply-topic-data}")
    private String requestReplyTopicData;

    @Value("${kafka.consumergroup}")
    private String consumerGroup;

    //server side
    //client side
    @Bean
    public Map<String, Object> producerConfigs3() {
        Map<String, Object> props = new HashMap<>();
        // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    //server side
    //client side
    @Bean
    public Map<String, Object> consumerConfigs3() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
        return props;
    }

    // sever side
    @Bean
    public ProducerFactory<String, ResponseData> producerFactory3() {
        return new DefaultKafkaProducerFactory<>(producerConfigs3());
    }


    // server side
    @Bean
    public KafkaTemplate<String, ResponseData> dataKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory3());
    }


    //server side
    @Bean
    public ConsumerFactory<String, RequestData> dataReplyConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs3(), new StringDeserializer(), new JsonDeserializer<>(RequestData.class));
    }

    //server side
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, RequestData>> kafkaDataListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RequestData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(dataReplyConsumerFactory());
        factory.setReplyTemplate(dataKafkaTemplate());
        return factory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ConsumerFactory consumerFactory2() {
        return new DefaultKafkaConsumerFactory(consumerConfigs3(), new StringDeserializer(), new JsonDeserializer<>(RequestData.class));
    }
}

