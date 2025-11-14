//import guru.nidi.graphviz.engine.Format;
//import guru.nidi.graphviz.engine.Graphviz;
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

//import java.io.File;
//import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import witupgraph.WITUpGraph;

/**
 * Driver class for creating control property graphs from Java methods containing throw statements.
 */
public final class Driver {
    /**
     * Executes the analysis on the specified class and returns graphs for methods with throw statements.
     *
     * @param location the location of the class to analyze
     * @param clasz the name of the class to analyze
     * @return Hash map where keys are methods' full names and values are the graphs
     */
    public HashMap<String, WITUpGraph> buildCPGForThrowingMethods(final String location, final String clasz) {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation(location);
        JavaView view = new JavaView(inputLocation);
        JavaClassType classType = view.getIdentifierFactory().getClassType(clasz);

        Optional<JavaSootClass> optSootClass = view.getClass(classType);
        Set<JavaSootMethod> methods = optSootClass.get().getMethods();

        HashMap<String, WITUpGraph> graphs = new HashMap<>();
        
        methods.forEach(m -> {
            Body body = m.getBody();
            StmtGraph<?> graph = body.getStmtGraph();

            for (Stmt s : graph) {
                if (s instanceof JThrowStmt) {
//                    System.out.println(m.getBody());
                    graphs.put(m.getSignature().toString(), buildCodePropertyGraph(m));
                    break;
                }
            }
        });

        return graphs;
    }

    /**
     * Builds a code property graph for the given method.
     *
     * @param m the method to analyze
     * @return the code property graph
     */
    public WITUpGraph buildCodePropertyGraph(final JavaSootMethod m) {
        AstCreator astCreator = new AstCreator();
        CfgCreator cfgCreator = new CfgCreator();
        CdgCreator cdgCreator = new CdgCreator();
        DdgCreator ddgCreator = new DdgCreator();

        CpgCreator cpgCreator = new CpgCreator(astCreator, cfgCreator, cdgCreator, ddgCreator);

        PropertyGraph cpg = cpgCreator.createCpg(m);
        String dotGraph = cpg.toDotGraph();

//        try {
//            Graphviz.fromString(dotGraph)
//                    .render(Format.SVG)
//                    .toFile(new File(m.getName() + "graph.svg"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        return WITUpGraph.fromPropertyGraph(cpg);
    }
}
