// Errors.java

/** Errors.java is an error reporting package.
 *  <p>
 *  This provides a standard prefix and behavior for warnings and fatal
 *  error messages.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 */
public class Errors {
    // Error messages are counted.
    private static int errorCount = 0;

    /** Allow public read-only access to the count of error messages
     *  @return the count
     */
    public static int count() {
        return errorCount;
    }

    /** Report nonfatal errors, output a message and return
     *  @param message the message to output
     */
    public static void warn( String message ) {
        System.err.println( "RoadNetwork: " + message );
        errorCount = errorCount + 1;
    }

    /** Report fatal errors, output a message and exit, never to return
     *  @param message the message to output
     */
    public static void fatal( String message ) {
        warn( message );
        System.exit( 1 );
    }
}
