package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SClusterState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class K8SClusterStatePollingBridge {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStatePollingBridge.class);

    private Consumer<K8SClusterState> clusterStateConsumer;
    private Supplier<K8SClusterState> clusterStateSupplier;

    @Autowired
    public K8SClusterStatePollingBridge(Consumer<K8SClusterState> clusterStateConsumer, Supplier<K8SClusterState> clusterStateSupplier) {
        this.clusterStateConsumer = clusterStateConsumer;
        this.clusterStateSupplier = clusterStateSupplier;
    }

    @Scheduled(fixedRateString = "${k8s.pollingInterval}")
    public void scan() {
        logger.debug("Fetching cluster state");
        K8SClusterState state = clusterStateSupplier.get();
        logger.debug("Got cluster state [{}]. Will send it to consumer", state);
        clusterStateConsumer.accept(state);
    }
}
