import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import graph.Graph;

public class DriverTest {

    @Test
    public void buildJimple() {
        Driver driver = new Driver();
        List<Graph> graphs = driver.execute("/Users/rbonifacio/Documents/workspace-java/sootup/target/test-classes", "br.unb.cic.samples.Math");
        assertNotNull(graphs);
        assertTrue(graphs.size() == 1);
    }
}
