import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.junit.jupiter.api.Disabled;
import sootup.codepropertygraph.propertygraph.nodes.StmtGraphNode;
import witupgraph.witupedge.BooleanCFGEdge;
import witupgraph.witupedge.CFGEdge;
import witupgraph.witupedge.WITUpEdge;
import witupgraph.witupnode.IfStatementNode;
import witupgraph.witupnode.SimpleNode;
import witupgraph.witupnode.ThrowStatementNode;
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

        List<WITUpNode> conditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(1, conditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());
    }

    @Test
    public void findCircleAreaThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double circleArea()>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());

        List<WITUpNode> conditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(1, conditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(1, throwConditions.get(throwNodes.get(0)).size());
    }

    @Test
    public void findProbabilityThrowNodes() {
        Driver driver = new Driver();
        HashMap<String, WITUpGraph> graphs = driver.buildCPGForThrowingMethods(testClassesDir.toString(), "br.unb.cic.samples.Math");
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double probability(int)>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());

        List<WITUpNode> throwConditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(2, throwConditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(2, throwConditions.get(throwNodes.get(0)).size());

        List<List<BooleanCFGEdge>> paths = WITUpGraph.findPathsToTrow(g, throwNodes.get(0), throwConditionNodes.get(0));
        System.out.println("number of unique paths: " + paths.size());
        System.out.println(paths);
    }
}
