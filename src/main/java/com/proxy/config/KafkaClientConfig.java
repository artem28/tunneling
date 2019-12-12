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
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Profile("entrance")
@Configuration
public class KafkaClientConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.requestreply-topic-data}")
    private String requestReplyTopicData;

    @Value("${kafka.consumergroup}")
    private String consumerGroup;

    //client side
    @Bean
    public Map<String, Object> producerConfigs2() {
        Map<String, Object> props = new HashMap<>();
        // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    //client side
    @Bean
    public Map<String, Object> consumerConfigs2() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
        return props;
    }

    //client side
    @Bean
    public ConsumerFactory<String, ResponseData> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs2(), new StringDeserializer(), new JsonDeserializer<>(ResponseData.class));
    }
    //client side
    @Bean
    public ConsumerFactory consumerFactory2() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs2(), new StringDeserializer(), new JsonDeserializer<>(ResponseData.class));
    }

    //client side
    @Bean
    public ProducerFactory<String, RequestData> producerFactory2() {
        return new DefaultKafkaProducerFactory<>(producerConfigs2());
    }

    //client side
    @Bean
    public ReplyingKafkaTemplate<String, RequestData, ResponseData> replyDataKafkaTemplate(ProducerFactory<String, RequestData> pf, KafkaMessageListenerContainer<String, ResponseData> container) {
        return new ReplyingKafkaTemplate<>(pf, container);

    }

    //client side
    @Bean
    public KafkaMessageListenerContainer<String, ResponseData> replyDataContainer(ConsumerFactory<String, ResponseData> cf) {
        ContainerProperties containerProperties = new ContainerProperties(requestReplyTopicData);
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ResponseData>> kafkaDataListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, ResponseData> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }
}

