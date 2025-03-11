package kz.medet.userservice.util;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

public class MixedDeserializer<T> implements Deserializer<T> {

    public MixedDeserializer() {
        System.out.println("✅ MixedDeserializer инициализирован");
    }

    private final JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>();
    private final StringDeserializer stringDeserializer = new StringDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        jsonDeserializer.configure(configs, isKey);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            return jsonDeserializer.deserialize(topic, data);
        } catch (Exception e) {
            return (T) stringDeserializer.deserialize(topic, data);
        }
    }

    @Override
    public void close() {
        jsonDeserializer.close();
        stringDeserializer.close();
    }
}

