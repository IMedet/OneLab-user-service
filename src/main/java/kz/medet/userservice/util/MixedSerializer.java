package kz.medet.userservice.util;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

public class MixedSerializer implements Serializer<Object> {

    private final JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
    private final StringSerializer stringSerializer = new StringSerializer();

    public MixedSerializer() {
        System.out.println("✅ MixedSerializer инициализирован");
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        jsonSerializer.configure(configs, isKey);
        stringSerializer.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        System.out.println("🔹 MixedSerializer сериализует объект: " + data.getClass().getName());
        if (data instanceof String) {
            return stringSerializer.serialize(topic, (String) data);
        }
        return jsonSerializer.serialize(topic, data);
    }

    @Override
    public void close() {
        jsonSerializer.close();
        stringSerializer.close();
    }
}
