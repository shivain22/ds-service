package com.ainnotate.aidas.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootStrapServers = "localhost:9092";

    private Map<String, String> consumer = new HashMap<>();

    private Map<String, String> producer = new HashMap<>();

    public String getBootStrapServers() {
        return bootStrapServers;
    }

    public void setBootStrapServers(String bootStrapServers) {
        this.bootStrapServers = bootStrapServers;
    }

    public Map<String, Object> getConsumerProps() {
        Map<String, Object> property = new HashMap<>(this.consumer);
        if (!property.containsKey("bootstrap.servers")) {
            property.put("bootstrap.servers", this.bootStrapServers);
        }
        return property;
    }

    public void setConsumer(Map<String, String> consumer) {
        this.consumer = consumer;
    }

    public Map<String, Object> getProducerProps() {
        Map<String, Object> property = new HashMap<>(this.producer);
        if (!property.containsKey("bootstrap.servers")) {
            property.put("bootstrap.servers", this.bootStrapServers);
        }
        return property;
    }

    public void setProducer(Map<String, String> producer) {
        this.producer = producer;
    }
}
