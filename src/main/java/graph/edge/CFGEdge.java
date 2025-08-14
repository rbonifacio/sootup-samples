package graph.edge;

import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;

/**
 * Control flow graph edge.
 */
public class CFGEdge extends Edge {

    public CFGEdge(PropertyGraphEdge edge) {
        super(edge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CFGEdge edge = (CFGEdge) o;
        return super.equals(edge);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
