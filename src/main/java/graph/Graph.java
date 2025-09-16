package graph;

import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.EdgeReversedGraph;

import graph.edge.BooleanCFGEdge;
import graph.edge.CFGEdge;
import graph.edge.ControlDependencyEdge;
import graph.edge.DataDependencyEdge;
import graph.edge.Edge;
import graph.node.IfStatementNode;
import graph.node.Node;
import graph.node.SimpleNode;
import graph.node.ThrowStatementNode;
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

/**
 * A graph representation for control property graphs extending JGraphT's DirectedWeightedPseudograph.
 */
import java.util.ArrayList;
//import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Graph extends DirectedPseudograph<Node, Edge> {

    
    private Graph() {
        super(Edge.class);
    }

    /**
     * Creates a Graph from a PropertyGraph.
     *
     * @param cpg the property graph to convert
     * @return the converted graph
     */
    public static Graph fromPropertyGraph(final PropertyGraph cpg) {
        Graph graph = new Graph();
        
        for (PropertyGraphEdge edge : cpg.getEdges()) {

            if (edge instanceof AbstAstEdge) {
                continue;
            }
            
            Node source = createNode(edge.getSource());
            Node target = createNode(edge.getDestination());
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

    private static Node createNode(final PropertyGraphNode node) {
        if (node instanceof StmtGraphNode stmt && stmt.getStmt() instanceof JThrowStmt throwStmt) {
            return new ThrowStatementNode(node, throwStmt.getOp());
        } else if (node instanceof StmtGraphNode stmt && stmt.getStmt() instanceof JIfStmt ifStmt) {
            return new IfStatementNode(node, ifStmt.getCondition());
        }
        return new SimpleNode(node);
    }

    public static List<Node> findThrowNodes(Graph g) {
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
    public static List<Node> findThrowConditions(Graph g, ThrowStatementNode t) {
        List <Node> throwConditions = new ArrayList<>();
        // Not sure how costly this reversal can be at scale. Doc says there is a penalty
        // We can easily build the reversed graph if we like
        EdgeReversedGraph<Node, Edge> reversedGraph = new EdgeReversedGraph<>(g);
        Iterator<Node> iterator = new DepthFirstIterator<>(reversedGraph, t);
        while (iterator.hasNext()) {
            Node n = iterator.next();
            if (n instanceof IfStatementNode) {
                throwConditions.add(n);
            }
        }

        return throwConditions;
    }

    /**
     * Visualise input CPG. Saves
     * @param cpg a ControlPropertyGraph
     */
    public static void toSvg(final PropertyGraph cpg) {

    }

    public static void toSvg(final Graph graph) {

    }
}
