/**
 * @author: Gruppe P: Carolin, Dominik
 */

//#define DOMJUDGE                                               /* uncomment this define to enable domjudge mode, which
//                                                                * disables any outputs that the one expected          */
#include <iostream>
#include <string>
#include <vector>
#include <cstdio>
#include <iomanip>
#include <set>

#ifndef DOMJUDGE
#include <fstream>
#endif

//Helper classes
class Node {
    public:
        unsigned int id;
        unsigned int heuristicValue;
        unsigned int pathCost = 0;

        Node() : id(0), heuristicValue(0) {}
        Node(unsigned int id, unsigned int h, unsigned int cost) : id(id), heuristicValue(h), pathCost(cost) {}

        friend std::ostream& operator<< (std::ostream& os, const Node& n) {
            os << "id: " << std::setw(3) << n.id << " | h: " << std::setw(3) << n.heuristicValue;
            os << " | pathcost: " << std::setw(3) << n.pathCost;
            os << " | sum: " << std::setw(3) << n.heuristicValue + n.pathCost;

            return os;
        }

        friend bool operator< (const Node &a, const Node &b) {
            if ((a.pathCost + a.heuristicValue) < (b.pathCost + b.heuristicValue)) {
                return true;
            } else if ((a.pathCost + a.heuristicValue) == (b.pathCost + b.heuristicValue)) {
                return a.id < b.id;
            } else {
                return false;
            }
        }
};

//Helper methods
void expand(const Node &node, std::set<Node, std::less<Node>> &fringe, const std::vector<Node> &nodes, const std::vector<std::vector<int>> &edges);
void loadGraph(std::vector<Node> &nodes, std::vector<std::vector<int>> &edges, std::istream &stream);

int main() {
    //////////////////
    /// READ GRAPH ///
    //////////////////
    std::vector<Node> nodes;
    std::vector<std::vector<int>> edges;

#ifndef DOMJUDGE
    std::cout << "Reading szenario in..."  << std::endl;
    std::ifstream input;
    input.open("../AStarInput2.txt");

    if (!input.is_open()) {
        std::cerr << "Unable to open file!" << std::endl;
        return -1;
    }

    loadGraph(nodes, edges, input);
#else
    loadGraph(nodes, edges, std::cin);
#endif

    std::set<unsigned int>          closedNodes;
    std::set<Node, std::less<Node>> fringe;

    Node node;

    fringe.insert(*nodes.begin());

    while(true) {
#ifndef DOMJUDGE/*
        std::cout << "Fringe: " << fringe.size() << std::endl;
        if(fringe.size() == 0) {
            return -1;
        }

        for (auto node : fringe) {
            std::cout << node << std::endl;
        }*/
#endif

        node = *fringe.begin();
        fringe.erase(fringe.begin());

        if(node.id == (nodes.size() - 1)) {
            std::cout << node.id << std::endl;
            break;
        }

        if(closedNodes.find(node.id) == closedNodes.end()) {
            std::cout << node.id << std::endl;
            closedNodes.insert(node.id);

            expand(node, fringe, nodes, edges);
        }
    }

    return 0;
}

/**
 * Loads the graph from the given stream.
 * @param nodes  Vector of nodes where the nodes will be written to.
 * @param edges  Vector of edges where the edges will get written to.
 * @param stream Stream which should be used to read from.
 */
void loadGraph(std::vector<Node> &nodes, std::vector<std::vector<int>> &edges, std::istream &stream) {
    std::string line;
    unsigned int numNodes;
    unsigned int numEdges;

    for (unsigned int lineCount = 0; stream; lineCount++) {
        std::getline(stream, line);
        if (!stream) {
            break;
        }

        if (lineCount == 0) {
            sscanf(line.c_str(), "%d %d", &numNodes, &numEdges);

            nodes.resize(numNodes);                                        /* reserve memory                         */
            edges.resize(numNodes);
            for (unsigned int i = 0; i < numNodes; i++) {
                edges.at(i).resize(numNodes, -1);
            }
        } else if (lineCount <= numNodes) {
            nodes.at(lineCount - 1).id = lineCount - 1;
            nodes.at(lineCount - 1).heuristicValue = stoi(line);
        } else if (lineCount <= numNodes + numEdges) {
            unsigned int startIndex;
            unsigned int endIndex;
            int cost;
            sscanf(line.c_str(), "%d %d %d", &startIndex, &endIndex, &cost);

            edges.at(startIndex).at(endIndex) = cost;
        }
    }

#ifndef DOMJUDGE
    for (unsigned int i = 0; i < nodes.size(); i++) {
        std::cout << nodes.at(i) << std::endl;
    }

    for (unsigned int i = 0; i < edges.size(); i++) {
        for (unsigned int b = 0; b < edges.at(i).size(); b++) {
            std::cout << std::setw(3) << edges.at(i).at(b) << " ";
        }
        std::cout << std::endl;
    }
#endif
}

/**
 * Expands the given node.
 * @param node   Node to expand.
 * @param fringe Fringe to insert the nodes.
 * @param nodes  Vector of all nodes.
 * @param edges  Cost matrix of the edges.
 */
void expand(const Node &node, std::set<Node, std::less<Node>> &fringe, const std::vector<Node> &nodes, const std::vector<std::vector<int>> &edges) {
    int cost;

    for(unsigned int i = 0; i < edges.at(node.id).size(); i++) {
        cost = edges.at(node.id).at(i);
        if(node.id != i && cost != -1) {
            fringe.insert(Node(i, nodes.at(i).heuristicValue, node.pathCost + cost));
        }
    }
}