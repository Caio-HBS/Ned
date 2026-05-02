package io.github.caiohbs.authentication.publisher;

import io.github.caiohbs.authentication.model.GenericEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SendEmailPublisher {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${NED.config.kafka.topics.emails}")
    private String TOPIC;

    public void sendEmail(GenericEmail email) {
        try {
            kafkaTemplate.send(TOPIC, "email", objectMapper.writeValueAsString(email));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
