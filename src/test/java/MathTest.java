import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Disabled;
import sootup.codepropertygraph.propertygraph.nodes.StmtGraphNode;
import witupgraph.witupedge.WITUpEdge;
import witupgraph.witupnode.IfStatementNode;
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
        WITUpGraph g = graphs.get("<br.unb.cic.samples.Math: double probability(double)>");
        List<WITUpNode> throwNodes = WITUpGraph.findThrowNodes(g);
        assertEquals(1, throwNodes.size());

        List<WITUpNode> conditionNodes = WITUpGraph.findConditionNodes(g, (ThrowStatementNode) throwNodes.get(0));
        assertEquals(2, conditionNodes.size());

        HashMap<WITUpNode, List<WITUpNode>> throwConditions = WITUpGraph.findThrowConditions(g, throwNodes);
        assertEquals(2, throwConditions.get(throwNodes.get(0)).size());

        for (WITUpNode n: throwConditions.get(throwNodes.get(0))) {
            IfStatementNode ifNode = (IfStatementNode) n;
            // By construction, there should always be an edge between the first
            // operand of the condition (eg $stack3) and a StmtGraphNode where
            // the operand is the leftOp of a JAssignStmt in a StmtGraphNode
            System.out.println("ifNodeCondition: " + ifNode.getNode() + " may or may not need complement");
            for (WITUpEdge e : g.edgeSet()) {
                if (e.getEdge().getSource() instanceof StmtGraphNode &&
                        (e.getEdge().getDestination() instanceof StmtGraphNode)) {
                    if (Objects.equals(e.getEdge().getDestination(), ifNode.getNode())) {
                        System.out.println("bateu site");
//                        StmtGraphNode rato = (StmtGraphNode) e.getEdge().getSource();
//                        // This is the node I need $stack3 = p cmpg 0.0
//                        // This is the edge "src $stack3 = p cmpg 0.0 dst if $stack3 < 0"
//                        // Now cast the node to what it is and get leftop, rightop, ...
//                        System.out.println("This node relates the stack variable to the parameter: " +  rato.getStmt().toString());
//                        System.out.println("The source node '" + e.getEdge().getSource() + "' is the one above.");
//                        System.out.println("The destination node  '" + e.getEdge().getDestination() + "' is the if condition that throws");
//                        System.out.println("The edge is " + e);
//                        System.out.println("The edge class is " + e.getClass());
                    }
                }
            }
        }
    }
}
