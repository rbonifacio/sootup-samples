package graph;

import org.jgrapht.graph.DirectedWeightedPseudograph;

import graph.edge.BooleanCFGEdge;
import graph.edge.CFGEdge;
import graph.edge.ControlDependencyEdge;
import graph.edge.DataDependencyEdge;
import graph.edge.Edge;
import graph.node.IfStatementNode;
import graph.node.Node;
import graph.node.SimpleNode;
import graph.node.ThrowStatementNode;
import sootup.codepropertygraph.propertygraph.PropertyGraph;
import sootup.codepropertygraph.propertygraph.edges.AbstAstEdge;
import sootup.codepropertygraph.propertygraph.edges.CdgEdge;
import sootup.codepropertygraph.propertygraph.edges.DdgEdge;
import sootup.codepropertygraph.propertygraph.edges.IfFalseCfgEdge;
import sootup.codepropertygraph.propertygraph.edges.IfTrueCfgEdge;
import sootup.codepropertygraph.propertygraph.edges.NormalCfgEdge;
import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;
import sootup.codepropertygraph.propertygraph.edges.StmtAstEdge;
import sootup.codepropertygraph.propertygraph.nodes.PropertyGraphNode;
import sootup.codepropertygraph.propertygraph.nodes.StmtGraphNode;
import sootup.core.jimple.common.stmt.JIfStmt;
import sootup.core.jimple.common.stmt.JThrowStmt;

public class Graph extends DirectedWeightedPseudograph<Node, Edge> {

    
    private Graph() {
        super(Edge.class);
    }

    public static Graph fromPropertyGraph(PropertyGraph cpg) {
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
            }
            else if (edge instanceof CdgEdge) {
                graph.addEdge(source, target, new ControlDependencyEdge(edge));
            }
            else if (edge instanceof IfTrueCfgEdge) {
                graph.addEdge(source, target, new BooleanCFGEdge(edge, true));
            }
            else if (edge instanceof IfFalseCfgEdge) {
                graph.addEdge(source, target, new BooleanCFGEdge(edge, false));
            }
            else if (edge instanceof NormalCfgEdge)     {
                graph.addEdge(source, target, new CFGEdge(edge));
            }
            else {
                throw new IllegalArgumentException("Unknown edge type: " + edge.getClass().getName());
            }
        }

        return graph;
    }

    private static Node createNode(PropertyGraphNode node) {
        if (node instanceof StmtGraphNode stmt && stmt.getStmt() instanceof JThrowStmt throwStmt) {
            return new ThrowStatementNode(node, throwStmt.getOp());
        }
        else if (node instanceof StmtGraphNode stmt && stmt.getStmt() instanceof JIfStmt ifStmt) {
            return new IfStatementNode(node, ifStmt.getCondition());
        }
        return new SimpleNode(node);
    }
}
