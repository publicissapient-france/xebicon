package fr.xebia.xebicon;

import fr.xebia.xebicon.service.KubernetesClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class K8SClusterListenerTest {

    @InjectMocks
    K8SClusterStateListener k8SClusterStateListener;

    @Mock
    KubernetesClientService kubernetesClientService;

    @Test
    public void should_delete_barapp_pod_when_receiving_KEYNOTE_START() {
        // Given
        Map<String, Object> keynoteStartMsg = new HashMap() {{
            put("type", "KEYNOTE_STATE");
            put("payload", new HashMap<String, String>() {{
                put("value", "KEYNOTE_START");
            }});
        }};

        // When
        k8SClusterStateListener.receiveState(keynoteStartMsg);

        // Then
        Mockito.verify(kubernetesClientService, times(1)).deletePodsWithLabel(eq("app"), eq("bar-app"));
    }

}