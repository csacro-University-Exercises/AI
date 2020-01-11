import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Resolution {

    public static void main(String[] args) {
        LinkedList<int[]> clausesReadIn = new LinkedList<int[]>();

        //read in
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] br_buf;
        int anzAtom = 0;
        int anzClauses = 0;
        int readClauses = 0;
        int atom;
        try {
            while (br.ready()) {
                br_buf = br.readLine().split(" ");
                switch (br_buf[0]) {
                    case "c":
                        //do nothing (is a comment)
                        break;
                    case "p":
                        if(br_buf.length == 4 && br_buf[1].equals("cnf")) {
                            anzAtom = Integer.parseInt(br_buf[2]);
                            anzClauses = Integer.parseInt(br_buf[3]);
                        }
                        break;
                    default:
                        if(readClauses == anzClauses) {
                            break;
                        }
                        readClauses++;
                        int[] clause = new int[anzAtom];
                        for(int i=0; i<br_buf.length && !br_buf[i].equals("0"); i++) {
                            atom = Integer.parseInt(br_buf[i]);
                            clause[Math.abs(atom)-1] = Integer.signum(atom);
                        }
                        clausesReadIn.add(clause);
                }
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //algorithm
        System.out.println(isInconsistend(clausesReadIn));
        System.exit(0);
    }

    private static boolean isInconsistend(LinkedList<int[]> clausesAll) {
        LinkedList<int[]> clausesNew = new LinkedList<int[]>();
        while(true) {
            int[] ci;
            int[] cj;
            for (int i=0; i<clausesAll.size(); i++) {
                ci = clausesAll.get(i);
                for (int j=i+1; j<clausesAll.size(); j++) {
                    cj = clausesAll.get(j);
                    LinkedList<int[]> resolvents = plResolve(ci, cj);
                    if (isEmptyClauseElem(resolvents)) {
                        return true;
                    }
                    union(clausesNew, resolvents);
                }
            }
            if(clausesAll.containsAll(clausesNew)) {
                return false;
            }
            union(clausesAll, clausesNew);
        }
    }

    private static LinkedList<int[]> plResolve(int[] ci, int[] cj) {
        LinkedList<int[]> ret = new LinkedList<int[]>();
        LinkedList<Integer> resolvePositions = getResolvePositions(ci, cj);
        if(resolvePositions.size() == 1) {
            int[] resolvent = new int[ci.length];
            for(int a=0; a<ci.length; a++) {
                resolvent[a] = ci[a] + cj[a];
            }
            ret.add(resolvent);
        }
        return ret;
    }

    private static LinkedList<Integer> getResolvePositions(int[] ci, int[] cj) {
        LinkedList<Integer> positions = new LinkedList<Integer>();
        for (int p=0; p<ci.length; p++) {
            if( (ci[p] == 1 && cj[p] == -1) || (ci[p] == -1 && cj[p] == 1)) {
                positions.add(p);
            }
        }
        return positions;
    }

    private static void union(LinkedList<int[]> l1Union, LinkedList<int[]> l2ToUnite) {
        for(int[] valToUnite: l2ToUnite) {
            if(!l1Union.contains(valToUnite)) {
                l1Union.add(valToUnite);
            }
        }
    }

    private static boolean isEmptyClauseElem(LinkedList<int[]> list) {
        int count;
        for(int[] clause: list) {
            count = 0;
            for(int i=0; i<clause.length; i++) {
                if(clause[i] != 0) {
                    break;
                }
                count++;
            }
            if(count == clause.length) {
                return true;
            }
        }
        return false;
    }
}
