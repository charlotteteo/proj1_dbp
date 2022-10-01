import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {
	private Node previousNode;
	private Node nextNode;
	private Node parent;
	private List<Key> keys;
	private List<Node> children;
	public boolean internal;

	public Node() {
		this.previousNode = null;
		this.nextNode = null;
		this.keys = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	public List<Key> getKeys() {
		return keys;
	}

	public void setKeys(List<Key> keys) {
		Iterator<Key> iterator = keys.iterator();
		while (iterator.hasNext()) {
			this.keys.add(iterator.next());
		}
	}

	public int getNoOfKeys() {
		return this.keys.size();
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getNextNode() {
		return nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
	}

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
	}
	
	@Override
	public String toString() {
		return "Keys: " + keys.toString();
	}
}
