package consumer;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personObject.Person;
import personObject.PersonDeserializer;

public class Consumer {
    private final KafkaConsumer<Integer,Person> mConsumer;
    private final Logger mLogger=LoggerFactory.getLogger(Consumer.class);
    

    public Properties consumerProps(String bootstrapServer, String groupId, String offsetConfig){
        String keyDeserializer=IntegerDeserializer.class.getName();
        String valueDeserializer=PersonDeserializer.class.getName();
        Properties props=new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetConfig);
        return props;
    }

    public Consumer(String bootstrapServer, String groupId, String offsetConfig){
        Properties props=consumerProps(bootstrapServer,groupId,offsetConfig);
        mConsumer=new KafkaConsumer<>(props);
        mLogger.info("Consumer initialized");
    }

    public void run(){
        try{
            while(true){
                mLogger.info("Polling");

                ConsumerRecords<Integer,Person> records=mConsumer.poll(Duration.ofMillis(100));
                
                for(ConsumerRecord<Integer,Person> record : records){
                    mLogger.info("Key: "+record.key()+", Value: "+record.value());
                    mLogger.info("Partition: "+record.partition()+ ", Offset: "+record.offset());

                }
            }
        }catch(WakeupException e){
            mLogger.info("Received shutdown signal");
        }finally{
            
        }
    }
}
