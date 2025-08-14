package graph.edge;

import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;

/**
 * Control dependency edge.
 */
public class ControlDependencyEdge extends Edge {

    public ControlDependencyEdge(PropertyGraphEdge edge) {
        super(edge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlDependencyEdge edge = (ControlDependencyEdge) o;
        return super.equals(edge);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
