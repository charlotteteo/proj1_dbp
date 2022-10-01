import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BPlusTree {

    // B+ Tree Info
    private int m;
    private Node root;
    private int height = 0;

    // For Experiment Purposes
    private int numNodes = 0;
    private int numDeleted = 0;
    private int numMerged = 0;
    private int indexNodesAccess = 0;
    private int dataBlocksAccess = 0;
    private int uniqueKeysCount = 1;
    private int recordsCountInANode = 0;
    private int recordsCountTotal = 0;
    private int numOfNodes = 0;

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
    // // 1: Insertion Functions

    public void insertKey(float key, Record value) {
        // 1: Empty B+ Tree => Create New Root Node
        if (null == this.root) {
            Node newNode = new Node();
            newNode.getKeys().add(new Key(key, value));
            this.root = newNode;
            this.root.internal = false;
            this.root.setParent(null); // Since the root has no parent, parent set to null
        } else if (this.root.getChildren().isEmpty() && this.root.getKeys().size() < (this.m - 1)) {
            // 2: Node is Not Full
            this.root.internal = true;
            insertExternalNode(key, value, this.root);
        } else {
            // 3: Normal insert
            Node curr = this.root;

            // Traverse to the last leaf node
            while (!curr.getChildren().isEmpty()) {
                curr = curr.getChildren().get(searchInternalNode(key, curr.getKeys()));
            }

            insertExternalNode(key, value, curr);

            // External node is full => Split node
            if (curr.getKeys().size() == this.m) {
                splitExternalNode(curr, this.m);
            }
        }

        // Increase number of nodes value
        numNodes++;

    }

    private void insertExternalNode(float key, Record value, Node node) {
        // Find index of key to be inserted
        int index = searchInternalNode(key, node.getKeys());

        if (index != 0 && node.getKeys().get(index - 1).getKey() == key) {
            // Add the new value to the list
            node.getKeys().get(index - 1).getValues().add(value);
        } else {
            // Key is null => Add key and data
            Key newKey = new Key(key, value);
            node.getKeys().add(index, newKey);

            node.internal = false;
        }
    }

    private void splitExternalNode(Node curr, int m) {

        curr.internal = false;

        // Find the middle index
        int midIndex = m / 2;

        Node middleNode = new Node();
        Node rightNode = new Node();

        // Set the right part to have middle element and the elements right to the
        // middle element
        rightNode.setKeys(curr.getKeys().subList(midIndex, curr.getNoOfKeys()));
        rightNode.setParent(middleNode);

        // Internal nodes do not contain values => Set only Keys
        middleNode.getKeys().add(new Key(curr.getKeys().get(midIndex).getKey()));
        middleNode.getChildren().add(rightNode);

        // Update the split node to contain just the left part
        curr.getKeys().subList(midIndex, curr.getNoOfKeys()).clear();

        boolean split = true;
        splitInternalNode(curr.getParent(), curr, m, middleNode, split);

    }

    private void splitInternalNode(Node curr, Node prevNode, int m, Node insertedNode, boolean split) {

        // If current node is null
        if (null == curr) {
            // Set new root
            this.root = insertedNode;

            // Find where the child has to be inserted
            int prevIndex = searchInternalNode(prevNode.getKeys().get(0).getKey(), insertedNode.getKeys());
            prevNode.setParent(insertedNode);
            insertedNode.getChildren().add(prevIndex, prevNode);
            if (split) {
                // Update the linked list only for first split (for external node)
                if (prevIndex == 0) {
                    insertedNode.getChildren().get(0).setNext(insertedNode.getChildren().get(1));
                    insertedNode.getChildren().get(1).setPrev(insertedNode.getChildren().get(0));
                } else {
                    insertedNode.getChildren().get(prevIndex + 1).setPrev(insertedNode.getChildren().get(prevIndex));
                    insertedNode.getChildren().get(prevIndex - 1).setNext(insertedNode.getChildren().get(prevIndex));
                }
            }
        } else {
            // Merge the internal node with the mid + right of previous split
            mergeInternalNodes(insertedNode, curr);

            // Split if internal node is full
            if (curr.getNoOfKeys() == m) {
                int midIndex = (int) Math.ceil(m / 2.0) - 1;
                Node middleNode = new Node();
                Node rightNode = new Node();

                rightNode.setKeys(curr.getKeys().subList(midIndex + 1, curr.getKeys().size()));
                rightNode.setParent(middleNode);

                middleNode.getKeys().add(curr.getKeys().get(midIndex));
                middleNode.getChildren().add(rightNode);

                List<Node> currChildren = curr.getChildren();
                List<Node> rightChildren = new ArrayList<>();

                int leftChild = currChildren.size() - 1;

                // update the children that have to be sent to the right part
                // from the split node
                for (int i = currChildren.size() - 1; i >= 0; i--) {
                    List<Key> currKeysList = currChildren.get(i).getKeys();
                    if (middleNode.getKeys().get(0).getKey() <= currKeysList.get(0).getKey()) {
                        currChildren.get(i).setParent(rightNode);
                        rightChildren.add(0, currChildren.get(i));
                        leftChild--;
                    } else {
                        break;
                    }
                }

                rightNode.setChildren(rightChildren);

                // Update the node to contain just the left part and its children
                curr.getChildren().subList(leftChild + 1, currChildren.size()).clear();
                curr.getKeys().subList(midIndex, curr.getKeys().size()).clear();

                splitInternalNode(curr.getParent(), curr, m, middleNode, false);
            }
        }
    }

    private void mergeInternalNodes(Node nodeFrom, Node nodeTo) {
        Key keyFromInserted = nodeFrom.getKeys().get(0);
        Node childFromInserted = nodeFrom.getChildren().get(0);

        // Find the index where the key has to be inserted
        int indexToBeInsertedAt = searchInternalNode(keyFromInserted.getKey(), nodeTo.getKeys());
        int childInsertPos = indexToBeInsertedAt;
        if (keyFromInserted.getKey() <= childFromInserted.getKeys().get(0).getKey()) {
            childInsertPos = indexToBeInsertedAt + 1;
        }

        childFromInserted.setParent(nodeTo);
        nodeTo.getChildren().add(childInsertPos, childFromInserted);
        nodeTo.getKeys().add(indexToBeInsertedAt, keyFromInserted);

        // Update Linked List of external nodes
        if (!nodeTo.getChildren().isEmpty() && nodeTo.getChildren().get(0).getChildren().isEmpty()) {

            if (nodeTo.getChildren().size() - 1 != childInsertPos
                    && nodeTo.getChildren().get(childInsertPos + 1).getPrev() == null) {
                nodeTo.getChildren().get(childInsertPos + 1).setPrev(nodeTo.getChildren().get(childInsertPos));
                nodeTo.getChildren().get(childInsertPos).setNext(nodeTo.getChildren().get(childInsertPos + 1));
            } else if (0 != childInsertPos && nodeTo.getChildren().get(childInsertPos - 1).getNext() == null) {
                nodeTo.getChildren().get(childInsertPos).setPrev(nodeTo.getChildren().get(childInsertPos - 1));
                nodeTo.getChildren().get(childInsertPos - 1).setNext(nodeTo.getChildren().get(childInsertPos));
            } else {
                // Merge is in between, then the next and the previous element's prev and next
                // pointers have to be updated
                nodeTo.getChildren().get(childInsertPos)
                        .setNext(nodeTo.getChildren().get(childInsertPos - 1).getNext());
                nodeTo.getChildren().get(childInsertPos).getNext().setPrev(nodeTo.getChildren().get(childInsertPos));
                nodeTo.getChildren().get(childInsertPos - 1).setNext(nodeTo.getChildren().get(childInsertPos));
                nodeTo.getChildren().get(childInsertPos).setPrev(nodeTo.getChildren().get(childInsertPos - 1));
            }
        }

    }

    // 2: Search Functions
    public int searchInternalNode(float key, List<Key> keys) {
        int startIndex = 0;
        int endIndex = keys.size() - 1;
        int mid;
        int index = -1;

        // Return first index if key is less than the first element
        if (key < keys.get(startIndex).getKey()) {
            return 0;
        }

        // If key greater than last key
        if (key >= keys.get(endIndex).getKey()) {
            return keys.size();
        }

        while (startIndex <= endIndex) {

            // Get mid index
            mid = (startIndex + endIndex) / 2;

            // Find index of key < index key and >= than previous index key
            if (key < keys.get(mid).getKey() && key >= keys.get(mid - 1).getKey()) {
                index = mid;
                break;
            } else if (key >= keys.get(mid).getKey()) {
                startIndex = mid + 1;
            } else {
                endIndex = mid - 1;
            }
        }
        return index;
    }

    public void deleteKey(float key) {

        numDeleted = 0;
        numMerged = 0;

        Node curr = this.root;

        // Minimum number of keys to balance the b-tree in the leaf node
        int minKeysLeafNode = (int) Math.floor(m / 2.0);
        System.out.println("Minimum number of keys in the leaf node: " + minKeysLeafNode);

        // Minimum number of keys to balance the b-tree in the non-leaf node
        int minKeysNonLeafNode = (int) Math.floor((m - 1) / 2.0);
        System.out.println("Minimum number of keys in the non-leaf node: " + minKeysNonLeafNode);

        // Minimum number of children node to balance the b-tree in the non-leaf node
        int minChildrenNonLeafNode = (int) Math.ceil((m) / 2.0);
        System.out.println("Minimum number of children in the non-leaf node: " + minChildrenNonLeafNode);

        while (curr.getChildren().size() != 0) {
            curr = curr.getChildren().get(searchInternalNode(key, curr.getKeys()));
            System.out.println("curr: " + curr.getKeys());
            System.out.println("curr children: " + curr.getChildren() + " | Size: " + curr.getChildren().size());

            List<Key> keys = curr.getKeys();

            if (curr.internal == true) {
                System.out.println("=Internal=");
                System.out.println("Here: " + keys + " | Size: " + keys.size());

                System.out.println("Children: " + curr.getChildren());

                // If key is in non-leaf node, remove key
                for (int i = 0; i < keys.size(); i++) {
                    if (keys.get(i).getKey() == key) {
                        System.out.println("Removed");
                        keys.remove(i);
                    }
                }

                // if key size is less than min. keys required in non leaf node
                if (keys.size() < minKeysNonLeafNode) {
                    Node firstChildrenNode = curr.getChildren().get(0);
                    System.out.println("Children Key: " + firstChildrenNode.getKeys());
                    curr.setKeys(firstChildrenNode.getKeys());

                }

            } else {
                System.out.println("=Leaf=");
                System.out.println("Here: " + keys + " | Size: " + keys.size());

                // If key is in leaf node, remove key
                for (int i = 0; i < keys.size(); i++) {
                    if (keys.get(i).getKey() == key) {
                        keys.remove(i);
                    }
                }

                List<Node> nodeList = curr.getParent().getChildren();

                System.out.println("Curr: " + curr.getParent().getChildren());
                for (int i = 0; i < nodeList.size(); i++) {
                    if (nodeList.get(i).getKeys().size() == 0) {
                        nodeList.remove(i);
                    }
                }

                // 1: If node has less than ceil(m/2):
                if (keys.size() < minKeysLeafNode) {
                    System.out.println("Not enough keys in node");
                    System.out.println("Parent: " + curr.getParent());
                    System.out.println("Next Sibling: " + curr.getNext());
                    System.out.println("Prev Sibling: " + curr.getPrev());

                    // See if sibling can lend a key
                    // If can borrow, borrow key and adjust parent node keys
                    Node nextNode = curr.getNext();
                    Node prevNode = curr.getPrev();
                    Node parentNode = curr.getParent();

                    /*
                     * // If can borrow a key from left or right sibling, adjust keys in leaf node
                     * and its parent node //1) Check left sibling first, if cannot borrow //2)
                     * Check right sibling
                     * 
                     * int st = 0; int end = prevNode.getKeys().size() - 1;
                     * if(prevNode.getKeys().size() - 1 > minKeysLeafNode) { // Add the last element
                     * key of left sibling to current node keys.add(st,
                     * prevNode.getKeys().get(end));
                     * 
                     * // Update smallest key of current node to parent node for(int i = 0; i <
                     * parentNode.getKeys().size(); i++) { // Find the previous smallest key of
                     * current node in the parent node if(parentNode.getKeys().get(i).getKey() ==
                     * key) { // Get the new smallest key of current node at index position 0 and
                     * update the new smallest key in the parent node
                     * parentNode.getKeys().get(i).setKey(keys.get(st).getKey()); } } } else
                     * if(nextNode.getKeys().size() - 1 > minKeysLeafNode) { // Add the first
                     * element key of right sibling to current node
                     * keys.add(nextNode.getKeys().get(st)); }
                     */

                    // If cannot borrow, merge with sibling & adjust parent node keys

                    if (parentNode != null) {
                        System.out.println("Parent Children: " + curr.getParent().getChildren());

                    }

                } else {
                    // If smallest key is deleted push up the next key

                    Node parentNode = curr.getParent();
                    for (int i = 0; i < parentNode.getKeys().size(); i++) {
                        // Find the previous smallest key of current node in the parent node
                        if (parentNode.getKeys().get(i).getKey() == key) {
                            // Get the new smallest key of current node at index position 0 and update the
                            // new smallest key in the parent node
                            parentNode.getKeys().get(i).setKey(keys.get(0).getKey());
                        }
                    }
                }
            }
        }

    }

    public List<Record> searchKey(float key) {

        // Set access numbers to 0
        dataBlocksAccess = 0;
        indexNodesAccess = 0;

        List<Record> searchValues = null;

        Node curr = this.root;
        indexNodesAccess++;
        System.out.println("Index Node Access: Node = " + curr.getKeys());

        // Traverse to the corresponding external node that would contain this key
        while (curr.getChildren().size() != 0) {
            curr = curr.getChildren().get(searchInternalNode(key, curr.getKeys()));
            indexNodesAccess++;
            System.out.println("Index Node Access: Node = " + curr.getKeys());
        }

        List<Key> keyList = curr.getKeys();

        // Do a linear search in this node for the key
        for (int i = 0; i < keyList.size(); i++) {

            // dataBlocksAccess++;

            if (key == keyList.get(i).getKey()) {

                System.out.println("Data Block Access: NumVotes = " + keyList.get(i).getKey());
                System.out.println("Value Size = " + keyList.get(i).getValues().size() + " Records");
                System.out.println("Value (0) = " + keyList.get(i).getValues().get(0));
                dataBlocksAccess++;

                searchValues = keyList.get(i).getValues();
                

            }
            if (key < keyList.get(i).getKey()) {
                break;
            }
        }

        return searchValues;
    }

    public void displayTreeInfo() {
        // Reset all
        numOfNodes = 0;
        recordsCountTotal = 0;
        recordsCountInANode = 0;
        height = 0;
        uniqueKeysCount = 1;

        Queue<Node> queue = new LinkedList<Node>();
        queue.add(this.root);
        queue.add(null);
        Node curr = null;
        int levelNumber = 2;
        System.out.println("Printing level 1 (Root)");
        while (!queue.isEmpty()) {
            curr = queue.poll();
            if (null == curr) {
                queue.add(null);
                if (queue.peek() == null) {
                    break;
                }
                height = levelNumber;
                System.out.println("\n" + "Printing level " + levelNumber++);

                continue;
            }

            displayNodeInfo(curr);
            numOfNodes++;

            if (curr.getChildren().isEmpty()) {
                break;
            }
            for (int i = 0; i < curr.getChildren().size(); i++) {
                queue.add(curr.getChildren().get(i));
            }
        }

        curr = curr.getNext();
        while (null != curr) {
            displayNodeInfo(curr);
            numOfNodes++;
            curr = curr.getNext();
        }
        System.out.println("\nTotal number of nodes in B+ tree is: " + numOfNodes);
        System.out.println("Total number of records in B+ tree is: " + recordsCountTotal);
    }

    private void displayNodeInfo(Node curr) {

        for (int i = 0; i < curr.getKeys().size(); i++) {
            recordsCountInANode = 0;
            System.out.print(curr.getKeys().get(i).getKey() + ":(");
            String values = "";
            for (int j = 0; j < curr.getKeys().get(i).getValues().size(); j++) {
                values = values + curr.getKeys().get(i).getValues().get(j) + ",";
                recordsCountInANode++;

                /*
                 * if(!values.isEmpty()) {
                 * System.out.print(curr.getKeys().get(i).getValues().get(j).getTConst() + ",");
                 * }
                 */

            }

            recordsCountTotal += recordsCountInANode;
            // System.out.print(values.isEmpty() ? ");" : uniqueKeysCount++ + ")" + "(" +
            // recordsCountInANode + ")" + values.substring(0, values.length() - 1) +
            // ");\n");
            // System.out.print(values.isEmpty() ? ");" : uniqueKeysCount++ + ")" + "(" +
            // recordsCountInANode + ");");
            System.out.print(values.isEmpty() ? ");" : recordsCountInANode + ");");
        }

        if (curr.getKeys().size() != 0) {
            System.out.print("||");
        }

    }

    public void displayHeightInfo() {
		System.out.println("Tree height = " + height);
	}

    public void displayUpdatedNodesInfo() {
		System.out.println("No. of deleted nodes = " + numDeleted);
		System.out.println("No. of merged nodes = " + numMerged);
	}
    public List<List<Record>> searchKeyRange(float minkey, float maxkey) {

        // Set access numbers to 0
        dataBlocksAccess = 0;
        indexNodesAccess = 0;

        List<List<Record>> searchValues = new ArrayList<>();

        Node curr = this.root;
        indexNodesAccess++;
        System.out.println("Index Node Access: Node= " + curr.getKeys());

        // Traverse to the corresponding external node that would contain this key
        while (curr.getChildren().size() != 0) {
            curr = curr.getChildren().get(searchInternalNode(minkey, curr.getKeys()));
            indexNodesAccess++;
            System.out.println("Index Node Access: Node= " + curr.getKeys());
        }

        List<Key> keyList = curr.getKeys();
        for (int i = 0; i<keyList.size();i++){
            System.out.println(keyList.get(i).getKey());
        }

        Boolean flag = true;

        while (flag){
            for (int i = 0; i < keyList.size(); i++) {

                // dataBlocksAccess++;
                if (minkey <= keyList.get(i).getKey() && maxkey >= keyList.get(i).getKey()) {

                    System.out.println("Data Block Access: Key=" + keyList.get(i).getKey());
                    System.out.println("Value Size=" + keyList.get(i).getValues().size() + " Records");
                    System.out.println("Value (0)=" + keyList.get(i).getValues().get(0));
                    dataBlocksAccess++;

                    searchValues.add(keyList.get(i).getValues());
                    for (int j = 0; j < keyList.get(i).getValues().size(); j++) {
                        keyList.get(i).getValues().get(j).printRecord();
                    }

                }
                if (maxkey < keyList.get(i).getKey()) {
                    break;
                }
            }
            curr = curr.getNext();
            if (curr == null){
                break;
            }
            keyList = curr.getKeys();
        }
        for(int i=0; i<searchValues.size(); i++) {
            for (int j = 0; j < searchValues.get(i).size(); j++) {
                searchValues.get(i).get(j).printRecord();
            }
        }

    public void printIndexNodeAccess() {
        System.out.println("Number of Index Nodes Access: " + indexNodesAccess);
    }
    
    public void printDataBlockAccess() {
        System.out.println("Number of Data Block Access: " + dataBlocksAccess);
    }
    
}
