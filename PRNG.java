// PRNG.java

import java.util.Random;

/** PRNG.java
 *  Support class for pseudo-random number generation
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 */
public class PRNG {

    // Warning:  For debugging, use a known seed so errors are reproducible
    private static Random stream = new Random( 5 );

    /** Get a random number 0 to n.
     *  @param bound one greater than the maximum return value
     *  @return n
     */
    public static int fromZeroTo( int bound ) {
	    return stream.nextInt( bound );
    }
}
