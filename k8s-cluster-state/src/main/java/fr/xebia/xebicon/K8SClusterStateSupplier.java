package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SClusterState;
import fr.xebia.xebicon.model.K8SNode;
import fr.xebia.xebicon.model.K8SNodeType;
import fr.xebia.xebicon.service.KubernetesClientService;
import io.fabric8.zjsonpatch.internal.guava.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class K8SClusterStateSupplier implements Supplier<K8SClusterState> {

    private static final Logger logger = LoggerFactory.getLogger(K8SClusterStateSupplier.class);

    private final KubernetesClientService kubernetesClientService;

    @Autowired
    public K8SClusterStateSupplier(KubernetesClientService kubernetesClientService) {
        this.kubernetesClientService = kubernetesClientService;
    }

    @Override
    public K8SClusterState get() {
        logger.debug("Will fetch the cluster state");

        List<K8SNode> cloudNodes = findCloudNode();
        List<K8SNode> localNodes = findLocalNode();

        K8SNode cloudNode = mergeNodes(cloudNodes, K8SNodeType.CLOUD);
        K8SNode localNode = mergeNodes(localNodes, K8SNodeType.LOCAL);

        return new K8SClusterState(Arrays.asList(cloudNode, localNode));
    }

    private K8SNode mergeNodes(List<K8SNode> nodes, K8SNodeType type) {
        if (nodes.isEmpty()) {
            return new K8SNode(type, false, Collections.emptyList());
        }
        Set<K8SApp> allApps = new HashSet<>();
        List<K8SNode> activeNodes = nodes
                .stream()
                .filter(node -> node.active)
                .collect(Collectors.toList());

        for (K8SNode node : activeNodes) {
            allApps.addAll(node.apps);
        }

        if (activeNodes.isEmpty()) {
            return new K8SNode(type, false, Collections.emptyList());
        } else {
            return new K8SNode(type, true, Lists.newArrayList(allApps));
        }
    }

    private List<K8SNode> findCloudNode() {
        return kubernetesClientService.findNodesByType(K8SNodeType.CLOUD);
    }

    private List<K8SNode> findLocalNode() {
        return kubernetesClientService.findNodesByType(K8SNodeType.LOCAL);
    }

}
