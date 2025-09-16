import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import graph.node.Node;
import org.junit.jupiter.api.Test;

import graph.Graph;

import static org.junit.jupiter.api.Assertions.*;

public class DriverTest {
    Path projectRoot = Paths.get(System.getProperty("user.dir"));
    Path testClassesDir = projectRoot.resolve("target/test-classes");

    @Test
    public void buildJimple() {
        Driver driver = new Driver();
        List<Graph> graphs = driver.execute(testClassesDir.toString(), "br.unb.cic.samples.Math");
        assertNotNull(graphs);
        assertEquals(1, graphs.size());
    }

    @Test
    public void findExceptionConditionNodes() {
        Driver driver = new Driver();
        List<Graph> graphs = driver.execute(testClassesDir.toString(), "br.unb.cic.samples.Math");
        assertNotNull(graphs);
        assertEquals(1, graphs.size());
        List<Node> throwNodes = Graph.findThrowNodes(graphs.get(0));
        assertEquals(1, throwNodes.size());
        HashMap<Node, List<Node>> throwConditions = driver.findThrowConditionNodes(graphs.get(0));
        assertEquals(1, throwConditions.size());
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());
    }
}
