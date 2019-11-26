import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class AStar {

    private static int n = 0;
    private static int m = 0;
    private static LinkedList<Node> nodes = new LinkedList<Node>();
    private static LinkedList<Node> fringe = new LinkedList<Node>();
    private static LinkedList<Integer> closed = new LinkedList<Integer>();
    private static boolean goalNode = false;

    public static void main(String[] args) {
        //read in graph
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int linecount = 0;
        int nodeId_buf;
        ArrayList<Integer> g_buf;
        String br_buf;
        String[] br_buf_ar;
        Integer cost_buf;
        Integer oldcost_buf;
        try {
            while (br.ready()) {
                br_buf = br.readLine();
                br_buf_ar = br_buf.split(" ");

                if(linecount==0) {
                    n = Integer.parseInt(br_buf_ar[0]);
                    m = Integer.parseInt(br_buf_ar[1]);
                } else if(linecount <= n) {
                    nodes.addLast(new Node(linecount-1, Integer.parseInt(br_buf_ar[0])));
                } else if(linecount <= n+m) {
                    nodeId_buf = Integer.parseInt(br_buf_ar[1]); //nach
                    g_buf = nodes.get(Integer.parseInt(br_buf_ar[0])).g; //von.g

                    while (nodeId_buf >= g_buf.size()) {
                        //fill list until nodeId_buf-1
                        g_buf.add(null);
                    }
                    cost_buf = Integer.parseInt(br_buf_ar[2]);
                    oldcost_buf = g_buf.get(nodeId_buf);
                    if ( oldcost_buf==null || oldcost_buf>cost_buf) {
                        g_buf.set(nodeId_buf, cost_buf);
                    }
                }

                linecount++;
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //A* search
        Integer cost;
        Node node;

        expand(0,0);
        while(!goalNode && !fringe.isEmpty()) {
            Collections.sort(fringe);
            node = fringe.removeFirst();

            if(node.id == (n-1)) {
                goalNode = true;
                System.out.println(node.id);
                break;
            }
            if(!closed.contains(node.id)) {
                System.out.println(node.id);
                closed.add(node.id);
                for(int i=0; i<node.g.size(); i++) {
                    cost = node.g.get(i);
                    if(cost != null) {
                        expand(i, node.pathcost + cost);
                    }
                }
            }

        }
        System.exit(0);
    }

    private static void expand(int id, int c) {
        Node node = nodes.get(id).copy(c);
        fringe.add(node);
    }

    private static class Node implements Comparable<Node> {
        int id;
        int h;
        ArrayList<Integer> g = new ArrayList<Integer>();
        int pathcost = 0;

        public Node(int id, int h) {
            this.id = id;
            this.h = h;
        }

        public Node copy(int c) {
            Node n = new Node(this.id, this.h);
            for(Integer i: this.g) {
                n.g.add(i);
            }
            n.pathcost = c;
            return n;
        }

        @Override
        public int compareTo(Node o) {
            return ((this.pathcost+this.h) - (o.pathcost+o.h));
        }

        /*
        @Override
        public String toString() {
            return ("id: " + this.id + " | h: " + this.h + " | pathcost: " + this.pathcost);
        }
        */
    }
}
