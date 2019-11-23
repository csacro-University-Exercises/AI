import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class SudokuM {

    private static int k;
    private static LinkedList<String> constraints = new LinkedList<String>();

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

        //data and constraints
        k = puzzle.size();
        int c;
        System.out.println(k*k);
        for(int i=0; i<k; i++) {
            for(int j=0; j<k; j++) {
                c = Character.getNumericValue(puzzle.get(i).charAt(j));
                if(c!=0) {
                    System.out.println(arrCords(i, j) + " " + c);
                } else {
                    System.out.println(arrCords(i, j) + " 1 2 3 4 5 6 7 8 9");
                }
                generateLineRowConstraints(i, j);
                generateBlockConstraints(i, j, i%3, j%3);
            }
        }
        System.out.println(constraints.size());
        for(String s: constraints) {
            System.out.println(s);
        }


    }

    private static int min(int a, int b) {
        if (a<b) {
            return a;
        } else {
            return b;
        }
    }
    private static void generateLineRowConstraints(int posi, int posj) {
        for (int i=min(posi+1, posj+1); i < k; i++) {
            if (i > posj) {
                constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi, i));
            }
            if (i > posi) {
                constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(i, posj));
            }
        }
    }
    private static void generateBlockConstraints(int posi, int posj, int blocki, int blockj) {
        if(blocki==0 && blockj==0) {
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+1, posj+2));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+2, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+2, posj+2));
        } else if(blocki==1 && blockj==0) {
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+1, posj+2));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-1, posj+2));
        } else if(blocki==2 && blockj==0) {
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-1, posj+2));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-2, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-2, posj+2));
        } else if(blocki==0 && blockj==1) {
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+2, posj+1));
        } else if(blocki==1 && blockj==1) {
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi+1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-1, posj+1));
        } else if(blocki==2 && blockj==1) {
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-1, posj+1));
            constraints.add(1 + " " + arrCords(posi, posj) + " " + arrCords(posi-2, posj+1));
        }
    }

    private static String arrCords(int i, int j) {
        return ((i + 1) + "-" + (j + 1));
    }
}
