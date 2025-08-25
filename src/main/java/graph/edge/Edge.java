package graph.edge;

import java.util.Objects;

import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;

/**
 * Abstract base class for graph edges.
 */
public abstract class Edge {
    private PropertyGraphEdge edge;

    /**
     * Constructor for Edge.
     *
     * @param edge the property graph edge
     */
    public Edge(final PropertyGraphEdge edge) {
      this.edge = edge;
    }

    /**
     * Gets the underlying property graph edge.
     *
     * @return the property graph edge
     */
    public PropertyGraphEdge getEdge() {
      return edge;
    }

   @Override
   public boolean equals(final Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    Edge otherEdge = (Edge) o;
    return Objects.equals(this.edge, otherEdge.edge);
   }
   
   @Override
   public int hashCode() {
    return Objects.hash(edge);
   }

}
