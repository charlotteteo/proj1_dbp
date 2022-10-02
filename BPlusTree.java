import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BPlusTree {

    // B+ Tree Info
    private Node root;
    private int height = 0;
    private int m;

    // For Experiment Purposes
    private int indexNodesAccessed = 0;
    private int dataBlocksAccessed = 0;

    private int mergedNo = 0;
    private int deletedNo = 0;

    private int nodeRecordCount = 0;
    private int noOfNodes = 0;

    public BPlusTree(int order) {
        this.m = order; // Set B+ Tree M value
        this.root = null; // Set Root Node to null on default
    }
    // Insertion on B+ Tree:
    // 1.Perform a search to determine what node the new record should be inserted
    // to
    // 2.If the node is not full (at most n keys after the insertion), insert the
    // record to the node
    // 3.Otherwise,
    // 1)Split the node into two
    // 2)Distribute the keys among the two nodes
    // 3)Insert the new node to the parent if any and create a new root otherwise
    // 4)Repeat until a parent is found that need not split

    public void insertNodeKey(Record record, float key) {
        if (null == this.root) {
            Node node = new Node();
            node.getKeys().add(new Key(key, record));

            this.root = node;
            this.root.setParent(null);
            this.root.internal = false;

        } else if (this.root.getChildren().isEmpty() && this.root.getKeys().size() < (this.m - 1)) {
            this.root.internal = true;
            insertExternalNodeKey(record, this.root, key);

        } else {
            Node currentNode = this.root;

            while (!currentNode.getChildren().isEmpty()) {
                currentNode = currentNode.getChildren().get(searchInternalNodeKey(currentNode.getKeys(), key));
            }

            insertExternalNodeKey(record, currentNode, key);
            if (currentNode.getKeys().size() == this.m) {
                splitExternalNodeKey(currentNode, this.m);
            }
        }
    }

    private void insertExternalNodeKey(Record record, Node node, float key) {
        int index = searchInternalNodeKey(node.getKeys(), key);

        if (index != 0 && node.getKeys().get(index - 1).getKey() == key) {
            node.getKeys().get(index - 1).getRec().add(record);

        } else {
            Key keyNew = new Key(key, record);
            node.getKeys().add(index, keyNew);

            node.internal = false;
        }
    }

    private void splitExternalNodeKey(Node node, int m) {
        int midIndex = m / 2;
        node.internal = false;

        Node rightNode = new Node();
        Node midNode = new Node();

        rightNode.setKeys(node.getKeys().subList(midIndex, node.getNoOfKeys()));
        rightNode.setParent(midNode);

        midNode.getKeys().add(new Key(node.getKeys().get(midIndex).getKey()));
        midNode.getChildren().add(rightNode);

        node.getKeys().subList(midIndex, node.getNoOfKeys()).clear();
        splitInternalNodeKey(node.getParent(), node, midNode, m, true);
    }

    private void splitInternalNodeKey(Node node, Node previousNode, Node insertedNode, int m, boolean split) {
        if (null == node) {
            this.root = insertedNode;

            int prevIndex = searchInternalNodeKey(insertedNode.getKeys(), previousNode.getKeys().get(0).getKey());

            previousNode.setParent(insertedNode);
            insertedNode.getChildren().add(prevIndex, previousNode);

            if (split) {
                if (prevIndex == 0) {
                    insertedNode.getChildren().get(0).setNextNode(insertedNode.getChildren().get(1));
                    insertedNode.getChildren().get(1).setPreviousNode(insertedNode.getChildren().get(0));

                } else {
                    insertedNode.getChildren().get(prevIndex + 1)
                            .setPreviousNode(insertedNode.getChildren().get(prevIndex));
                    insertedNode.getChildren().get(prevIndex - 1)
                            .setNextNode(insertedNode.getChildren().get(prevIndex));
                }
            }
        } else {
            mergeInternalNodeKey(insertedNode, node);

            if (node.getNoOfKeys() == m) {
                int midIndex = (int) Math.ceil(m / 2.0) - 1;

                Node rightNode = new Node();
                Node midNode = new Node();

                rightNode.setKeys(node.getKeys().subList(midIndex + 1, node.getKeys().size()));
                rightNode.setParent(midNode);

                midNode.getKeys().add(node.getKeys().get(midIndex));
                midNode.getChildren().add(rightNode);

                List<Node> rightChild = new ArrayList<>();
                List<Node> currentChild = node.getChildren();
                int leftChild = currentChild.size() - 1;

                for (int i = currentChild.size() - 1; i >= 0; i--) {
                    List<Key> currKeysList = currentChild.get(i).getKeys();
                    if (midNode.getKeys().get(0).getKey() <= currKeysList.get(0).getKey()) {
                        currentChild.get(i).setParent(rightNode);
                        rightChild.add(0, currentChild.get(i));

                        leftChild--;
                    } else {
                        break;
                    }
                }

                rightNode.setChildren(rightChild);

                node.getChildren().subList(leftChild + 1, currentChild.size()).clear();
                node.getKeys().subList(midIndex, node.getKeys().size()).clear();

                splitInternalNodeKey(node.getParent(), node, midNode, m, false);
            }
        }
    }

    public int searchInternalNodeKey(List<Key> keyList, float key) {
        int index = -1;
        int start = 0;
        int mid;
        int end = keyList.size() - 1;

        if (key < keyList.get(start).getKey()) {
            return 0;
        }

        if (key >= keyList.get(end).getKey()) {
            return keyList.size();
        }

        while (start <= end) {
            mid = (start + end) / 2;

            if (key < keyList.get(mid).getKey() && key >= keyList.get(mid - 1).getKey()) {
                index = mid;
                break;

            } else if (key >= keyList.get(mid).getKey()) {
                start = mid + 1;

            } else {
                end = mid - 1;

            }
        }
        return index;
    }

    private void mergeInternalNodeKey(Node nodeF, Node nodeT) {
        Node insertedChild = nodeF.getChildren().get(0);
        Key insertedKey = nodeF.getKeys().get(0);

        int insertedIndex = searchInternalNodeKey(nodeT.getKeys(), insertedKey.getKey());
        int childPosition = insertedIndex;

        if (insertedKey.getKey() <= insertedChild.getKeys().get(0).getKey()) {
            childPosition = insertedIndex + 1;
        }

        insertedChild.setParent(nodeT);

        nodeT.getChildren().add(childPosition, insertedChild);
        nodeT.getKeys().add(insertedIndex, insertedKey);

        if (!nodeT.getChildren().isEmpty() && nodeT.getChildren().get(0).getChildren().isEmpty()) {
            if (nodeT.getChildren().size() - 1 != childPosition
                    && nodeT.getChildren().get(childPosition + 1).getPreviousNode() == null) {
                nodeT.getChildren().get(childPosition + 1).setPreviousNode(nodeT.getChildren().get(childPosition));
                nodeT.getChildren().get(childPosition).setNextNode(nodeT.getChildren().get(childPosition + 1));

            } else if (0 != childPosition && nodeT.getChildren().get(childPosition - 1).getNextNode() == null) {
                nodeT.getChildren().get(childPosition).setPreviousNode(nodeT.getChildren().get(childPosition - 1));
                nodeT.getChildren().get(childPosition - 1).setNextNode(nodeT.getChildren().get(childPosition));

            } else {
                nodeT.getChildren().get(childPosition)
                        .setNextNode(nodeT.getChildren().get(childPosition - 1).getNextNode());
                nodeT.getChildren().get(childPosition).getNextNode()
                        .setPreviousNode(nodeT.getChildren().get(childPosition));

                nodeT.getChildren().get(childPosition - 1).setNextNode(nodeT.getChildren().get(childPosition));
                nodeT.getChildren().get(childPosition).setPreviousNode(nodeT.getChildren().get(childPosition - 1));
            }
        }
    }

    public List<Record> searchNodeKey(float key) {
        Node currentNode = this.root;

        List<Record> searchList = null;

        dataBlocksAccessed = 0;
        indexNodesAccessed = 0;
        indexNodesAccessed++;

        System.out.println("Index Node Access: Node = " + currentNode.getKeys());

        // Traverse to the corresponding external node that would contain this key
        while (currentNode.getChildren().size() != 0) {
            currentNode = currentNode.getChildren().get(searchInternalNodeKey(currentNode.getKeys(), key));
            indexNodesAccessed++;

            System.out.println("Index Node Access: Node = " + currentNode.getKeys());
        }

        List<Key> keyList = currentNode.getKeys();

        for (int i = 0; i < keyList.size(); i++) {
            if (key == keyList.get(i).getKey()) {
                System.out.println("Data Block Access: Key = " + keyList.get(i).getKey());
                System.out.println("Value Size = " + keyList.get(i).getRec().size() + " Records");

                dataBlocksAccessed++;

                searchList = keyList.get(i).getRec();
            }
            if (key < keyList.get(i).getKey()) {
                System.out.println("Value not found");
                break;
            }
        }
        return searchList;
    }

    public List<List<Record>> searchNodeKeyRange(float minimumKey, float maximumKey) {
        List<List<Record>> searchValues = new ArrayList<>();
        Node currentNode = this.root;

        dataBlocksAccessed = 0;
        indexNodesAccessed = 0;
        indexNodesAccessed++;

        System.out.println("Index Node Access: Node = " + currentNode.getKeys());

        while (currentNode.getChildren().size() != 0) {
            currentNode = currentNode.getChildren().get(searchInternalNodeKey(currentNode.getKeys(), minimumKey));
            indexNodesAccessed++;

            System.out.println("Index Node Access: Node = " + currentNode.getKeys());
        }

        List<Key> keyList = currentNode.getKeys();

        while (true) {
            for (int i = 0; i < keyList.size(); i++) {
                if (minimumKey <= keyList.get(i).getKey() && maximumKey >= keyList.get(i).getKey()) {
                    if (dataBlocksAccessed < 5) {
                        System.out.println("Data Block Access: Key = " + keyList.get(i).getKey());
                        System.out.println("Value Size = " + keyList.get(i).getRec().size() + " Records");
                        System.out.println("TConst = " + keyList.get(i).getRec().get(0).getTConst()
                                + " AverageRating = " + keyList.get(i).getRec().get(0).getAverageRating()
                                + " NumVote = " + keyList.get(i).getRec().get(0).getNumVotes());

                    }
                    dataBlocksAccessed++;

                    searchValues.add(keyList.get(i).getRec());
                }

                if (maximumKey < keyList.get(i).getKey()) {
                    break;
                }
            }

            currentNode = currentNode.getNextNode();
            if (currentNode == null) {
                break;
            }

            keyList = currentNode.getKeys();
        }

        double totalRating = 0;
        float noOfRecords = 0;
        for (int i = 0; i < searchValues.size(); i++) {
            for (int j = 0; j < searchValues.get(i).size(); j++) {
                totalRating += searchValues.get(i).get(j).getAverageRating();
                noOfRecords++;
            }
        }
        System.out.println("Average of averageRating: " + totalRating / noOfRecords);
        return searchValues;
    }

    public void deleteNodeKey(float key) {
        Node currentNode = this.root;
        int minLeafKey = (int) Math.floor(m / 2.0);
        int minNonLeafKey = (int) Math.floor((m - 1) / 2.0);

        deletedNo = 0;
        mergedNo = 0;

        while (currentNode.getChildren().size() != 0) {
            currentNode = currentNode.getChildren().get(searchInternalNodeKey(currentNode.getKeys(), key));

            List<Key> keyList = currentNode.getKeys();

            if (currentNode.internal == true) {
                for (int i = 0; i < keyList.size(); i++) {
                    if (keyList.get(i).getKey() == key) {
                        keyList.remove(i);

                    }
                }
                if (keyList.size() < minNonLeafKey) {
                    Node firstChildrenNode = currentNode.getChildren().get(0);
                    currentNode.setKeys(firstChildrenNode.getKeys());

                }
            } else {
                for (int i = 0; i < keyList.size(); i++) {
                    if (keyList.get(i).getKey() == key) {
                        keyList.remove(i);

                    }
                }

                List<Node> nodeList = currentNode.getParent().getChildren();
                for (int i = 0; i < nodeList.size(); i++) {
                    if (nodeList.get(i).getKeys().size() == 0) {
                        nodeList.remove(i);

                    }
                }

                if (keyList.size() >= minLeafKey) {
                    Node parentNode = currentNode.getParent();
                    for (int i = 0; i < parentNode.getKeys().size(); i++) {
                        if (parentNode.getKeys().get(i).getKey() == key) {
                            parentNode.getKeys().get(i).setKey(keyList.get(0).getKey());

                        }
                    }
                }
            }
        }
    }

    public void printTree() {
        Queue<Node> qu = new LinkedList<Node>();
        qu.add(this.root);
        qu.add(null);

        Node curr = null;
        int levelNumber = 2;

        noOfNodes = 0;
        nodeRecordCount = 0;
        height = 0;

        System.out.println("Printing level 1 (Root)");
        while (!qu.isEmpty()) {
            curr = qu.poll();

            if (null == curr) {
                qu.add(null);
                if (qu.peek() == null) {
                    break;
                }
                height = levelNumber;

                System.out.println("\n" + "Printing level " + levelNumber++);
                continue;
            }

            printNodeInfo(curr);
            noOfNodes++;

            if (curr.getChildren().isEmpty()) {
                break;
            }

            for (int i = 0; i < curr.getChildren().size(); i++) {
                qu.add(curr.getChildren().get(i));
            }
        }

        curr = curr.getNextNode();
        while (null != curr) {
            printNodeInfo(curr);
            noOfNodes++;
            curr = curr.getNextNode();

        }
        System.out.println("\nTotal no. of nodes in B+ tree = " + noOfNodes);
    }

    private void printNodeInfo(Node currentNode) {

        for (int i = 0; i < currentNode.getKeys().size(); i++) {
            nodeRecordCount = 0;

            System.out.print(currentNode.getKeys().get(i).getKey() + " ");
            String values = "";

            for (int j = 0; j < currentNode.getKeys().get(i).getRec().size(); j++) {
                values = values + currentNode.getKeys().get(i).getRec().get(j) + ",";
                nodeRecordCount++;

            }
            System.out.print(values.isEmpty() ? " " : nodeRecordCount + " ");
        }

        if (currentNode.getKeys().size() != 0) {
            System.out.print("| ");
        }
    }

    public void printUpdatedNodesInfo() {
        System.out.println("No. of Merged nodes = " + mergedNo);
        System.out.println("No. of Deleted nodes = " + deletedNo);
    }

    public void printIndexNodeAccess() {
        System.out.println("No. of Index Nodes Access: " + indexNodesAccessed);
    }

    public void printDataBlockAccess() {
        System.out.println("No. of Data Block Access: " + dataBlocksAccessed);
    }

    public void printHeightInfo() {
        System.out.println("Tree height = " + height);
    }
}
