/*
Given some numbers, build a binary search tree (BST).

You must implement and use an AVL tree or a Red-Black tree. You implementation must be generic in the type of keys.

Input
Input starts with a line with one number N (0 < N <  = 105). The next line has N integer numbers.

Output
Start output with N — number of nodes in binary search tree.

In the next N lines output information about nodes (one node per line). For each node output integer value xi at node i, li (index of the left node or  - 1) and ri (index of the right node or  - 1).

In the final line output the index of the root node.

Node indexing starts with 1 and does not have to preserve input order.

Input
3
1 2 3

Output
3
2 2 3
1 -1 -1
3 -1 -1
1
*/
import java.util.*;
import static java.lang.Math.*;


public class Main {
    static int count = 1;
    static ArrayList<Node<Integer>> nodes = new ArrayList<>();
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        int n = s.nextInt();
        RedBlackTree<Integer> tree = new RedBlackTree<>();
        for (int i = 0; i < n; i++) {
            tree.insert(new Node<Integer>(s.nextInt()));
        }
        output(tree.root);
        System.out.println(n);
        for (Node<Integer> node : nodes) {
            int left = -1;
            if (!(node.leftChild instanceof Nil<Integer>) && (node.leftChild != null)) {
                left = node.leftChild.counter;
            }
            int right = -1;
            if (!(node.rightChild instanceof Nil<Integer>) && (node.rightChild != null)) {
                right = node.rightChild.counter;
            }
            System.out.printf("%d %d %d\n", node.key, left, right);
        }
        System.out.println(1);
    }
    public static void output(Node<Integer> node) {
        if (node instanceof Nil<Integer> || node == null) {
            return;
        }
        nodes.add(node);
        node.counter = count;
        count++;
        output(node.leftChild);
        output(node.rightChild);
    }
}


class RedBlackTree <T extends Comparable> {
    public Node<T> root;
    public void rotate(Node<T> newNode, Node<T> y) {
        y.parent = newNode.parent;
        if (newNode.parent == null || newNode.parent instanceof Nil<T>) {
            this.root = y;
        }
        else if (newNode == newNode.parent.leftChild) {
            newNode.parent.leftChild = y;
        }
        else {
            newNode.parent.rightChild = y;
        }
    }
    public void rightRotate(Node<T> newNode) {
        Node<T> y = newNode.leftChild;
        newNode.leftChild = y.rightChild;
        if (y.rightChild != null && !(y.rightChild instanceof Nil<T>)) {
            y.rightChild.parent = newNode;
        }
        rotate((Node<T>) newNode, (Node<T>) y);
        y.rightChild = newNode;
        newNode.parent = y;
    }
    public void leftRotate(Node<T> newNode) {
        Node<T> y = newNode.rightChild;
        newNode.rightChild = y.leftChild;
        if (y.leftChild != null && !(y.leftChild instanceof Nil<T>)) {
            y.leftChild.parent = newNode;
        }
        rotate((Node<T>) newNode, (Node<T>) y);
        y.leftChild = newNode;
        newNode.parent = y;
    }
    public void insert(Node<T> newNode) {
        Node<T> x = this.root;
        Node<T> y = new Nil<>();
        while (!(x instanceof Nil<T>) && (x != null)) {
            y = x;
            if (newNode.key.compareTo(x.key) < 0) {
                x = x.leftChild;
            }
            else {
                x = x.rightChild;
            }
        }
        newNode.parent = y;
        if (y instanceof Nil<T> || y == null) {
            this.root = newNode;
        }
        else if (newNode.key.compareTo(y.key) < 0) {
            y.leftChild = newNode;
        }
        else {
            y.rightChild = newNode;
        }
        insertFixup(newNode);
    }
    public void insertFixup(Node<T> newNode) {
        while (newNode.parent.color == Color.RED) {
            if (newNode.parent == newNode.parent.parent.leftChild) {
                Node<T> uncle = newNode.parent.parent.rightChild;
                if (uncle.color == Color.RED) {
                    newNode.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    newNode = newNode.parent.parent;
                }
                else {
                    if (newNode == newNode.parent.rightChild) {
                        newNode = newNode.parent;
                        leftRotate(newNode);
                    }
                    newNode.parent.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    rightRotate(newNode.parent.parent);
                }
            }
            else {
                Node<T> uncle = newNode.parent.parent.leftChild;
                if (uncle.color == Color.RED) {
                    newNode.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    newNode = newNode.parent.parent;
                }
                else {
                    if (newNode == newNode.parent.leftChild) {
                        newNode = newNode.parent;
                        rightRotate(newNode);
                    }
                    newNode.parent.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    leftRotate(newNode.parent.parent);
                }
            }
        }
        this.root.color = Color.BLACK;
    }
    RedBlackTree() {
        this.root = new Nil<T>();
    }
}


class Node <T extends Comparable> {
    public T key;
    public int counter;
    public Color color;
    public Node<T> parent;
    public Node<T> leftChild;
    public Node<T> rightChild;
    Node() {
        this.key = null;
        this.color = Color.BLACK;
        this.parent = null;
        this.leftChild = null;
        this.rightChild = null;
    }
    Node(T key) {
        this.key = key;
        this.color = Color.RED;
        this.parent = new Nil<T>();
        this.leftChild = new Nil<T>();
        this.rightChild = new Nil<T>();
    }
}


class Nil<T extends Comparable> extends Node<T> {
    Nil() {
        super();
    }
}


enum Color {
    RED,
    BLACK
}