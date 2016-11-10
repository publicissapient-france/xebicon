package fr.xebia.xebicon.service;

import fr.xebia.xebicon.model.K8SApp;
import fr.xebia.xebicon.model.K8SNode;
import fr.xebia.xebicon.model.K8SNodeType;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class KubernetesClientService {

    private final KubernetesClient k8sClient;

    @Autowired
    public KubernetesClientService(KubernetesClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    public List<K8SNode> findNodesByType(K8SNodeType type) {
        List<Node> cloudNodes = k8sClient.nodes().withLabel("type", type.name().toLowerCase()).list().getItems();

        List<Pod> pods = findPods();

        return cloudNodes
                .stream()
                .map(node -> buildK8sNode(node, pods, type))
                .collect(Collectors.toList());
    }

    public void deletePodsWithLabel(String label, String value) {
        k8sClient.pods().withLabel("app", "bar-app").delete();
    }

    private List<Pod> findPods() {
        return k8sClient.pods().list().getItems();
    }


    private K8SNode buildK8sNode(Node node, List<Pod> pods, K8SNodeType type) {
        List<K8SApp> apps = pods.stream()
                .filter(pod -> pod.getSpec().getNodeName().equals(node.getMetadata().getName()))
                .filter(pod -> pod.getMetadata().getLabels().containsKey("display"))
                .map(pod -> new K8SApp(pod.getMetadata().getLabels().get("display"))).collect(toList());
        Optional<NodeCondition> ready = node.getStatus().getConditions().stream().filter(condition -> condition.getType().equals("Ready") && condition.getStatus().equals("True")).findFirst();
        return new K8SNode(type, ready.isPresent(), apps);
    }

}
