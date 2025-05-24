/*
A road map for a number of cities is given as an adjacency matrix. Write a program that will be able to tell if any city is reachable from any other city (perhaps via other cities).

Входные данные
First line has the number of cities on a map N (1 ≤ N ≤ 1000). Each of the next N lines has N numbers (0 or 1, separated with spaces) – elements of the adjacency matrix for the graph corresponding to the road map.

Выходные данные
Your program should output YES if every city is reachable from every other city, or NO otherwise.

Входные данные
5
0 0 1 0 0
0 0 1 0 1
1 1 0 0 0
0 0 0 0 0
0 1 0 0 0

Выходные данные
NO
*/
import java.util.LinkedList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        int n;
        Scanner s = new Scanner(System.in);
        Graph<Integer, Integer> graph = new Graph<>();
        n = s.nextInt();
        for (int i = 0; i < n; i++) {
            graph.newVertex(i);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (s.nextInt() > 0) {
                    graph.newEdge(0, graph.getVertex(i), graph.getVertex(j));
                }
            }
        }
        int[] components = new int[n];
        int numberOfComponents = 1;
        for (int i = 0; i < n; i++) {
            if (components[i] == 0) {
                graph.AndreyTorgashinov_dfs(graph.getVertex(i), components, numberOfComponents);
                numberOfComponents++;
            }
        }
        if (numberOfComponents == 2) {
            System.out.println("YES");
        } else{
            System.out.println("NO");
        }
    }
}


class Edge <T> {
    protected T weight;
    protected Vertex from;
    protected Vertex to;
    Edge(Graph graph, Vertex from, Vertex to, T weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        from.edges.add(this);
    }
}


class Vertex <T> {
    protected LinkedList<Edge> edges;
    protected T key;
    protected int index;
    Vertex(Graph graph, T key) {
        edges = new LinkedList<>();
        this.key = key;
        this.index = graph.getSize();
    }
}


class Graph <V, E> {
    private LinkedList<Vertex> vertices;
    private LinkedList<Edge> edges;
    private int size;
    public void newVertex(V key) {
        this.vertices.add(new Vertex(this, key));
        this.size++;
    }
    public void newEdge(E weight, Vertex from, Vertex to) {
        this.edges.add(new Edge(this, from, to, weight));
    }
    public Vertex getVertex(V key) {
        for (Vertex v : this.vertices) {
            if (v.key == key) {
                return v;
            }
        }
        return null;
    }
    public int getSize() {
        return this.size;
    }
    public void AndreyTorgashinov_dfs(Vertex v, int[] components, int numberOfComponents) {
        components[v.index] = numberOfComponents;
        for (Object edge : v.edges) {
            Vertex u = ((Edge) edge).to;
            if (components[u.index] == 0) {
                AndreyTorgashinov_dfs(u, components, numberOfComponents);
            }
        }
    }
    Graph() {
        this.vertices = new LinkedList<>();
        this.edges = new LinkedList<>();
        this.size = 0;
    }
}