/*
Given an oriented graph, determine if it has a cycle of negative weight and print it (if exists).

Input
Input's first line has number N (1 ≤ N ≤ 100) — number of vertices in the graph. Each of the next N lines contains N numbers — all representing an adjacency matrix. All weights are strictly less than 10000 by absolute value. If there is no edge, corresponding value will be exactly 100000.

Output
First line should YES if there exists a negative cycle, or NO otherwise. If the cycle exists, second line should should contain the number of vertices in that cycle and third line should contain indices of those vertices (in cycle order).

Input
2
0 -1
-1 0

Output
YES
2
2 1 
*/
#include <string>
#include <stack>
#include <queue>
#include <algorithm>
#include <set>
#include <map>
#include <list>
#include <cmath>
#include <vector>
#include <memory>
#include <iostream>
#include <iomanip>
#include <stdio.h>
#include <cmath>
using namespace std;
using ll = long long;
using ull = unsigned long long;
using db = double;
using uint = unsigned int;


const int INF = 1e9;


template <typename T> class Vertex {
public:
    T key;
    int index;
    Vertex(T key, int index) {
        this->key = key;
        this->index = index;
    }
};


template <typename V, typename  E> class Edge {
public:
    E weight;
    shared_ptr<Vertex<V>> from;
    shared_ptr<Vertex<V>> to;
    Edge(shared_ptr<Vertex<V>> from, shared_ptr<Vertex<V>> to, E weight) {
        this->from = from;
        this->to = to;
        this->weight = weight;
    }
};


template <typename V, typename E> class Graph {
public:
    list<shared_ptr<Vertex<V>>> vertices;
    list<shared_ptr<Edge<V, E>>> edges;
    int size;
    shared_ptr<Vertex<V>> getVertex(V key) {
        for (auto it = this->vertices.begin(); it != this->vertices.end(); it++) {
            shared_ptr<Vertex<V>> vertex = *it;
            if (vertex->key == key) {
                return vertex;
            }
        }
        return nullptr;
    }
    void addVertex(shared_ptr<Vertex<V>> vertex) {
        this->vertices.push_back(vertex);
        this->size++;
    }
    void addEdge(shared_ptr<Edge<V, E>> edge) {
        this->edges.push_back(edge);
    }
    void newVertex(V key) {
        shared_ptr<Vertex<V>> vertex = make_shared<Vertex<V>>(key, this->size++);
        this->vertices.push_back(vertex);
    }
    void newEdge(V from, V to, E weight) {
        shared_ptr<Vertex<V>> fromVertex = getVertex(from);
        shared_ptr<Vertex<V>> toVertex = getVertex(to);
        shared_ptr<Edge<V, E>> edge = make_shared<Edge<V, E>>(fromVertex, toVertex, weight);
        this->edges.push_back(edge);
    }
    int getSize() {
        return this->size;
    }
    bool relax(shared_ptr<Edge<V, E>> edge, shared_ptr<int[]> distance, shared_ptr<int[]> path) {
        if (distance[edge->to->index] > distance[edge->from->index] + edge->weight) {
            distance[edge->to->index] = distance[edge->from->index] + edge->weight;
            path[edge->to->index] = edge->from->index;
            return true;
        }
        return false;
    }
    void printSycle(shared_ptr<int[]> path, int last) {
        stack<int> cycle;
        for (int v = path[last]; v != last; v = path[v]) {
            cycle.push(v);
        }
        cycle.push(last);
        cout << cycle.size() << "\n";
        while (!cycle.empty()) {
            int vertex = cycle.top();
            cout << vertex + 1 << " ";
            cycle.pop();
        }
        cout << "\n";
    }
    bool AndreyTorgashinov_sp(int s) {
        shared_ptr<int[]> distance(new int[this->size]);
        shared_ptr<int[]> path(new int[this->size]);
        for (int i = 0; i < this->size; i++) {
            if (i != s) {
                distance[i] = INF;
            }
            else {
                distance[i] = 0;
            }
            path[i] = i;
        }
        for (int i = 0; i < this->size; i++) {
            for (auto it = this->edges.begin(); it != this->edges.end(); it++) {
                shared_ptr<Edge<V, E>> edge = *it;
                if (distance[edge->from->index] != INF) {
                    relax(edge, distance, path);
                }
            }
        }
        for (auto it = this->edges.begin(); it != this->edges.end(); it++) {
            shared_ptr<Edge<V, E>> edge = *it;
            if (distance[edge->from->index] != INF) {
                if (relax(edge, distance, path)) {
                    cout << "YES\n";
                    int from = edge->from->index;
                    for (int i = 0; i < this->size - 1; i++) {
                        from = path[from];
                    }
                    printSycle(path, from);
                    return false;
                }
            }
        }
        return true;
    }
    Graph() {
        this->size = 0;
    }
};


int main() {
    ios::sync_with_stdio(0);
    cin.tie();
    Graph<int, int> graph;
    int n;
    cin >> n;
    for (int i = 1; i <= n; i++) {
        graph.newVertex(i);
    }
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= n; j++) {
            int weight;
            cin >> weight;
            if (weight != 100000) {
                graph.newEdge(i, j, weight);
            }
        }
    }
    int i;
    for (i = 0; i < n; i++) {
        if (!graph.AndreyTorgashinov_sp(i)) {
            break;
        }
    }
    if (i == n) {
        cout << "NO\n";
    }
    return 0;
}