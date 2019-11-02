import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Gruppe P: Carolin, Dominik
 */

public class SokoBFS {

    public static void main(String[] args) {
        //read in labyrinth (upper left corner is (0,0) and lower right corner is (n,m))
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String br_buf;
        int pos_buf;
        int linecount = 0;
        ArrayList<String> labyrinth = new ArrayList<String>();
        ComparablePoint startpos = null;
        ArrayList<ComparablePoint> startboxpos = new ArrayList<ComparablePoint>();
        try {
            while (br.ready()) {
                br_buf = br.readLine();
                if((pos_buf = br_buf.indexOf('@')) >= 0) {
                    startpos = new ComparablePoint(linecount , pos_buf);
                }
                if((pos_buf = br_buf.indexOf('$')) >= 0) {
                    startboxpos.add(new ComparablePoint(linecount , pos_buf));
                }
                labyrinth.add(br_buf);
                linecount++;
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //Search
        if(startpos != null && !startboxpos.isEmpty()) {
            State startstate = new State(startpos, startboxpos);
            ArrayList<State> visited = new ArrayList<State>();
            visited.add(startstate);
            int size;

            while(true) {
                size = visited.size();
                for (int i=0; i<size; i++) {
                    State s = visited.get(i);
                    checkState(goNorth(s, labyrinth), visited, labyrinth);
                    checkState(goSouth(s, labyrinth), visited, labyrinth);
                    checkState(goWest(s, labyrinth), visited, labyrinth);
                    checkState(goEast(s, labyrinth), visited, labyrinth);
                }
            }
        }
        System.exit(1);
    }

    private static void checkState(State state, ArrayList<State> visited, ArrayList<String> lab) {
        if(state != null) {
            if(!isInList(state, visited)) {
                visited.add(state);
            }
            if (isGoal(state, lab)) {
                System.out.println(state.steps);
                System.exit(0);
            }
        }
    }

    private static State move (State curstate, int dx, int dy, ArrayList<String> lab, String move) {
        State ret = curstate.copy();
        ret.steps += move;
        ret.pos.translate(dx, dy);

        if(!isPassable(ret.pos, lab)){
            return null;
        } else {
            for(ComparablePoint box: ret.boxpos) {
                if(ret.pos.compareTo(box) == 0) {
                    box.translate(dx, dy);
                    if (!isPassable(box, lab)) {
                        return null;
                    } else {
                        for(ComparablePoint boxcomp: ret.boxpos) {
                            if(box.compareTo(boxcomp) == 0) {
                                return null;
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(ret.boxpos);
        //ret.boxpos.sort(ComparablePoint::compareTo);
        return ret;
    }
    private static boolean isPassable(ComparablePoint point, ArrayList<String> lab) {
        if( point.x>=0 && point.x<lab.size() && point.y>=0 && point.y<lab.get(0).length()
                && lab.get(point.x).charAt(point.y) != '#' ) {
            return true;
        }
        return false;
    }
    private static State goNorth(State curstate, ArrayList<String> lab) {
        return move(curstate, -1, 0, lab, "U");
    }
    private static State goSouth(State curstate, ArrayList<String> lab) {
        return move(curstate, 1, 0, lab, "D");
    }
    private static State goEast(State curstate, ArrayList<String> lab) {
        return move(curstate, 0, 1, lab, "R");
    }
    private static State goWest(State curstate, ArrayList<String> lab) {
        return move(curstate, 0, -1, lab, "L");
    }

    private static boolean isGoal(State state, ArrayList<String> lab) {
        for(ComparablePoint box: state.boxpos) {
            if(lab.get(box.x).charAt(box.y) != '.') {
                return false;
            }
        }
        return true;
    }

    private static boolean isInList(State state, ArrayList<State> stateList) {
        for(State s: stateList) {
            if(state.pos.equals(s.pos) && state.boxpos.equals(s.boxpos)) {
                return true;
            }
        }
        return false;
    }

    static class State {
        ComparablePoint pos;
        ArrayList<ComparablePoint> boxpos;
        String steps;

        public State() {
            super();
            steps = "";
        }
        public State(ComparablePoint pos, ArrayList<ComparablePoint> boxpos) {
            this.pos = pos;
            this.boxpos = boxpos;
            steps = "";
            Collections.sort(this.boxpos);
            //this.boxpos.sort(ComparablePoint::compareTo);
        }

        public State copy() {
            return new State(this.pos, this.boxpos);
        }
    }

    static class ComparablePoint extends Point implements Comparable {

        public ComparablePoint(int x, int y) {
            super(x,y);
        }

        @Override
        public int compareTo(Object o) {
            if(o != null) {
                if(o instanceof ComparablePoint) {
                    if(this.x > ((ComparablePoint)o).x) {
                        return 1;
                    } else if(this.x < ((ComparablePoint)o).x) {
                        return -1;
                    } else {
                        if(this.y > ((ComparablePoint)o).y) {
                            return 1;
                        } else if(this.y < ((ComparablePoint)o).y) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                } else {
                    throw new ClassCastException();
                }
            } else {
                throw new NullPointerException();
            }
        }
    }
}
