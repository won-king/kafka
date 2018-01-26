package com.wonking.projects.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

/**
 * Created by kewangk on 2017/12/28.
 */
public class KafkaConsumerDemo {
    public static void main(String[] args){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        //props.put("fetch.min.bytes", 1000);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //commitAuto(props);
        commitManual(props, false);
    }

    private static void commitAuto(Properties properties){
        properties.put("enable.auto.commit", "true");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("test", "my-replicated-topic", "multi-partition-topic"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                System.out.printf("offset = %d, partition = %d, key = %s, value = %s%n",
                        record.offset(), record.partition(), record.key(), record.value());
            }
        }
    }

    //手动提交消息的offset
    private static void commitManual(Properties properties, boolean flag){
        KafkaConsumer<String,String> consumer=new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("multi-partition-topic"));
        try{
            while (true){
                ConsumerRecords<String,String> records=consumer.poll(Long.MAX_VALUE);
                System.out.println("-----poll begin-----");
                //这里是将poll出的一批消息记录按照内部排序来消费的
                consume(consumer, records, flag);
                /*for(ConsumerRecord<String,String> record:records){
                    try{
                        System.out.printf("offset = %d, partition = %d, key = %s, value = %s%n",
                                record.offset(), record.partition(), record.key(), record.value());
                        if(flag){
                            throw new RuntimeException("consumer exception");
                        }
                        TopicPartition partition=new TopicPartition(record.topic(), record.partition());
                        OffsetAndMetadata offset=new OffsetAndMetadata(record.offset()+1);
                        consumer.commitSync(Collections.singletonMap(partition, offset));
                    }catch (Exception e){
                        System.out.println("consumer exception.");
                    }
                }*/
                //这里是将poll出的一批消息按partition分组来消费的
                /*for(TopicPartition partition: records.partitions()){
                    //records.records()
                    List<ConsumerRecord<String,String>> partitionRecords=records.records(partition);
                    consume(consumer, partitionRecords, flag);
                }*/
                System.out.println("-----poll end-----");
            }
        }finally {
            consumer.close();
        }
    }

    private static <K,V> void consume(KafkaConsumer<K,V> consumer, Iterable<ConsumerRecord<K, V>> iterable, boolean flag){
        for(ConsumerRecord<K,V> record : iterable){
            try{
                System.out.printf("offset = %d, partition = %d, key = %s, value = %s%n",
                        record.offset(), record.partition(), record.key(), record.value());
                if(flag){
                    throw new RuntimeException("consumer exception");
                }
                TopicPartition partition=new TopicPartition(record.topic(), record.partition());
                OffsetAndMetadata offset=new OffsetAndMetadata(record.offset()+1);
                consumer.commitSync(Collections.singletonMap(partition, offset));
            }catch (Exception e){
                System.out.println("consumer exception.");
            }
        }
    }

}
