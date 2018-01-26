package com.wonking.projects.kafka;

import com.wonking.projects.kafka.annotation.Destroyed;
import org.apache.kafka.clients.producer.*;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by kewangk on 2017/12/13.
 */
public class KafkaProducerDemo {

    @Destroyed
    public String testAnnotation(){
        return "hehe";
    }

    public static void main(String[] args){

        //这相当于properties配置文件中的配置信息
        Properties props=new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        String topic1="my-replicated-topic";
        String multiPartitionTopic="multi-partition-topic";
        Producer<String,String> producer= new KafkaProducer<>(props);
        for(int i=0;i<10;++i){
            Future future=producer.send(new ProducerRecord<>(multiPartitionTopic, null, "message "+i), new CallBackTask());
            try {
                future.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("will close");
        producer.close(2, TimeUnit.SECONDS);
        System.out.println("already close");
    }

    private static class CallBackTask implements Callback{

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if(e!=null){
                e.printStackTrace();
                return;
            }
            System.out.println("time->"+new Date(recordMetadata.timestamp()).toString());
            System.out.println("topic->"+recordMetadata.topic());
            System.out.println("partition->"+recordMetadata.partition());
            System.out.println("offset->"+recordMetadata.offset());
        }
    }
}
