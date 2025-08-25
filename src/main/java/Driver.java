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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import graph.Graph;

/**
 * Driver class for creating control property graphs from Java methods containing throw statements.
 */
public final class Driver {

    /**
     * Executes the analysis on the specified class and returns graphs for methods with throw statements.
     *
     * @param location the location of the class to analyze
     * @param clasz the name of the class to analyze
     * @return list of control property graphs
     */
    public List<Graph> execute(final String location, final String clasz) {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation(location);
        JavaView view = new JavaView(inputLocation);
        JavaClassType classType = view.getIdentifierFactory().getClassType(clasz);

        Optional<JavaSootClass> optSootClass = view.getClass(classType);
        Set<JavaSootMethod> methods = optSootClass.get().getMethods();

        List<Graph> graphs = new ArrayList<>();
        
        methods.forEach(m -> {
            Body body = m.getBody();
            StmtGraph graph = body.getStmtGraph();
            Iterator<Stmt> it = graph.iterator();

            while (it.hasNext()) {
                Stmt s = it.next();

                if (s instanceof JThrowStmt) {
                    graphs.add(buildControlPropertyGraph(m));
                    break;
                }
            }
        });

        return graphs;
    }

    /**
     * Builds a control property graph for the given method.
     *
     * @param m the method to analyze
     * @return the control property graph
     */
    public Graph buildControlPropertyGraph(final JavaSootMethod m) {
        AstCreator astCreator = new AstCreator();
        CfgCreator cfgCreator = new CfgCreator();
        CdgCreator cdgCreator = new CdgCreator();
        DdgCreator ddgCreator = new DdgCreator();

        CpgCreator cpgCreator = new CpgCreator(astCreator, cfgCreator, cdgCreator, ddgCreator);

        PropertyGraph cpg = cpgCreator.createCpg(m);
        return Graph.fromPropertyGraph(cpg);
    }

}
