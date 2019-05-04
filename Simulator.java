// Simulator.java

import java.util.PriorityQueue;

/** Framework for discrete event simulation.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 */
public class Simulator {

    /** Class Event used for simulation.
     */
    public static abstract class Event {
        /** Time of the event.
         */
        protected final float time;

        // Event constructor
        Event( float t ) {
            time = t;
        }

        // Each subclass must define how to trigger it
        abstract void trigger();
    }

    // Queue with all events
    private static PriorityQueue <Event> eventSet
            = new PriorityQueue <> (
                    (Event e1, Event e2) -> Float.compare( e1.time, e2.time )
    );

    /** Schedule one new event.
     *  @param e the event to schedule
     */
    public static void schedule( Event e ) {
        eventSet.add( e );
    }

    /** Main loop that runs the simulation.
     *  This must be called after all initial events are scheduled.
     */
    public static void run() {
        while (!eventSet.isEmpty()) {
            Event e = eventSet.remove();
            e.trigger();
        }
    }
}
