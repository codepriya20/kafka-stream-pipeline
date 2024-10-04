package com.example.kafkastreampipeline.service;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class KafkaStreamsService {

    private final KStream<String, String> kStream;

    private final JdbcTemplate jdbcTemplate;
    private final LinkedBlockingQueue<KeyValue<String, String>> queue;
    private static final int BATCH_SIZE = 100;


    @Autowired
    public KafkaStreamsService(KStream<String, String> kStream, DataSource dataSource) {
        this.kStream = kStream;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.queue = new LinkedBlockingQueue<>();
    }

    @PostConstruct
    public void startProcessing() {
        kStream.foreach((key, value) -> {
            key = UUID.randomUUID().toString();
            System.out.println("Through Stream Processing Consumed message: Key = " + key + ", Value = " + value);
            queue.offer(new KeyValue<>(key, value));
            if (queue.size() >= BATCH_SIZE) {
                insertBatch();
            }
        });
    }


    @Scheduled(fixedRateString = "${scheduled.fixedRate:5000}")
    public void scheduledInsert() {
        if (!queue.isEmpty()) {
            insertBatch();
        }
    }

    private void insertBatch() {
        List<KeyValue<String, String>> batch = new ArrayList<>();
        queue.drainTo(batch, BATCH_SIZE);

        if (!batch.isEmpty()) {
            List<Object[]> parameters = new ArrayList<>();
            for (KeyValue<String, String> entry : batch) {
                parameters.add(new Object[]{entry.key, entry.value});
            }

            jdbcTemplate.batchUpdate(
                    "INSERT INTO public.my_table (id, value) VALUES (?, ?)",
                    parameters
            );
        }
    }

    private static class KeyValue<K, V> {
        private final K key;
        private final V value;

        public KeyValue(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
