import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class SudokuEC {

    private static int k;

    public static void main(String[] args) {
        //read in Sudoku puzzle
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        LinkedList<String> puzzle = new LinkedList<String>();
        try {
            while (br.ready()) {
                puzzle.add(br.readLine());
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //interpret data
        k = puzzle.size();
        Node[][] nodes = new Node[k][k];
        System.out.println(k*k);
        for(int i=0; i<k; i++) {
            for (int j = 0; j < k; j++) {
                nodes[i][j] = new Node(Character.getNumericValue(puzzle.get(i).charAt(j)));
                System.out.println((i + 1) + "-" + (j + 1) + nodes[i][j].toString());
            }
        }

        //generate constraints
        //TODO

    }

    private static class Node {
        boolean[] posVal = new boolean[k];

        public Node(int pos) {
            if(pos != 0) {
                posVal[pos-1] = true;
            } else {
                for(int i=0; i<k; i++) {
                    posVal[i] = true;
                }
            }
        }

        @Override
        public String toString() {
            String ret = "";
            for(int i=0; i<k; i++) {
                if(posVal[i]) {
                    ret += " " + (i+1);
                }
            }
            return ret;
        }
    }
}
