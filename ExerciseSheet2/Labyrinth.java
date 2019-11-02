import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Gruppe P: Carolin, Dominik
 */

public class Labyrinth {

    public static void main(String[] args) {
        //read in labyrinth (upper left corner is (0,0) and lower right corner is (n,m))
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String br_buf;
        int pos_buf;
        int linecount = 0;
        ArrayList<String> labyrinth = new ArrayList<String>();
        Point startpos = null;
        try {
            while (br.ready()) {
                br_buf = br.readLine();
                if((pos_buf = br_buf.indexOf('@')) >= 0) {
                    startpos = new Point(linecount , pos_buf);
                }
                labyrinth.add(br_buf);
                linecount++;
            }
        } catch (IOException e) {
            System.exit(1);
        }

        //Search
        if(startpos != null) {
            int steps = 0;
            int size = 0;
            ArrayList<Point> visited = new ArrayList<Point>();
            visited.add(startpos);

            while(true) {
                steps++;
                size = visited.size();
                for (int i=0; i<size; i++) {
                    Point p = visited.get(i);
                    checkPoint(goNorth(p), visited, labyrinth, steps);
                    checkPoint(goSouth(p), visited, labyrinth, steps);
                    checkPoint(goWest(p), visited, labyrinth, steps);
                    checkPoint(goEast(p), visited, labyrinth, steps);
                }
            }
        }
        System.exit(1);
    }

    private static void checkPoint(Point point, ArrayList<Point> visited, ArrayList<String> lab, int steps) {
        if(!isInList(point, visited)) {
            visited.add(point);
            if (ratePosition(point, lab) == 0) {
                System.out.print(Integer.toString(steps) + "\r\n");
                System.exit(0);
            }
        }
    }
    private static Point goNorth(Point curpos) {
        int newx = curpos.x-1;
        int newy = curpos.y;

        return new Point(newx, newy);
    }
    private static Point goSouth(Point curpos) {
        int newx = curpos.x+1;
        int newy = curpos.y;

        return new Point(newx, newy);
    }
    private static Point goEast(Point curpos) {
        int newx = curpos.x;
        int newy = curpos.y+1;

        return new Point(newx, newy);
    }
    private static Point goWest(Point curpos) {
        int newx = curpos.x;
        int newy = curpos.y-1;

        return new Point(newx, newy);
    }

    private static int ratePosition(Point point, ArrayList<String> lab) {
        int x = point.x;
        int y = point.y;
        char c;
        if(x>=0 && x<lab.size() && y>=0 && y<lab.get(0).length()) {
            c = lab.get(x).charAt(y);
            if(c != '#') {
                if(c == '.') {
                    return 0;
                }
                return 1;
            }
        }
        return -1;
    }

    private static boolean isInList(Point point, ArrayList<Point> pointList) {
        for(Point p: pointList) {
            if(point.x == p.x && point.y == p.y) {
                return true;
            }
        }
        return false;
    }
}
