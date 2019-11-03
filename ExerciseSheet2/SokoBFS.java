import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
            Collections.sort(startboxpos);
            State startstate = new State(startpos, startboxpos);
            ArrayList<State> visited = new ArrayList<State>();
            visited.add(startstate);
            int size;
            int steplength = -1;

            while(true) {
                steplength++;
                size = visited.size();
                for (int i=0; i<size; i++) {
                    State s = visited.get(i);
                    if(s.steps.length() == steplength) {
                        checkState(goNorth(s, labyrinth), visited, labyrinth);
                        checkState(goSouth(s, labyrinth), visited, labyrinth);
                        checkState(goWest(s, labyrinth), visited, labyrinth);
                        checkState(goEast(s, labyrinth), visited, labyrinth);
                    }
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
        State ret;
        ComparablePoint newpos = new ComparablePoint(curstate.pos.x+dx, curstate.pos.y+dy);

        if(!isPassable(newpos, lab)){
            return null;
        } else {
            ComparablePoint box;
            int comppos;
            int compbox;
            for(int i=0; i<curstate.boxpos.size(); i++) {
                box = curstate.boxpos.get(i);
                comppos = newpos.compareTo(box);
                if(comppos > 0) {
                    break;
                }
                if(comppos == 0) {
                    ComparablePoint cp = new ComparablePoint(box.x+dx, box.y+dy);
                    if (!isPassable(cp, lab)) {
                        return null;
                    } else {
                        for(ComparablePoint boxpos: curstate.boxpos) {
                            compbox = boxpos.compareTo(cp);
                            if(compbox > 0) {
                                break;
                            }
                            if(compbox == 0) {
                                return null;
                            }
                        }
                        ret = curstate.copy();
                        ret.steps += move;
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
        boolean bufret = true;
        for(State s: stateList) {
            if(state.pos.compareTo(s.pos) == 0) {
                for(int i=0; i<state.boxpos.size(); i++) {
                    if(state.boxpos.get(i).compareTo(s.boxpos.get(i)) != 0) {
                        bufret = false;
                        break;
                    }
                }
                if(bufret) {
                    return true;
                }
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
