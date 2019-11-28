import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CSP {

    private static int k;
    private static int anzNodes;
    private static int anzConstraints;
    //TODO: change nodes (values: int[]) ? --> change read in and arc consistency and output
    private static LinkedHashMap<String, int[]> nodes = new LinkedHashMap<String, int[]>();
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
                    nodes.put(br_buf[0], posNumToBool(br_buf[1]));
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
        //TODO: check if needed, implement algorithm

        //output
        //TODO: sort
        System.out.println(anzNodes);
        for(HashMap.Entry<String, int[]> entry: nodes.entrySet()) {
            System.out.println(entry.getKey() + boolToPosNum(entry.getValue()));
        }
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
        int[] x = nodes.get(ci);
        int[] y = nodes.get(cj);
        if(y[0] == 1) {
            for (int i = 1; i < k + 1; i++) {
                if (y[i] == 1 && x[i] == 1) {
                    x[i] = 0;
                    x[0]--;
                    removed = true;
                    break;
                }
            }
        }
        return removed;
    }

    private static int[] posNumToBool(String nums) {
        int count = 0;
        String[] splitNums = nums.split(" ");
        int[] ret = new int[k+1];
        for (String s : splitNums) {
            ret[Integer.parseInt(s)] = 1;
            count++;
        }
        ret[0] = count;
        return ret;
    }
    private static String boolToPosNum(int[] bool) {
        String ret = "";
        for(int i=1; i<bool.length; i++) {
            if (bool[i] == 1) {
                ret += " " + i;
            }
        }
        return ret;
    }
}
