import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class SudokuEC {

    private static int k;
    private static int anzNodes;
    private static int anzConstraints;
    private static LinkedHashMap<String, int[]> nodes = new LinkedHashMap<String, int[]>();
    private static LinkedList<String> constraints_queue = new LinkedList<String>();
    private static HashMap<String, ArrayList<String>> neighbours = new HashMap<String, ArrayList<String>>();

    public static void main(String[] args) {
        //read in Sudoku puzzle
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
        //TODO: algorithm not solving completely
        String constraint;
        String[] splitConstraint;
        String ci;
        String cj;
        ArrayList<String> neighbours_cj;
        ArrayList<String> neighbours_ci;
        while(!constraints_queue.isEmpty()) {
            constraint = constraints_queue.removeFirst();
            splitConstraint = constraint.split(" ");
            ci = splitConstraint[1];
            cj = splitConstraint[2];
            neighbours_cj = neighbours.get(cj);
            if(neighbours_cj == null) {
                neighbours_cj = new ArrayList<String>();
            }
            neighbours_cj.add(constraint);
            if(removeInconstistentValues(ci, cj)) {
                neighbours_ci = neighbours.get(ci);
                if(neighbours_ci != null) {
                    for (String s : neighbours_ci) {
                        constraints_queue.addFirst(s);
                    }
                }
            }
        }

        //output
        System.out.println(anzNodes);
        for(HashMap.Entry<String, int[]> entry: nodes.entrySet()) {
            System.out.println(entry.getKey() + boolToPosNum(entry.getValue()));
        }
    }

    private static boolean removeInconstistentValues(String ci, String cj) {
        boolean removed = false;
        int[] x = nodes.get(ci);
        int[] y = nodes.get(cj);
        for(int i=1; i<k+1; i++) {
            if(y[0]==1 && x[i]==1 && y[i]==1) {
                x[i] = 0;
                x[0]--;
                removed = true;
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
