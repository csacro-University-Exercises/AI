import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

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
        int aimcount = 0;
        try {
            while (br.ready()) {
                br_buf = br.readLine();
                //get agent position
                if((pos_buf = br_buf.indexOf('@')) >= 0) {
                    startpos = new ComparablePoint(linecount , pos_buf);
                }
                //get position of boxes
                pos_buf = 0;
                while((pos_buf = br_buf.indexOf('$', pos_buf+1)) >= 0) {
                    startboxpos.add(new ComparablePoint(linecount, pos_buf));
                }
                //get number of aims
                pos_buf = 0;
                while((pos_buf = br_buf.indexOf('.', pos_buf+1)) >= 0) {
                    aimcount++;
                }
                labyrinth.add(br_buf);
                linecount++;
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //Search
        if(startpos != null && !startboxpos.isEmpty()) {
            Collections.sort(startboxpos);
            State startstate = new State(startpos, startboxpos);
            ArrayList<Integer> visited = new ArrayList<Integer>();
            ArrayList<State> curstates = new ArrayList<State>();
            curstates.add(startstate);
            visited.add(Objects.hashCode(startstate));
            int size;
            int steplength = -1;

            while(true) {
                steplength++;
                size = curstates.size();
                for (int i=size-1; i>=0; i--) {
                    State s = curstates.get(i);
                    if(s.steps.length() < steplength) {
                        break;
                    } else {
                        checkState(goNorth(s, labyrinth), curstates, labyrinth, aimcount, visited);
                        checkState(goSouth(s, labyrinth), curstates, labyrinth, aimcount, visited);
                        checkState(goWest(s, labyrinth), curstates, labyrinth, aimcount, visited);
                        checkState(goEast(s, labyrinth), curstates, labyrinth, aimcount, visited);
                    }
                }
            }
        }
        System.exit(1);
    }

    private static void checkState(State state, ArrayList<State> curstates, ArrayList<String> lab, int aims, ArrayList<Integer> visited) {
        if(state != null) {
            if (isGoal(state, lab, aims)) {
                System.out.println(state.steps.toUpperCase());
                System.exit(0);
            }
            if(!isInList(state, visited)) {
                curstates.add(state);
                visited.add(Objects.hashCode(state));
            }
        }
    }

    private static State move (State curstate, int dx, int dy, ArrayList<String> lab, String move) {
        State ret;
        ComparablePoint newpos = new ComparablePoint(curstate.pos.x+dx, curstate.pos.y+dy);

        if(!isPassable(newpos, lab) || isMoveBack(move, curstate.steps)){
            return null;
        } else {
            ComparablePoint box;
            for(int i=0; i<curstate.boxpos.size(); i++) {
                box = curstate.boxpos.get(i);
                if(newpos.compareTo(box) == 0) {
                    ComparablePoint cp = new ComparablePoint(box.x+dx, box.y+dy);
                    if (!isPassable(cp, lab)) {
                        return null;
                    } else {
                        for(ComparablePoint boxpos: curstate.boxpos) {
                            if(boxpos.compareTo(cp) == 0) {
                                return null;
                            }
                        }
                        ret = curstate.copy();
                        ret.steps += move.toUpperCase();
                        ret.pos = newpos;
                        ret.boxpos.set(i, cp);
                        Collections.sort(ret.boxpos);
                        return ret;
                    }
                }
            }
            ret = curstate.copy();
            ret.steps += move;
            ret.pos = newpos;
        }
        return ret;
    }

    private static boolean isPassable(ComparablePoint point, ArrayList<String> lab) {
        try {
            if (lab.get(point.x).charAt(point.y) != '#') {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }
    private static boolean isMoveBack(String move, String laststeps) {
        try {
            String lastmove = laststeps.substring(laststeps.length() - 1);
            switch (move) {
                case "u":
                    if (lastmove.equals("d")) {
                        return true;
                    }
                    break;
                case "d":
                    if (lastmove.equals("u")) {
                        return true;
                    }
                    break;
                case "l":
                    if (lastmove.equals("r")) {
                        return true;
                    }
                    break;
                case "r":
                    if (lastmove.equals("l")) {
                        return true;
                    }
                    break;
            }
            return false;
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    private static State goNorth(State curstate, ArrayList<String> lab) {
        return move(curstate, -1, 0, lab, "u");
    }
    private static State goSouth(State curstate, ArrayList<String> lab) {
        return move(curstate, 1, 0, lab, "d");
    }
    private static State goEast(State curstate, ArrayList<String> lab) {
        return move(curstate, 0, 1, lab, "r");
    }
    private static State goWest(State curstate, ArrayList<String> lab) {
        return move(curstate, 0, -1, lab, "l");
    }

    private static boolean isGoal(State state, ArrayList<String> lab, int aims) {
        boolean boxinaim = true;
        int boxinaimcount = 0;
        boolean aimwithbox = false;

        for(ComparablePoint box: state.boxpos) {
            if(lab.get(box.x).charAt(box.y) != '.') {
                boxinaim = false;
            } else {
                boxinaimcount++;
            }
        }
        if(boxinaimcount == aims) {
            aimwithbox = true;
        }
        return (boxinaim || aimwithbox);
    }

    private static boolean isInList(State state, ArrayList<Integer> stateList) {
        int statehash = Objects.hashCode(state);
        for(int hash: stateList) {
            if (statehash == hash) {
                return true;
            }
        }
        return false;
    }

    static class State {
        ComparablePoint pos;
        ArrayList<ComparablePoint> boxpos;
        String steps;

        public State(ComparablePoint pos, ArrayList<ComparablePoint> boxpos) {
            this.pos = pos;
            this.boxpos = boxpos;
            steps = "";
        }
        public State(ComparablePoint pos, ArrayList<ComparablePoint> boxpos, String steps) {
            this.pos = pos;
            this.boxpos = boxpos;
            this.steps = steps;
        }

        public State copy() {
            return new State(copyPoint(this.pos), copyPointList(this.boxpos), this.steps);
        }

        private ComparablePoint copyPoint(ComparablePoint cp) {
            return new ComparablePoint(cp.x, cp.y);
        }
        private ArrayList<ComparablePoint> copyPointList(ArrayList<ComparablePoint> cpList) {
            ArrayList<ComparablePoint> ret = new ArrayList<ComparablePoint>();
            for(ComparablePoint cp: cpList) {
                ret.add(copyPoint(cp));
            }
            return ret;
        }
    }

    static class ComparablePoint extends Point implements Comparable<ComparablePoint> {

        public ComparablePoint(int x, int y) {
            super(x,y);
        }

        @Override
        public int compareTo(ComparablePoint o) {
            if(o != null) {
                if(this.x > o.x) {
                    return 1;
                } else if(this.x < o.x) {
                    return -1;
                } else {
                    if(this.y > o.y) {
                        return 1;
                    } else if(this.y < o.y) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else {
                throw new NullPointerException();
            }
        }
    }
}