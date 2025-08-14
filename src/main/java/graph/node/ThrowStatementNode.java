package graph.node;

import sootup.codepropertygraph.propertygraph.nodes.PropertyGraphNode;
import sootup.core.jimple.basic.Immediate;

public class ThrowStatementNode extends Node {

    private Immediate throwExpr;
    public ThrowStatementNode(PropertyGraphNode node, Immediate throwExpr) {
        super(node);
        this.throwExpr = throwExpr;
    }

    public Immediate getThrowExpr() {
        return throwExpr;
    }
    
}   
