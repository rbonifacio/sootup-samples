import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.common.stmt.JThrowStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class Driver {

    public void execute(String location, String clasz, String method) {
        AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation(location);
        JavaView view = new JavaView(inputLocation);
        JavaClassType classType = view.getIdentifierFactory().getClassType(clasz);

        Optional<JavaSootClass> optSootClass = view.getClass(classType);
        Set<JavaSootMethod> methods = optSootClass.get().getMethods();

        methods.forEach(m -> {
            Body body = m.getBody();
            StmtGraph graph = body.getStmtGraph();
            Iterator<Stmt> it = graph.iterator();

            while(it.hasNext()) {
                Stmt s = it.next();

                if(s instanceof JThrowStmt) {
                    System.out.println(body);
                    JThrowStmt throwStmt = (JThrowStmt)s;
                    System.out.println("Found a throw stmt: " + throwStmt.toString());
                    if(throwStmt.getOp() instanceof Local) {
                        Local local = (Local)throwStmt.getOp();
                        local.getDefsForLocalUse(graph, s).forEach(def -> System.out.println(def));
                        System.out.println("TODO: This is not enough to find RuntimeException in the example: " + local.getType());
                    }
                }
            }

        });

    }

}
