package witupgraph;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.EdgeReversedGraph;

import org.json.JSONArray;
import org.json.JSONObject;
import sootup.core.jimple.common.stmt.JIdentityStmt;
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
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
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

import java.util.*;

/**
 * A graph representation for control property graphs extending JGraphT's DirectedPseudograph.
 */
public final class WITUpGraph extends DirectedPseudograph<WITUpNode, WITUpEdge> {
    WITUpNode first;

    public WITUpNode getFirstNode() {
        return this.first;
    }

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

    public static List<WITUpNode> findThrowNodes(final WITUpGraph g) {
        return g
                .vertexSet()
                .stream()
                .filter(n -> n.getClass().equals(ThrowStatementNode.class)).toList();
    }

    /**
     *
     * @param g a WITUpGraph
     * @param t a ThrowStatementNode
     * @return a list of IfStatementNode that have a path to t
     */
    public static List<WITUpNode> findConditionNodes(final WITUpGraph g, final ThrowStatementNode t) {
        List<WITUpNode> throwConditionNodes = new ArrayList<>();
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

    /**
     *
     * @param g an instance of WITUpGraph for a method
     * @param throwNodes nodes that correspondd to a Throw statement
     * @return A JSONArray like
     * [{"truthValue":false,"conditionStmt":"p < 0"},{"truthValue":false,"conditionStmt":"p <= 1"}]
     * i.e., the statement in text form and the truth value that must be satisfied.
     */
    public static JSONArray findConditionPathsThatThrow(final WITUpGraph g, final List<WITUpNode> throwNodes) {
        Optional<WITUpNode> entryNode = g.vertexSet().stream()
                .filter(n -> g.incomingEdgesOf(n).stream()
                        .noneMatch(e -> e instanceof CFGEdge || e instanceof BooleanCFGEdge))
                .findFirst();

        WITUpNode entry = entryNode.orElseThrow(() ->
                new RuntimeException("No entry node found")
        );
        
        AllDirectedPaths<WITUpNode, WITUpEdge> allPaths = new AllDirectedPaths<>(g);
        List<GraphPath<WITUpNode, WITUpEdge>> throwPaths = allPaths
                .getAllPaths(Set.of(entry), new HashSet<>(throwNodes), true, null);

        // We essentially only care about CFG edges when determining the paths. The other edges only create
        // noise/redundant paths
        List<GraphPath<WITUpNode, WITUpEdge>> pathsWithIfStatements = throwPaths.stream()
                .filter(p -> p.getEdgeList()
                        .stream()
                        .noneMatch(e ->
                                e instanceof DataDependencyEdge || e instanceof ControlDependencyEdge)
                        && p.getVertexList().stream().anyMatch(v -> v instanceof IfStatementNode)
                )
                .toList();

        List<List<BooleanCFGEdge>> throwConditionsPaths = pathsWithIfStatements.stream()
                .map(p -> p.getEdgeList().stream()
                        .filter(e -> e instanceof BooleanCFGEdge)
                        .map(e -> (BooleanCFGEdge) e)
                        .toList()
                )
                .toList();
        // need to grab the source node of these edges and trace them back in the DDG
        // coming close to having everything we need, will then need to sort out the order
        // and how to optimise/write proper code


        JSONArray allPathsConditions = new JSONArray();

        for (List<BooleanCFGEdge> throwConditionsPath : throwConditionsPaths) {
            JSONArray pathConditions = new JSONArray();
            for (BooleanCFGEdge throwConditionsEdge : throwConditionsPath) {
                JSONObject c = new JSONObject();
                c.put("truthValue",  throwConditionsEdge.getCondition());
                StmtGraphNode stmt = (StmtGraphNode) throwConditionsEdge.getEdge().getSource();
                JIfStmt ifStmt = (JIfStmt) stmt.getStmt();
                c.put("conditionStmt", ifStmt.getCondition());
                pathConditions.put(c);
            }
            allPathsConditions.put(pathConditions);
        }

        return allPathsConditions;
    }

    public static boolean sameConditionNode(WITUpNode lhs, WITUpNode rhs) {
        StmtGraphNode lhsNode = (StmtGraphNode) lhs.getNode();
        StmtGraphNode rhsNode = (StmtGraphNode) rhs.getNode();

        if (!(lhsNode.getStmt() instanceof JIfStmt lhsIf)) return false;
        if (!(rhsNode.getStmt() instanceof JIfStmt rhsIf)) return false;

        // Smells to compare strings here but we have no equals() that works atm
        return lhsIf.getCondition().equivTo(rhsIf.getCondition());
    }

    public static void traceConditionNodes(final WITUpGraph cpg, final WITUpGraph ddg, final WITUpNode conditionNode) {
        List<WITUpNode> entryNodes = ddg.vertexSet().stream()
                .filter(n -> ddg.incomingEdgesOf(n).isEmpty()).toList();

        Optional<WITUpNode> ddgNode =
                ddg.vertexSet().stream()
                        .filter(n -> sameConditionNode(n, conditionNode))
                        .findFirst();

        WITUpNode ddgConditionNode = ddgNode.orElseThrow(() ->
                new RuntimeException("DDG node does not have a counterpart to CPG node")
        );

        AllDirectedPaths<WITUpNode, WITUpEdge> adp = new AllDirectedPaths<>(ddg);
        List<GraphPath<WITUpNode, WITUpEdge>> allPaths = adp
                .getAllPaths(new HashSet<>(entryNodes), Set.of(ddgConditionNode), true, null);

        GraphPath<WITUpNode, WITUpEdge> path = allPaths.stream()
                        .filter(p -> !p.getEdgeList().isEmpty()).toList().get(0);
        // ignore the identity statement for now. In the edges of the CPG, find a BooleanCFGEdge one leaving
        // the condition node on its way to throw
        //

        System.out.println(allPaths);
    }
}
