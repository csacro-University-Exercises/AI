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

        nodes = copy(nodes);
        //arc consistency
        AC3(nodes);
        //backtrack search
        nodes = recursiveBacktracking(copy(nodes));

        //output
        TreeMap<String, List<String>> sortedNodes = new TreeMap<String, List<String>>(nodes);
        for(Map.Entry<String, List<String>> entry: sortedNodes.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().get(0));
        }
    }

    public static void printCompleteOutput(HashMap<String, List<String>> map) {
        System.out.println("------------------------");
        TreeMap<String, List<String>> sortedNodes = new TreeMap<String, List<String>>(map);
        for(Map.Entry<String, List<String>> entry: sortedNodes.entrySet()) {
            System.out.println(entry.getKey() + " " + Arrays.toString(entry.getValue().toArray()));
        }
        System.out.println("------------------------\n");
    }

    private static boolean AC3(HashMap<String, List<String>> assignment) {
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
            if(!checkValues(ci, cj, assignment) || !checkValues(cj, ci, assignment)) {
                return false;
            }
        }
        return true;
    }

    private static HashMap<String, List<String>> recursiveBacktracking(HashMap<String, List<String>> assignment) {
        HashMap<String, List<String>> copiedAssignment;
        List<String> copiedNodeValues;

        if(!isBacktrackSearchNeeded(assignment)) {
            return assignment;
        }

        String node = selectMRV(assignment);
        List<String> oldNodeValues = assignment.get(node);
        for (String value: oldNodeValues) {
            copiedAssignment = copy(assignment);
            copiedNodeValues = new ArrayList<String>();
            copiedNodeValues.add(value);
            copiedAssignment.put(node, copiedNodeValues);
            if (AC3(copiedAssignment)) {
                HashMap<String, List<String>> result = recursiveBacktracking(copiedAssignment);
                if (result != null) {
                    return result;
                }
                assignment.put(node, oldNodeValues);
            }
        }
        return null;
    }

    private static String selectMRV(HashMap<String, List<String>> assignment) {
        String key = "";
        int keyValueCount = 0;
        int valueCount;
        for(HashMap.Entry<String, List<String>> entry: assignment.entrySet()) {
            if(keyValueCount <= 1) {
                key = entry.getKey();
                keyValueCount = entry.getValue().size();
            } else if( (valueCount=entry.getValue().size()) <= keyValueCount && valueCount > 1) {
                key = entry.getKey();
                keyValueCount = valueCount;
            }
        }
        return key;
    }

    private static HashMap<String, List<String>> copy(HashMap<String, List<String>> toCopy) {
        HashMap<String, List<String>> copy = new HashMap<String, List<String>>();
        for(HashMap.Entry<String, List<String>> entry: toCopy.entrySet()) {
            //copy List
            List<String> toCopyList = entry.getValue();
            List<String> copyList = new ArrayList<String>();
            for(String s: toCopyList) {
                copyList.add(s);
            }

            copy.put(entry.getKey(), copyList);
        }
        return copy;
    }
    private static LinkedList<String> copy(LinkedList<String> toCopy) {
        LinkedList<String> copy = new LinkedList<String>();
        for(String s: toCopy) {
            copy.add(s);
        }
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
    private static boolean checkValues(String ci, String cj, HashMap<String, List<String>> assignment) {
        int rIV = removeInconstistentValues(ci, cj, assignment);
        if(rIV == -1) {
            return false;
        }
        if(rIV == 1) {
            for (String s : neighbours.get(ci)) {
                constraints_queue.addLast(s);
            }
        }
        return true;
    }
    private static int removeInconstistentValues(String ci, String cj, HashMap<String, List<String>> assignment) {
        int removed = 0;
        List<String> x = assignment.get(ci);
        List<String> y = assignment.get(cj);

        if(y.size() == 1 && x.remove(y.get(0))) {
            if (x.isEmpty()) {
                removed = -1;
            } else {
                assignment.put(ci, x);
                removed = 1;
            }
        }
        return removed;
    }
}
