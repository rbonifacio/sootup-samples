package graph.edge;

import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;

/**
 * Data dependency edge.
 */
public class DataDependencyEdge extends Edge {

    public DataDependencyEdge(PropertyGraphEdge edge) {
        super(edge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataDependencyEdge edge = (DataDependencyEdge) o;
        return super.equals(edge);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
