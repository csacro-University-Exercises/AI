import jdk.nashorn.api.tree.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CSP {

    private static int k;
    private static int anzNodes;
    private static int anzConstraints;
    private static HashMap<String, List<String>> nodes = new HashMap<String, List<String>>(); //assignment
    private static LinkedList<String> constraints = new LinkedList<String>(); //CSP
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
                    constraints.add(br.readLine());
                }

                linecount++;
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //backtrack search
        recursiveBacktracking(copy(nodes));

        //output
        TreeMap<String, List<String>> sortedNodes = new TreeMap<String, List<String>>(nodes);
        System.out.println(anzNodes);
        for(Map.Entry<String, List<String>> entry: sortedNodes.entrySet()) {
            System.out.println(entry.getKey() + entry.getValue().get(0));
        }
    }

    private static void AC3(HashMap<String, List<String>> assignment) {
        constraints_queue = copy(constraints);
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
            checkValues(ci, cj, assignment);
            checkValues(cj, ci, assignment);
        }
    }

    private static boolean recursiveBacktracking(HashMap<String, List<String>> assignment) {
        //arc consistency
        AC3(assignment);

        boolean success = false;
        if(!isBacktrackSearchNeeded(assignment)) {
            success = true;
        } else {
            String node = selectMRV(assignment);
            List<String> oldNodeValues = assignment.get(node);
            for (String value: oldNodeValues) {
                if (isConsistent(assignment, node, value)) {
                    List<String> nodeValues = new ArrayList<String>();
                    nodeValues.add(value);
                    assignment.replace(node, nodeValues);
                    boolean result = recursiveBacktracking(copy(assignment));
                    if (result) {
                        success = true;
                        break;
                    }
                    assignment.replace(node, oldNodeValues);
                }
            }
        }

        if(success) {
            nodes = assignment;
        }
        return success;
    }

    private static String selectMRV(HashMap<String, List<String>> assignment) {
        String key;
        int keyValueCount = 0;
        int valueCount;
        for(HashMap.Entry<String, List<String>> entry: assignment.entrySet()) {
            if( (valueCount=entry.getValue().size()) > keyValueCount) {
                key = entry.getKey();
                keyValueCount = valueCount;
            }
        }
        return key;
    }

    private static boolean isConsistent(HashMap<String, List<String>> assignment, String node, String value) {
        boolean ret = true;
        String[] splitConstraint;
        String c1;
        String c2;
        for(String constraint: constraints) {
            if(constraint.contains(node)) {
                splitConstraint = constraint.split(" ");
                c1 = splitConstraint[1];
                c2 = splitConstraint[2];
                if(c1.equals(node)) {
                    ret = checkConsistency(assignment.get(c2), value);
                } else {
                    ret = checkConsistency(assignment.get(c1), value);
                }
            }
            if(!ret) {
                break;
            }
        }
        return ret;
    }
    private static boolean checkConsistency(List<String> values, String comp) {
        if(values.contains(comp) && values.size() <= 1) {
            return false;
        }
        return true;
    }

    private static HashMap<String, List<String>> copy(HashMap<String, List<String>> toCopy) {
        HashMap<String, List<String>> copy = new HashMap<String, List<String>>();
        for(HashMap.Entry<String, List<String>> entry: toCopy.entrySet()) {
            //copy List
            List<String> toCopyList = entry.getValue();
            List<String> copyList = new ArrayList<String>();
            copyList.addAll(toCopyList);

            copy.put(entry.getKey(), copyList);
        }
        return copy;
    }
    private static LinkedList<String> copy(LinkedList<String> toCopy) {
        LinkedList<String> copy = new LinkedList<String>();
        copy.addAll(toCopy);
        return copy;
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
    private static void checkValues(String ci, String cj, HashMap<String, List<String>> assignment) {
        if(removeInconstistentValues(ci, cj, assignment)) {
            for (String s : neighbours.get(ci)) {
                constraints_queue.addLast(s);
            }
        }
    }
    private static boolean removeInconstistentValues(String ci, String cj, HashMap<String, List<String>> assignment) {
        boolean removed = false;
        List<String> x = assignment.get(ci);
        List<String> y = assignment.get(cj);

        if(y.size() == 1 && x.remove(y.get(0))) {
            assignment.put(ci, x);
            removed = true;
        }
        return removed;
    }
}
