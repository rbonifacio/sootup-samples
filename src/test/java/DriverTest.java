import java.util.List;

import org.junit.Test;

import graph.Graph;

import static org.junit.Assert.*;

public class DriverTest {

    @Test
    public void buildJimple() {
        Driver driver = new Driver();
        List<Graph> graphs = driver.execute("/home/adriano/Projects/phd/sootup-samples/target/test-classes", "br.unb.cic.samples.Math");
        assertNotNull(graphs);
        assertEquals(1, graphs.size());
    }
}
