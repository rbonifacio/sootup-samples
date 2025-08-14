package graph.node;

import sootup.codepropertygraph.propertygraph.nodes.PropertyGraphNode;

import java.util.Objects;

public abstract class Node {
    private PropertyGraphNode node;

    public Node(PropertyGraphNode node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node1 = (Node) o;
        return Objects.equals(node, node1.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}
