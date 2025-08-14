package graph.edge;

import java.util.Objects;

import sootup.codepropertygraph.propertygraph.edges.PropertyGraphEdge;

public abstract class Edge {
    private PropertyGraphEdge edge;

    public Edge(PropertyGraphEdge edge) {
      this.edge = edge;
    }

    public PropertyGraphEdge getEdge() {
      return edge;
    }

   public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Edge edge = (Edge) o;
    return Objects.equals(this.edge, edge.edge);
   }
   
   public int hashCode() {
    return Objects.hash(edge);
   }

}
