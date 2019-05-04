// Intersection.java

import java.util.LinkedList;
import java.util.Scanner;

/** Intersections pass Vehicles between Roads.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Road
 *  @see StopLight
 *  @see NoStop
 */
public abstract class Intersection {

    /** Constructors may throw this when an error prevents construction.
     */
    public static class ConstructorFailure extends Exception {}

    /** Name of this Intersection.
     */
    final public String name;

    /** Set of all roads out of this Intersection.
     */
    public final LinkedList <Road> outgoing = new LinkedList <> ();

    /** Set of all roads in to this Intersection.
     */
    public final LinkedList <Road> incoming = new LinkedList <> ();

    /** Constructor used by subclasses to initialize final fields.
     *  @param name sets the name field
     */
    protected Intersection( String name ) {
        this.name = name;
    }

    /** Factory method to create subclasses of Intersections.
     *  @param sc the scanner to read intersection description from
     *  @return either a new intersection or null
     *  @throws ConstructorFailure when the intersection cannot be constructed
     */
    public static Intersection newIntersection( Scanner sc )
            throws ConstructorFailure {
        // Get the name
        final String name;
        try {
            name = ScanSupport.nextName( sc, ()-> "Intersection ???" );
        } catch (ScanSupport.NotFound e) {
            throw new ConstructorFailure();
        }
        // Check for duplicate definition
        if (RoadNetwork.findIntersection( name ) != null) {
            Errors.warn( "Intersection redefined: " + name );
            sc.nextLine();
            throw new ConstructorFailure();
        }
        // Find out which subclass of intersection is needed
        final String intersectionType;
        try {
            intersectionType = ScanSupport.nextName(
            sc, ()-> "Intersection " + name + " ???"
            );
        } catch (ScanSupport.NotFound e) {
            throw new ConstructorFailure();
        }
        // Construct the desired class of intersection and return it
        if ("nostop".equals( intersectionType )) {
            return new NoStop( sc, name );
        } else if ("stoplight".equals( intersectionType )) {
            return new StopLight( sc, name );
        } else if ("source".equals( intersectionType )) {
            return new Source( sc, name );
        } else if ("sink".equals( intersectionType )) {
            return new Sink( sc, name );
        } else {
            Errors.warn( "Intersection " + name + " " + intersectionType
                 + ": unknown type: " );
            sc.nextLine();
            throw new ConstructorFailure();
        }
    }

    /** Get the intersection description in a form like that used for input.
     *  @return the textual description
     */
    public String toString() {
        return  "Intersection " + name;
    }

    // Simulation methods

    /** Pick an outgoing road from this intersection.
     *  @return the road it picks
     */
    protected Road pickRoad() {
        // Pick a road at random
        int roadNumber = PRNG.fromZeroTo( outgoing.size() );
        return outgoing.get( roadNumber );
    }

    /** Simulate a vehicle arriving at this intersection.
     *  @param time a vehicle arrives
     *  @param dir the direction from which a vehicle arrives
     */
    public abstract void arrivalEvent( float time, int dir );

    /** Simulate a vehicle departs from this intersection.
     *  @param time a vehicle departs
     */
    public abstract void departureEvent( float time );
}

/** Intersection with no control, neither stopsign nor stoplight.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Intersection
 */
class NoStop extends Intersection {

    // Time it takes to traverse the intersection
    private final float delay;
    // Count of vehicles in the intersection
    private int occupants = 0;

    /** NoStop intersection constructor.
     *  @param sc scanner from which the description is taken
     *  @param name of the intersection the caller already scanned
     *  @throws Intersection.ConstructorFailure if the description is faulty
     */
    NoStop( Scanner sc, String name ) throws Intersection.ConstructorFailure {
        super( name );
        try {
            delay = ScanSupport.nextFloat(
                sc, ()-> "Floating point delay expected: Intersection "
                + name + " nostop"
            );
        } catch (ScanSupport.NotFound e) {
            throw new ConstructorFailure();
        }
        ScanSupport.lineEnd( sc, ()->this.toString() );
    }

    /** Get the intersection description in a form like that used for input.
     *  @return the textual description
     */
    public String toString() {
        return  super.toString() + " nostop " + delay;
    }

    // Simulation methods

    /** What happens when a vehicle arrives at this NoStop intersection.
     *  @param t the time the vehicle arrives
     *  @param dir the direction it arrives from
     */
    public void arrivalEvent( float t, int dir ) {
        System.out.println(
            "Vehicle arrived at " + this.toString() + " at " + t
        );
        // If intersection is clear, vehicle continues
        if (occupants == 0) {
            Simulator.schedule(
                new Simulator.Event( t + delay ) {
                    void trigger() { departureEvent( time ); }
                }
            );
        }
        occupants = occupants + 1;
    }

    /** What happens when a vehicle departs from this NoStop intersection.
     *  @param t the time the vehicle departs
     */
    public void departureEvent( float t ) {
        occupants = occupants - 1;
        // Send the vehicle onward
        this.pickRoad().entryEvent( t );
        // if others are queued up, let one of them continue
        if (occupants > 0) {
            Simulator.schedule(
                new Simulator.Event( t + delay ) {
                    void trigger() { departureEvent( time ); }
                }
            );
        }
    }
}

/** Intersection with a stoplight.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Intersection
 */
class StopLight extends Intersection {

    // Direction light is green
    private int lightDir = 0;
    // Time it stays green
    private final float lightInterval;
    // Where vehicles wait (just counts of vehicles)
    private int[] queues;
    // Time it takes to traverse the intersection
    private final float delay;
    // Count of vehicles in the intersection
    private int occupants = 0;

    /** StopLight intersection constructor.
     *  @param sc scanner from which the description is taken
     *  @param name of the intersection the caller already scanned
     *  @throws Intersection.ConstructorFailure if the description is faulty
     */
    StopLight( Scanner sc, String name )throws Intersection.ConstructorFailure {
        super( name );
        try {
            delay = ScanSupport.nextFloat(
                sc, ()->"Floating point delay expected: Intersection "
                + name + " stoplight"
            );
            lightInterval = ScanSupport.nextFloat(
                sc, ()->"Floating point light interval expected: Intersection "
                + name + " stoplight " + delay
            );
        } catch (ScanSupport.NotFound e) {
            throw new ConstructorFailure();
        }
        ScanSupport.lineEnd( sc, ()->this.toString() );
        // Start the light change event process
        Simulator.schedule(
            new Simulator.Event( 0 ) {
                void trigger() { lightChangeEvent( time ); }
            }
        );
    }

    /** Get the intersection description in a form like that used for input.
     *  @return the textual description
     */
    public String toString() {
        return  super.toString() + " stoplight " + delay + " " + lightInterval;
    }

    // Simulation methods

    /** What happens when the StopLight changes.
     *  @param t the time the light changes
     */
    private void lightChangeEvent( float t ) {
        if (queues == null) queues = new int[incoming.size()];
        // Change the light direction
        lightDir = lightDir + 1;
        if (lightDir >= incoming.size()) lightDir = 0;
        // Release the first waiting cars if the intersection is clear
        if ((queues[lightDir] > 0) && (occupants == 0)) {
            queues[lightDir] = queues[lightDir] - 1;
            Simulator.schedule(
                new Simulator.Event( t + delay ) {
                    void trigger() { departureEvent( time ); }
                }
            );
            occupants = occupants + 1;
        }
        // Advance the light change process
        Simulator.schedule(
            new Simulator.Event( t + lightInterval ) {
                void trigger() { lightChangeEvent( time ); }
            }
        );
    }

    /** What happens when a vehicle arrives at this StopLight intersection.
     *  @param t the time the vehicle arrives
     *  @param dir the direction it arrives from
     */
    public void arrivalEvent( float t, int dir ) {
        System.out.println(
            "Vehicle arrived at " + this.toString() + " at " + t
        );
        // Green and unoccupied
        if ((dir == lightDir) && (occupants == 0)) {
            // Car goes straight through green light
            Simulator.schedule(
                new Simulator.Event( t + delay ) {
                    void trigger() { departureEvent( time ); }
                }
            );
            occupants = occupants + 1;
        } else {
            // Light is red
            // Queue up another car
            queues[dir] = queues[dir] + 1;
        }
    }

    /** What happens when a vehicle departs from this StopLight intersection.
     *  @param t the time the vehicle departs
     */
    public void departureEvent( float t ) {
        // Move the departing vehicle onward
        Road r = this.pickRoad();
        r.entryEvent( t );
        occupants = occupants - 1;
        // If there are more vehicles, schedule the next departure
        if (queues[lightDir] > 0) {
            queues[lightDir] = queues[lightDir] - 1;
            Simulator.schedule(
                new Simulator.Event( t + delay ) {
                    void trigger() { departureEvent( time ); }
                }
            );
            occupants = occupants + 1;
        }
    }
}

/** Source Intersection
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Intersection
 */
class Source extends Intersection {

    // When source starts producing
    private final float startTime;
    // How many vehicles it produces
    private int numCars;
    // Period between vehicles
    private final float departureInterval;

    /** Source Intersection constructor.
     *  @param sc the scanner from which the description is read
     *  @param name of this intersection
     *  @throws Intersection.ConstructorFailure if description is bad
     */
    Source( Scanner sc, String name ) throws Intersection.ConstructorFailure {
        super( name );
        // Parse and initialize the source description
        try {
            startTime = ScanSupport.nextFloat(
            sc, ()-> Source.this.toString()
            // Bug: poorly constructed error message
            );
            numCars = ScanSupport.nextInt(
            sc, ()-> Source.this.toString()
            );
            departureInterval = ScanSupport.nextFloat(
            sc, ()-> Source.this.toString()
            );
        } catch (ScanSupport.NotFound e) {
            throw new Intersection.ConstructorFailure();
        }
        // Check sanity of the fields
        if (startTime < 0.0f) Errors.warn(
            "Negative start time: " + this.toString()
        );
        if (numCars <= 0) Errors.warn(
            "Never produces: " + this.toString()
        );
        if (departureInterval < 0.0f) Errors.warn(
            "Negative departure interval: " + this.toString()
        );
        ScanSupport.lineEnd( sc, ()->Source.this.toString() );
        // Start the simulation of this source
        Simulator.schedule(
            new Simulator.Event( startTime ) {
                void trigger() { departureEvent( time ); }
            }
        );
    }

    /** Get the intersection description in a form like that used for input.
     *  @return the textual description
     */
    public String toString() {
        return  super.toString() + " source " + startTime + " "
                + numCars + " " + departureInterval;
    }

    // Simulation methods

    /** Simulate arrival of one vehicle at this source intersection.
     *  @param time When the vehicle arrives
     */
    public void arrivalEvent( float time, int dir ) {
        Errors.fatal( "Vehicle arrived at: " + this.toString() );
    }

    /** Simulate departure of one vehicle from this source intersection.
     *  @param t the time when the vehicle departs
     */
    public void departureEvent( float t ) {
        // Simulate the departure
        this.pickRoad().entryEvent( t );
        // Schedule the departure of the next car, if there is one
        numCars = numCars - 1;
        if (numCars > 0) Simulator.schedule(
            new Simulator.Event( t + departureInterval ) {
                void trigger() { departureEvent( time ); }
            }
        );
    }

}
/** Sink Intersection
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Intersection
 */
class Sink extends Intersection {

    /** Sink intersection constructor.
     *  @param sc scanner from which the description is taken
     *  @param name of the intersection the caller already scanned
     */
    Sink( Scanner sc, String name ) {
        super( name );
        ScanSupport.lineEnd( sc, ()->this.toString() );
    }

    /** Get the intersection description in a form like that used for input.
     *  @return the textual description
     */
    public String toString() {
        return  super.toString() + " sink";
    }

    /** Simulate arrival of one vehicle at this sink intersection.
     *  @param time When the vehicle arrives
     */
    public void arrivalEvent( float time, int dir ) {
        System.out.println(
            "Vehicle arrived at " + this.toString() + " at " + time
        );
    }

    /** Simulate departure of one vehicle from this sink intersection.
     *  @param time When the vehicle departs
     */
    public void departureEvent( float time ) {
        Errors.fatal( "Vehicle departed from: " + this.toString() );
    }

}
