package producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
// import org.apache.kafka.common.serialization.BytesSerializer;
// import org.apache.kafka.common.serialization.ByteBufferSerializer;
// import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import personObject.Person;
import personObject.PersonSerializer;





public class Producer {
    private final KafkaProducer<Integer,Person> mProducer;
    private final Logger mLogger=LoggerFactory.getLogger(Producer.class);

    private Properties producerProps(String bootstrapServer){
        String keySerializer=IntegerSerializer.class.getName();
        String valueSerializer=PersonSerializer.class.getName();
        Properties props=new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return props;
    };


    public Producer (String bootstrapServer){
        Properties props=producerProps(bootstrapServer);
        mProducer=new KafkaProducer<>(props);
        mLogger.info("Producer initialized");
    }

    public void put_sync(String topic, int key, Person value) throws ExecutionException,InterruptedException{
        mLogger.info("Put value: "+value +", for key: "+key);
        ProducerRecord<Integer, Person> record=new ProducerRecord<>(topic,key,value);

        mProducer.send(record, (RecordMetadata,e)->{
            if(e!=null){
                mLogger.error("Error while producing",e);
                return;
            }
            mLogger.info("Received new meta. \n" +
                    "Topic: "+RecordMetadata.topic() +"\n"+
                    "Partition: "+RecordMetadata.partition()+"\n"+
                    "Offset: "+RecordMetadata.offset()+"/n"+
                    "Timestamp: "+RecordMetadata.timestamp());
        }).get();
    }

    public void put_async(String topic, int key, Person value) throws ExecutionException,InterruptedException{
        mLogger.info("Put value: "+value +", for key: "+key);
        ProducerRecord<Integer, Person> record=new ProducerRecord<>(topic,key,value);

        mProducer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata,Exception e){
                if(e==null){
                    mLogger.info("Received new meta. \n" +
                    "Topic: "+metadata.topic() +"\n"+
                    "Partition: "+metadata.partition()+"\n"+
                    "Offset: "+metadata.offset()+"/n"+
                    "Timestamp: "+metadata.timestamp());
                }else{
                    mLogger.error("Error while producing",e);
                }
            }
        });
        
    }
    public void close(){
        mLogger.info("Closing producer's connection");
        mProducer.close();
    }
}
