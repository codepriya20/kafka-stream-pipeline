package com.example.kafkastreampipeline.config;

import com.example.kafkastreampipeline.constants.KafkaConstants;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${kafka.streams.topics}")
    private String[] kafkaStreamTopics;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.kafka.streams.serde}")
    private String serde;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String truststoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.password}")
    private String truststorePassword;

    @Bean(name = "defaultKafkaStreamsConfig")
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(KafkaConstants.BOOTSTRAP_SERVERS, bootStrapServers);
        props.put(KafkaConstants.APPLICATION_ID, applicationName);
        props.put(KafkaConstants.DEFAULT_KEY_SERDE, serde);
        props.put(KafkaConstants.DEFAULT_VALUE_SERDE, serde);

        // SSL Settings
        //props.put(KafkaConstants.SECURITY_PROTOCOL, securityProtocol);
        //props.put(KafkaConstants.SSL_TRUSTSTORE_LOCATION, truststoreLocation);
        //props.put(KafkaConstants.SSL_TRUSTSTORE_PASSWORD, truststorePassword);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> streams = null;
        for (String topicName : kafkaStreamTopics) {
            KStream<String, String> topicStream = streamsBuilder.stream(topicName);
            streams = (streams == null) ? topicStream : streams.merge(topicStream);
        }
        return streams;
    }

}