import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Euclid {

    static int startnum1;
    static int startnum2;
    static LinkedList<State> stateTable = new LinkedList<>();

    public static void main(String[] args) {
        //read in
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String[] line = br.readLine().split(" ");
            startnum1 = Integer.parseInt(line[0]);
            startnum2 = Integer.parseInt(line[1]);
        } catch (IOException e) {
            System.exit(1);
        }

        //algorithm
        State initialState = new State(startnum1, startnum2, -1);
        initialState.v = maxval(initialState, -1 , 1);
        if (initialState.v == -1) {
            System.out.println("Ollie wins");
        } else {
            System.out.println("Stan wins");
        }

        System.exit(0);
    }

    private static int maxval(State state, int alpha, int beta) {
        if(terminal(state)) {
            return state.v;
        }

        int index;
        State there;
        index = stateTable.indexOf(state);
        if(index >= 0) {
            there = stateTable.get(index);
            if(state.playerLeadingToState == there.playerLeadingToState) {
                state.v = there.v;
                return state.v;
            } else {
                state.v = -there.v;
                return state.v;
            }
        } else {
            stateTable.add(state);
        }

        LinkedList<State> successor = calcSuccessors(state);
        state.v = -1;
        for(State s: successor) {
            state.v = max(state.v, minval(s, alpha, beta));
            if(state.v >= beta) {
                return state.v;
            }
            alpha = max(alpha, state.v);
        }
        return state.v;
    }

    private static int minval(State state, int alpha, int beta) {
        if(state.v != 0 || terminal(state)) {
            return state.v;
        }

        int index;
        State there;
        index = stateTable.indexOf(state);
        if(index >= 0) {
            there = stateTable.get(index);
            if(state.playerLeadingToState == there.playerLeadingToState) {
                state.v = there.v;
                return state.v;
            } else {
                state.v = -there.v;
                return state.v;
            }
        } else {
            stateTable.add(state);
        }

        LinkedList<State> successor = calcSuccessors(state);
        state.v = 1;
        for(State s: successor) {
            state.v = min(state.v, maxval(s, alpha, beta));
            if(state.v <= alpha) {
                return state.v;
            }
            beta = min(beta, state.v);
        }
        return state.v;
    }

    private static class State {
        int bignum;
        int smallnum;
        int playerLeadingToState; //-1||0->Stan, 1->Ollie
        int v; //1->Stan wins, -1->Ollie wins, 0->not calculated yet

        public State(int num1, int num2, int playerLeadingToState) {
            this.bignum = max(num1, num2);
            this.smallnum = min(num1, num2);
            this.playerLeadingToState = playerLeadingToState;
            v = 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof  State && ((State) obj).bignum == this.bignum && ((State) obj).smallnum == this.smallnum) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return bignum + " " + smallnum + " move: " + playerLeadingToState + " v: " + v;
        }
    }

    private static boolean terminal(State state) {
        if(state.smallnum == 0) {
            if (state.playerLeadingToState == 1) {
                state.v = -1;
            } else {
                state.v = 1;
            }
            return true;
        }
        return false;
    }

    private static LinkedList<State> calcSuccessors(State state) {
        LinkedList<State> successor = new LinkedList<>();
        for(int i = 1; i<=state.bignum/state.smallnum; i++) {
            successor.add(new State(state.bignum - i*state.smallnum, state.smallnum, (state.playerLeadingToState+1)%2));
        }
        return successor;
    }

    private static int max(int i, int j) {
        if (i > j) {
            return i;
        } else {
            return j;
        }
    }
    private static int min(int i, int j) {
        if (i < j) {
            return i;
        } else {
            return j;
        }
    }
}
