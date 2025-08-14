package graph.edge;

import java.util.Objects;

import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;

public class BooleanCFGEdge extends CFGEdge {

    private boolean condition;
    public BooleanCFGEdge(PropertyGraphEdge edge, boolean condition) {
        super(edge);
        this.condition = condition;
    }

    public boolean getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanCFGEdge edge = (BooleanCFGEdge) o;
        return super.equals(edge) && condition == edge.condition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), condition);
    }

}
