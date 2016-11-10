package fr.xebia.xebicon;

import fr.xebia.xebicon.model.K8SClusterState;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {K8SClusterStateApp.class})
@IntegrationTest
@Ignore
public class K8SClusterStateSupplierIT {

    @Autowired
    K8SClusterStateSupplier supplier;

    @Test
    public void shouldConnectToKubernetes() throws IOException {
        K8SClusterState state = supplier.get();
    }

}
