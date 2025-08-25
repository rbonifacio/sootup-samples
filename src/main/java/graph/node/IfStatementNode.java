package graph.node;

import sootup.codepropertygraph.propertygraph.nodes.PropertyGraphNode;
import sootup.core.jimple.common.expr.AbstractConditionExpr;

/**
 * A node representing an if statement.
 */
public class IfStatementNode extends Node {
    private AbstractConditionExpr condition;

    /**
     * Constructor for IfStatementNode.
     *
     * @param node the property graph node
     * @param condition the condition expression
     */
    public IfStatementNode(final PropertyGraphNode node, final AbstractConditionExpr condition) {
        super(node);
        this.condition = condition;
    }

    /**
     * Gets the condition expression.
     *
     * @return the condition expression
     */
    public AbstractConditionExpr getCondition() {
        return condition;
    }

}
