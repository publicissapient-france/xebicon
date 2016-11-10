package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.service.KubernetesClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class K8SClusterStateListener {

    private static Logger logger = LoggerFactory.getLogger(K8SClusterState.class);

    private final KubernetesClientService kubernetesClientService;

    @Autowired
    public K8SClusterStateListener(KubernetesClientService kubernetesClientService) {
        this.kubernetesClientService = kubernetesClientService;
    }

    @RabbitListener(queues = {K8SClusterStateApp.XEBICON_K8S_LISTENER})
    public void receiveState(Map<String, Object> msg) {
        logger.debug("Receiving message [{}]", msg);

        if (ofNullable(msg.get("type")).orElse("").equals("KEYNOTE_STATE")) {
            Optional<String> payloadValue = ofNullable((Map<String, String>) msg.get("payload")).map(payload -> payload.get("value"));
            if (payloadValue.orElse("").equals("KEYNOTE_START")) {
                kubernetesClientService.deletePodsWithLabel("app", "bar-app");
            }
        }

    }


}
