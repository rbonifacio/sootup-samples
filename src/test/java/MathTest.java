import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import witupgraph.witupnode.IfStatementNode;
import witupgraph.witupnode.WITUpNode;
import org.junit.jupiter.api.Test;

import witupgraph.WITUpGraph;

import static org.junit.jupiter.api.Assertions.*;

public class MathTest {
    Path projectRoot = Paths.get(System.getProperty("user.dir"));
    Path testClassesDir = projectRoot.resolve("target/test-classes");

    /*
    FIXME: Right now all this test changes whenever we alter the source code
     of Math. There will be as many graphs as there are methods that throw.
     */
    @Test
    public void findGraphsForMethodsThatThrow() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        assertNotNull(graphs);
        assertEquals(3, graphs.size());
    }


    @Test
    public void findDivThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: int div(int,int)>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());
        HashMap<WITUpNode, List<WITUpNode>> throwConditions = driver.findThrowConditionNodes(g);
        assertEquals(1, throwConditions.size());
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());
        IfStatementNode ifNode = (IfStatementNode) throwConditions.get(throwNodes.get(0)).get(0);
    }

    @Test
    public void findCircleAreaThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double circleArea()>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());
        HashMap<WITUpNode, List<WITUpNode>> throwConditions = driver.findThrowConditionNodes(g);
        assertEquals(1, throwConditions.size());
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());
        IfStatementNode ifNode = (IfStatementNode) throwConditions.get(throwNodes.get(0)).get(0);
    }

    @Test
    public void findProbabilityThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double probability(double)>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());
        HashMap<WITUpNode, List<WITUpNode>> throwConditions = driver.findThrowConditionNodes(g);
        assertEquals(1, throwConditions.size());
        assertEquals(2, throwConditions.get(throwNodes.get(0)).size());
        for (WITUpNode n: throwConditions.get(throwNodes.get(0))) {
            IfStatementNode ifNode = (IfStatementNode) n;
        }
    }
}
