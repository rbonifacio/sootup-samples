import graph.node.IfStatementNode;
import graph.node.Node;
import graph.node.ThrowStatementNode;
import sootup.codepropertygraph.ast.AstCreator;
import sootup.codepropertygraph.cdg.CdgCreator;
import sootup.codepropertygraph.cfg.CfgCreator;
import sootup.codepropertygraph.cpg.CpgCreator;
import sootup.codepropertygraph.ddg.DdgCreator;
import sootup.codepropertygraph.propertygraph.PropertyGraph;
import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.common.stmt.JThrowStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.*;

import graph.Graph;

public class Driver {

    public List<Graph> execute(String location, String clasz) {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation(location);
        JavaView view = new JavaView(inputLocation);
        JavaClassType classType = view.getIdentifierFactory().getClassType(clasz);

        Optional<JavaSootClass> optSootClass = view.getClass(classType);
        Set<JavaSootMethod> methods = optSootClass.get().getMethods();

        List<Graph> graphs = new ArrayList<>();
        
        methods.forEach(m -> {
            Body body = m.getBody();
            StmtGraph<?> graph = body.getStmtGraph();

            for (Stmt s : graph) {
                if (s instanceof JThrowStmt) {
                    System.out.println(body);
                    graphs.add(buildControlPropertyGraph(m));
                    break;
                }
            }
        });

        List<Node> throwConditions = new ArrayList<>();

        for (Graph g : graphs) {
            List<Node> throwNodes = Graph.findThrowNodes(g);
            for (Node throwNode : throwNodes) {
                ThrowStatementNode tsn = (ThrowStatementNode) throwNode;
                throwConditions = Graph.findThrowConditions(g, tsn);
            }
        }

        return graphs;
    }

    public Graph buildControlPropertyGraph(JavaSootMethod m) {
        AstCreator astCreator = new AstCreator();
        CfgCreator cfgCreator = new CfgCreator();
        CdgCreator cdgCreator = new CdgCreator();
        DdgCreator ddgCreator = new DdgCreator();

        CpgCreator cpgCreator = new CpgCreator(astCreator, cfgCreator, cdgCreator, ddgCreator);

        PropertyGraph cpg = cpgCreator.createCpg(m);
        return Graph.fromPropertyGraph(cpg);
    }
}
