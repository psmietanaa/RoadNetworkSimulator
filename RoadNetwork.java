// RoadNetwork.java

import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/** RoadNetwork is the main class.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Road
 *  @see Intersection
 *  @see ScanSupport
 *  @see Errors
 *  @see Simulator
 */
public class RoadNetwork {

    // The sets of all roads and all intersections
    private static LinkedList <Road> roads
    = new LinkedList <> ();
    private static LinkedList <Intersection> inters
    = new LinkedList <> ();

    /** Find an intersection by textual name in the set of all intersections.
     *  @param s name of an intersection
     *  @return the intersection named s or null if there are none
     */
    public static Intersection findIntersection( String s ) {
        for (Intersection i: inters) {
            if (i.name.equals( s )) {
                return i;
            }
        }
        return null;
    }

    // Initialize this road network by scanning its description
    private static void readNetwork( Scanner sc ) {
        while (sc.hasNext()) {
            String command = sc.next();
            if ("intersection".equals( command )) {
                try {
                    inters.add( Intersection.newIntersection( sc ) );
                } catch (Intersection.ConstructorFailure e) {
                    // Do nothing, the constructor already reported the error
                }
            } else if ("road".equals( command )) {
                try {
                    roads.add( new Road( sc ) );
                } catch (Road.ConstructorFailure e) {
                    // Do nothing, the constructor already reported the error
                }
            } else if ("--".equals( command )) {
                sc.nextLine();
            } else {
                Errors.warn( "Unknown command: " + command );
                sc.nextLine();
            }
        }
    }

    // Print out the road network
    private static void printNetwork() {
        for (Intersection i: inters) {
            System.out.println( i.toString() );
        }
        for (Road r: roads) {
            System.out.println( r.toString() );
        }
    }

    /** Main program.
     *  The only command line argument expected is the file name of a
     *  file that holds the description of a road network.
     *  @param args holds the command line arguments
     */
    public static void main( String[] args ) {
        if (args.length < 1) {
            Errors.fatal( "Missing file name argument" );
        } else if (args.length > 1) {
            Errors.fatal( "Too many arguments" );
        } else try {
            readNetwork( new Scanner( new File( args[0] ) ) );
            if (Errors.count() == 0) {
                Simulator.run();
            } else {
                printNetwork();
            }
        } catch (FileNotFoundException e) {
            Errors.fatal( "Can't open the file" );
        }
    }
}
