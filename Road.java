// Road.java

import java.util.Scanner;

/** Roads are joined by Intersections.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 */
public class Road {

    /** Constructors may throw this when an error prevents construction.
     */
    public static class ConstructorFailure extends Exception {}

    // Where this road comes from
    private final Intersection source;
    // Where this road goes, never null
    private final Intersection destination;
    // Travel time measured in seconds, always positive
    private final float travelTime;
    // What direction does this enter dst
    private final int dstDir;

    /** Construct a new road by scanning its description from the source file.
     *  @param sc the scanner from which the input is read
     *  @throws ConstructorFailure when it cannot construct a road
     */
    Road( Scanner sc ) throws ConstructorFailure {
        final String sourceName;
        final String dstName;
        try {
            sourceName = ScanSupport.nextName(
            sc, ()->"Road ???"
            );
            dstName = ScanSupport.nextName(
            sc, ()-> "Road " + sourceName + " ???"
            );
        } catch (ScanSupport.NotFound e) {
            throw new ConstructorFailure();
        }
        source = RoadNetwork.findIntersection( sourceName );
        destination = RoadNetwork.findIntersection( dstName );
        if (source == null) {
            Errors.warn( "No such source intersection: Road "
                + sourceName + " " + dstName
            );
            sc.nextLine();
            throw new ConstructorFailure();
        }
        if (destination == null) {
            Errors.warn( "No such destination intersection: Road "
                + sourceName + " " + dstName
            );
            sc.nextLine();
            throw new ConstructorFailure();
        }
        try {
            travelTime = ScanSupport.nextFloat(
            sc, ()->"Floating point travel time expected: Road "
                + sourceName + " " + dstName
            );
        } catch (ScanSupport.NotFound e) {
            throw new ConstructorFailure();
        }
        if (travelTime < 0.0F) {
            Errors.warn( "Negative travel time:" + this.toString() );
        }
        ScanSupport.lineEnd( sc, ()->this.toString() );
        // Register this road with its source and destination intersections
        source.outgoing.add( this );
        dstDir = destination.incoming.size();
        destination.incoming.add( this );
    }

    /** Give the road in a form like that used for input.
     *  @return the textual road description
     */
    public String toString() {
        return  "Road " + source.name + " "
                + destination.name + " "
                + travelTime;
    }

    // Simulation methods

    /** What happens when a vehicle enters this road.
     *  @param t the time the vehicle enters
     */
    public void entryEvent( float t ) {
        System.out.println(
            "Vehicle entered " + this.toString() + " at " + t
        );
        // After a vehicle enters the road, it exits it travelTime later
        Simulator.schedule(
            new Simulator.Event( t + travelTime ) {
                void trigger() { exitEvent( time ); }
            }
        );
    }

    // What happens when a vehicle exits this road.
    private void exitEvent(float time) {
        destination.arrivalEvent( time, dstDir );
    }
}
