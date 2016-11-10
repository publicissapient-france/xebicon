package fr.xebia.xebicon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.xebia.xebicon.model.K8SClusterState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class K8SClusterStateConsumer implements Consumer<K8SClusterState> {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateConsumer.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public K8SClusterStateConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void accept(K8SClusterState k8SClusterState) {
        try {
            logger.debug("Will send the state cluster [{}]", (new ObjectMapper()).writeValueAsString(k8SClusterState));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        rabbitTemplate.convertAndSend(k8SClusterState);
    }
}
