package witupgraph;

import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.EdgeReversedGraph;

import witupgraph.witupedge.BooleanCFGEdge;
import witupgraph.witupedge.CFGEdge;
import witupgraph.witupedge.ControlDependencyEdge;
import witupgraph.witupedge.DataDependencyEdge;
import witupgraph.witupedge.WITUpEdge;
import witupgraph.witupnode.IfStatementNode;
import witupgraph.witupnode.WITUpNode;
import witupgraph.witupnode.SimpleNode;
import witupgraph.witupnode.ThrowStatementNode;
import org.jgrapht.traverse.DepthFirstIterator;
import sootup.codepropertygraph.propertygraph.PropertyGraph;
import sootup.codepropertygraph.propertygraph.edges.AbstAstEdge;
import sootup.codepropertygraph.propertygraph.edges.CdgEdge;
import sootup.codepropertygraph.propertygraph.edges.DdgEdge;
import sootup.codepropertygraph.propertygraph.edges.IfFalseCfgEdge;
import sootup.codepropertygraph.propertygraph.edges.IfTrueCfgEdge;
import sootup.codepropertygraph.propertygraph.edges.NormalCfgEdge;
import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;
import sootup.codepropertygraph.propertygraph.nodes.PropertyGraphNode;
import sootup.codepropertygraph.propertygraph.nodes.StmtGraphNode;
import sootup.core.jimple.common.stmt.JIfStmt;
import sootup.core.jimple.common.stmt.JThrowStmt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A graph representation for control property graphs extending JGraphT's DirectedPseudograph.
 */
public class WITUpGraph extends DirectedPseudograph<WITUpNode, WITUpEdge> {

    
    private WITUpGraph() {
        super(WITUpEdge.class);
    }

    /**
     * Creates a WITUpGraph from <a href="https://soot-oss.github.io/SootUp/v2.0.0/codepropertygraphs/">SootUp's</a>
     * PropertyGraph type.
     *
     * @param cpg the PropertyGraph to convert
     * @return the converted WITUpGraph
     */
    /*
    This couples WITUpGraph with SootUp. If we are ever going to process multiple languages, then
    we are going to need to decide whether to couple the Java frontend to SootUp or to add a
    serialisation layer before creating the WITUpGraph
     */
    public static WITUpGraph fromPropertyGraph(final PropertyGraph cpg) {
        WITUpGraph graph = new WITUpGraph();

        for (PropertyGraphEdge edge : cpg.getEdges()) {

            if (edge instanceof AbstAstEdge) {
                continue;
            }
            
            WITUpNode source = createNode(edge.getSource());
            WITUpNode target = createNode(edge.getDestination());
            graph.addVertex(source);
            graph.addVertex(target);
            
            if (edge instanceof DdgEdge) {
                graph.addEdge(source, target, new DataDependencyEdge(edge));
            } else if (edge instanceof CdgEdge) {
                graph.addEdge(source, target, new ControlDependencyEdge(edge));
            } else if (edge instanceof IfTrueCfgEdge) {
                graph.addEdge(source, target, new BooleanCFGEdge(edge, true));
            } else if (edge instanceof IfFalseCfgEdge) {
                graph.addEdge(source, target, new BooleanCFGEdge(edge, false));
            } else if (edge instanceof NormalCfgEdge) {
                graph.addEdge(source, target, new CFGEdge(edge));
            } else {
                throw new IllegalArgumentException("Unknown edge type: " + edge.getClass().getName());
            }
        }

        return graph;
    }

    private static WITUpNode createNode(final PropertyGraphNode node) {
        if (node instanceof StmtGraphNode stmt && stmt.getStmt() instanceof JThrowStmt throwStmt) {
            return new ThrowStatementNode(node, throwStmt.getOp());
        } else if (node instanceof StmtGraphNode stmt && stmt.getStmt() instanceof JIfStmt ifStmt) {
            return new IfStatementNode(node, ifStmt.getCondition());
        }
        return new SimpleNode(node);
    }

    public static List<WITUpNode> findThrowNodes(WITUpGraph g) {
        return g
                .vertexSet()
                .stream()
                .filter(n -> n.getClass().equals(ThrowStatementNode.class)).toList();
    }

    /**
     *
     * @param g a Graph
     * @param t a ThrowStatementNode
     * @return a list of IfStatementNode that have a path to t
     */
    public static List<WITUpNode> findConditionNodesInThrowPath(WITUpGraph g, ThrowStatementNode t) {
        List <WITUpNode> throwConditionNodes = new ArrayList<>();
        // Not sure how costly this reversal can be at scale. Doc says there is a penalty
        // We can build the reversed graph if we need
        EdgeReversedGraph<WITUpNode, WITUpEdge> reversedGraph = new EdgeReversedGraph<>(g);
        Iterator<WITUpNode> iterator = new DepthFirstIterator<>(reversedGraph, t);
        while (iterator.hasNext()) {
            WITUpNode n = iterator.next();
            if (n instanceof IfStatementNode) {
                throwConditionNodes.add(n);
            }
        }

        return throwConditionNodes;
    }
}
