package animals;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class BST {
    private Node root;                      // Root node pointer
    private List<Choice> pathToCurrent;     // List of sequence of answers that leads to current node

    // Node class to build the binary search tree
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Node {
        String data;
        Node parent;    // parent pointer
        Node yes, no;   // left and right sub-tree pointers

        public Node() {}

        public Node(String data, Node parent) {
            this.data = data;
            this.parent = parent;
        }

        // Getters
        public String getData() {
            return data;
        }

        public Node getNo() {
            return no;
        }

        public Node getYes() {
            return yes;
        }

        // Setters
        public void setData(String data) {
            this.data = data;
        }

        public void setNo(Node no) {
            this.no = no;
        }

        public void setYes(Node yes) {
            this.yes = yes;
        }

        // Checking if the node is an animal or a question
        @JsonIgnore
        public boolean isAnimal() {
            return no == null && yes == null;
        }

        // Getting the next node based on the user's choice
        @JsonIgnore
        private Node next(Choice choice) {
            switch (choice) {
                case YES:
                    return yes;
                case NO:
                    return no;
                default:
                    return null;
            }
        }
    }

    // Setting up a new tree from memory
    public BST() {
        root = FileHandle.loadTree();
        pathToCurrent = new ArrayList<>();
    }

    // Setting up a new tree from user input
    public BST(String initialAnimal) {
        root = new Node(initialAnimal, null);
        pathToCurrent = new ArrayList<>();
    }

    // Adding answer into the tree
    public void addChoice(Choice choice) {
        pathToCurrent.add(choice);
    }

    // Getting the root
    public Node getRoot() {
        return root;
    }
    // Getting the current node
//    public Node getCurrentNode() {
//        Node searched = root;
//        int pathStep = 0;
//
//        // While loop to get to the current answer node
//        while (pathStep != pathToCurrent.size()) {
//            Node next = searched.next(pathToCurrent.get(pathStep));
//            if (next == null) {
//                break;
//            }
//            searched = next;
//            pathStep++;
//        }
//        return searched;
//    }

    // Adding the statement about the 2 animals into the answer tree
    public void insertStatement(String statement, String yesAnimal, String noAnimal) {
        root = insertStatement(root, null, 0,statement,yesAnimal, noAnimal);
    }

    private Node insertStatement(Node current, Node parent, int pathStep, String statement, String yesAnimal, String noAnimal) {
        if (pathStep == pathToCurrent.size()) {
            current = new Node(statement, parent);
            current.yes = new Node(yesAnimal, current);
            current.no = new Node(noAnimal, current);
        } else if (pathToCurrent.get(pathStep) == Choice.YES) {
            current.yes = insertStatement(current.yes, current, pathStep + 1, statement, yesAnimal, noAnimal);
        } else {
            current.no = insertStatement(current.no, current, pathStep + 1, statement, yesAnimal, noAnimal);
        }
        return current;
    }

    // Clearing the current answer path
    public void resetPath() {
        pathToCurrent.clear();
    }

    // Saving the current tree into memory
    public void saveTreeToFile() {
        FileHandle.saveTree(root);
    }

    // Checking if the current tree is empty
    public boolean isEmpty() {
        return root == null;
    }


    // Getting all animal nodes in the tree and put them in a list
    public List<String> listAllAnimals() {
        return listAnimalsTraverse(root);
    }

    private List<String> listAnimalsTraverse(Node t) {
        List<String> arrayList = new ArrayList<>();

        if (t == null) {            // Return the list if reached the end of the tree
            return arrayList;
        } else if (t.isAnimal()) {  // Adding current node to the list if its data is a name of a animal
            arrayList.add(t.getData().substring(2).strip());
        } else {
            arrayList.addAll(listAnimalsTraverse(t.getNo()));       // Getting all animal from all the left sub-nodes
            arrayList.addAll(listAnimalsTraverse(t.getYes()));      // Getting all animal from all the right sub-nodes
        }
        return arrayList;
    }

    // It searches for a particular animal and creates a Map of facts on the way
    // Map contains key-value pairs e.g. "Is it a mammal" - true
    public boolean searchForAnimal(String name, Map<String, Boolean> fact) {
        return searchForAnimalAndGetFacts(name, fact, root);
    }

    private boolean searchForAnimalAndGetFacts(String name, Map<String, Boolean> mapOfFacts, Node t) {
        // Return false if reached an empty node
        if (t == null) {
            return false;
        }

        // Return true if the animal node is found
        if (t.getData().equals(name)) {
            return true;
        }

        // Checking the left sub-node
        if (searchForAnimalAndGetFacts(name, mapOfFacts, t.getNo())) {
            mapOfFacts.put(t.getData(), false);     // Adding the negative fact of the animal to the map
            return true;
        }

        // Checking the right sub-node
        if (searchForAnimalAndGetFacts(name, mapOfFacts, t.getYes())) {
            mapOfFacts.put(t.getData(), true);      // Adding the positive fact of the animal to the map
            return true;
        }
        return false;
    }

    // Printing the tree's stats
    public void printStatistics() {
        System.out.println("The Knowledge Tree stats\n");
        // Printing the root node
        System.out.println("- root node " +root.getData());

        int numberOfNodes = totalNumberOfNodes(root);
        // Printing the number of nodes aka the size of the tree
        System.out.println("- total number of nodes " + numberOfNodes);

        int numberOfAnimals = numberOfAnimals(root);
        int numberOfFacts = numberOfNodes - numberOfAnimals;
        // Printing the number of animals and the number of facts
        System.out.println("- total number of animals " + numberOfAnimals);
        System.out.println("- total number of statements " + numberOfFacts);

        int height = heightOfTree(root);
        int minDepth = minDepth(root);
        // Printing the maximum and minimum depth of the tree
        System.out.println("- height of the tree " + height);
        System.out.println("- minimum animal's depth " + minDepth);

        // Average animal's depth = sum of all animals' depths / number of animals
        System.out.println("- average animal's depth " +
                (double) sumOfDepthOfLeaves(root, 0) / numberOfAnimals);

    }

    // Counting total number of nodes in the tree
    private int totalNumberOfNodes(Node t) {
        int count = 1;
        if (t.getNo() != null) {        // Count all the left sub-nodes of node t
            count += totalNumberOfNodes(t.getNo());
        }
        if (t.getYes() != null) {       // Count all the right sub-nodes of node t
            count += totalNumberOfNodes(t.getYes());
        }
        return count;
    }

    // Counting total number of animals in the tree
    private int numberOfAnimals(Node t) {
        return listAnimalsTraverse(root).size();
    }

    // Compute the height of the tree - the number of nodes along the longest path
    // from the root node down to the farthest leaf node
    private int heightOfTree(Node t) {
        if (t == null) {
            return -1;
        } else {        // Finding maximum depth
            return 1 + Math.max(heightOfTree(t.getNo()), heightOfTree(t.getYes()));
        }
    }

    // Compute the minimum depth of the tree
    private int minDepth(Node t) {
        if (t == null) {
            return -1;
        } else {
            return 1 + Math.min(minDepth(t.getNo()), minDepth(t.getYes()));
        }
    }

    // Compute the total sum of depths of all the leaf nodes aka animals
    private int sumOfDepthOfLeaves(Node t, int depth) {
        if (t == null) {
            return 0;
        } else if (t.isAnimal()) {
            return depth;
        } else {
            return sumOfDepthOfLeaves(t.getNo(), depth + 1) +
                    sumOfDepthOfLeaves(t.getYes(), depth + 1);
        }
    }

    // Print the Knowledge Tree
    public void print() {
        System.out.println();
        System.out.println(" └ " + root.data);
        if (!root.isAnimal()) {
            printChildNode(root, "  ");
        }
    }

    private void printChildNode(Node t, String prefix) {
        if(t == null) {
            return;
        }

        if (t.yes != null) {
            System.out.println(prefix + "├ " + t.yes.data);
            if (t.yes.isAnimal()) {
            } else {
                printChildNode(t.yes, prefix + "│");
            }
        }

        if (t.no != null) {
            System.out.println(prefix + "└ " + t.no.data);
            if (t.no.isAnimal()) {
            } else {
                printChildNode(t.no, prefix + " ");
            }
        }
    }
}
