import jdk.nashorn.api.tree.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CSP {

    private static int k;
    private static int anzNodes;
    private static int anzConstraints;
    private static HashMap<String, List<String>> nodes = new HashMap<String, List<String>>();
    private static LinkedList<String> constraints_queue = new LinkedList<String>();
    private static HashMap<String, ArrayList<String>> neighbours = new HashMap<String, ArrayList<String>>();

    public static void main(String[] args) {
        //read in constraint graph
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] br_buf;
        int linecount = 0;
        try {
            while (br.ready()) {
                if(linecount == 0) {
                    anzNodes = Integer.parseInt(br.readLine());
                    k = (int) Math.sqrt(anzNodes);
                } else if(linecount <= anzNodes){
                    br_buf = br.readLine().split(" ", 2);
                    nodes.put(br_buf[0], Arrays.asList(br_buf[1].split(" ")));
                } else if(linecount == anzNodes+1) {
                    anzConstraints = Integer.parseInt(br.readLine());
                } else {
                    constraints_queue.add(br.readLine());
                }

                linecount++;
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //arc consistency
        String constraint;
        String[] splitConstraint;
        String ci;
        String cj;
        while(!constraints_queue.isEmpty()) {
            constraint = constraints_queue.removeFirst();
            splitConstraint = constraint.split(" ");
            ci = splitConstraint[1];
            cj = splitConstraint[2];

            addNeighbour(cj, constraint);
            addNeighbour(ci, constraint);
            checkValues(ci, cj);
            checkValues(cj, ci);
        }

        //backtrack search
        if(isBacktrackSearchNeeded(nodes)) {
            recursiveBacktracking();
        }

        //output
        TreeMap<String, List<String>> sortedNodes = new TreeMap<String, List<String>>(nodes);
        System.out.println(anzNodes);
        for(Map.Entry<String, List<String>> entry: sortedNodes.entrySet()) {
            System.out.println(entry.getKey() + entry.getValue().get(0));
        }
    }

    private static boolean recursiveBacktracking() {
        //TODO: paramlist (have to give copied data structures because of recursion)

        //TODO: var <- SELECT-UNASSIGNED-VARIABLE(VARIABLES[csp], assignment, csp)
        for() { //TODO: for each value in ORDER-DOMAIN-VALUES(var, assignment, csp)
            if() { //TODO: if value is consistent with assignment given CONSTRAINTS[csp]
                //TODO: add {var = value} to assignment
                boolean result = recursiveBacktracking();
                if (result) {
                    //TODO: global data structure = copied data structure
                    return true;
                }
                //TODO: remove {var = value} from assignment
            }
        }
        return false;
    }

    private static boolean isBacktrackSearchNeeded(HashMap<String, List<String>> map) {
        for(HashMap.Entry<String, List<String>> entry: map.entrySet()) {
            if(entry.getValue().size() > 1) {
                return true;
            }
        }
        return false;
    }

    private static void addNeighbour(String c, String constraint) {
        ArrayList <String> neighbours_c = neighbours.get(c);
        if(neighbours_c == null) {
            neighbours_c = new ArrayList<String>();
        }
        neighbours_c.add(constraint);
        neighbours.put(c, neighbours_c);
    }
    private static void checkValues(String ci, String cj) {
        if(removeInconstistentValues(ci, cj)) {
            for (String s : neighbours.get(ci)) {
                constraints_queue.addLast(s);
            }
        }
    }
    private static boolean removeInconstistentValues(String ci, String cj) {
        boolean removed = false;
        List<String> x = nodes.get(ci);
        List<String> y = nodes.get(cj);

        if(y.size() == 1 && x.remove(y.get(0))) {
            nodes.put(ci, x);
            removed = true;
        }
        return removed;
    }
}
