package guru.springframework.sfgjms.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 5000)
    public void sendMessage() {
        //System.out.println("Sending a message");
        HelloWorldMessage message =
            HelloWorldMessage.builder().id(UUID.randomUUID()).message("Hello World!").build();
        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
    }

    @Scheduled(fixedRate = 5000)
    public void sendReceiveMessage() throws JMSException {
        //System.out.println("Sending a message");
        HelloWorldMessage message =
            HelloWorldMessage.builder().id(UUID.randomUUID()).message("Hello Send/Rec World!").build();
        Message recMessage = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, session -> {
            Message helloMessage;
            try {
                helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                helloMessage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");
            } catch (Exception e) {
                throw new JMSException(e.getMessage());
            }
            return helloMessage;
        });
        System.out.println(recMessage.getBody(String.class));
    }
}
