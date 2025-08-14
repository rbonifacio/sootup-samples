package graph.node;

import sootup.codepropertygraph.propertygraph.nodes.PropertyGraphNode;
import sootup.core.jimple.common.expr.AbstractConditionExpr;

public class IfStatementNode extends Node {
    private AbstractConditionExpr condition;

    public IfStatementNode(PropertyGraphNode node, AbstractConditionExpr condition) {
        super(node);
        this.condition = condition;
    }

    public AbstractConditionExpr getCondition() {
        return condition;
    }

}
