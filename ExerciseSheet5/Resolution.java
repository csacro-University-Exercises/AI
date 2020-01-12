import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Resolution {
    static int[] emptyClause;

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
                            emptyClause = new int[anzClauses];
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
                    if (isElemInList(emptyClause, resolvents)) {
                        return true;
                    }
                    union(clausesNew, resolvents);
                }
            }
            if(isFirstSubsetOfSecond(clausesNew, clausesAll)) {
                return false;
            }
            union(clausesAll, clausesNew);
        }
    }

    private static LinkedList<int[]> plResolve(int[] ci, int[] cj) {
        LinkedList<int[]> ret = new LinkedList<int[]>();
        Integer resolvePosition = getResolvePositions(ci, cj);
        if(resolvePosition != null) {
            int[] resolvent = new int[ci.length];
            for(int a=0; a<ci.length; a++) {
                if(a == resolvePosition) {
                    resolvent[a] = 0;
                } else if (ci[a] == 1 || cj[a] == 1) {
                    resolvent[a] = 1;
                } else if (ci[a] == -1 || cj[a] == -1) {
                    resolvent[a] = -1;
                }
            }
            ret.add(resolvent);
        }
        return ret;
    }

    private static Integer getResolvePositions(int[] ci, int[] cj) {
        Integer position = null;
        for (int p=0; p<ci.length; p++) {
            if( (ci[p] == 1 && cj[p] == -1) || (ci[p] == -1 && cj[p] == 1)) {
                if(position == null) {
                    position = p;
                } else {
                    return null;
                }
            }
        }
        return position;
    }

    private static void union(LinkedList<int[]> l1Union, LinkedList<int[]> l2ToUnite) {
        for(int[] valToUnite: l2ToUnite) {
            if(!isElemInList(valToUnite, l1Union)) {
                l1Union.add(valToUnite);
            }
        }
    }

    private static boolean isElemInList(int[] elem, LinkedList<int[]> list) {
        for(int[] clause: list) {
            if(areArraysEqual(clause, elem)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFirstSubsetOfSecond(LinkedList<int[]> subSet, LinkedList<int[]> set) {
        boolean found = false;
        for(int[] subSetClause: subSet) {
            for(int[] setClause: set) {
                found = areArraysEqual(subSetClause, setClause);
                if(found) {
                    break;
                }
            }
            if(!found) {
                return false;
            } else {
                found = false;
            }
        }
        return true;
    }

    private static boolean areArraysEqual(int[] ar1, int[] ar2) {
        for(int i=0; i<ar1.length; i++) {
            if(ar1[i] != ar2[i]) {
                return false;
            }
        }
        return true;
    }
}
