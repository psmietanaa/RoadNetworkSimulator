// ScanSupport.java

import java.util.regex.Pattern;
import java.util.Scanner;

/** Support methods for scanning
 *  Each of these methods scans the input for the desired target
 *  and returns the target converted as necessary.
 *  @author Douglas W. Jones
 *  @author Piotr Smietana
 *  @version 2019-03-31
 *  @see Errors
 */
public class ScanSupport {

    /** Exception thrown to indicate target not found.
     *  <p>
     *  This should only be thrown by code in this class.
     *  Users of scan-support methods must catch it.
     */
    public static class NotFound extends Exception {}

    // Patterns needed for scanning
    private static final Pattern name
    = Pattern.compile( "[a-zA-Z0-9_]*" );
    private static final Pattern intPattern
    = Pattern.compile( "-?[0-9][0-9]*|");
    private static final Pattern floatPattern
    = Pattern.compile( "-?[0-9][0-9]*\\.?[0-9]*|\\.[0-9][0-9]*|");
    private static final Pattern whitespace
    = Pattern.compile( "[ \t]*" );

    /** Interface for passing error messages.
     *  <p>
     *  Scan-support error messages are only needed if the target
     *  is not found.  This is not the usual case, so computation
     *  such as string concatenation needed to form the messages
     *  should not be done unless the message is to be printed.
     *  Usually, the actual parameter for an error message
     *  will be a lambda expression.
     */
    public interface Message {
        String myString();
    }

    /** Get next name without skipping to next line (unlike sc.Next()).
     *  @param sc the scanner from which end of line is scanned
     *  @param m gives the context part of the missing name error message
     *  @return the name if there was one.
     *  @throws NotFound if there wasn't one
     */
    public static String nextName( Scanner sc, Message m ) throws NotFound {
        sc.skip( whitespace );
        sc.skip( name );
        String s = sc.match().group();
        if ("".equals( s )) {
            Errors.warn( "Name expected: " + m.myString() );
            sc.nextLine();
            throw new NotFound();
        }
        return s;
    }

    /** Get next int without skipping to next line (unlike sc.nextInt()).
     *  @param sc the scanner from which end of line is scanned
     *  @param m gives the message to output if there was no int
     *  @return the value if there was one
     *  @throws NotFound if there wasn't one
     */
    public static int nextInt( Scanner sc, Message m ) throws NotFound {
        sc.skip( whitespace );
        sc.skip( intPattern );
        String s = sc.match().group();
        if ("".equals( s )) {
            Errors.warn( "Float expected: " + m.myString() );
            sc.nextLine();
            throw new NotFound();
        }
        return Integer.parseInt( s );
    }

    /** Get next float without skipping to next line (unlike sc.nextFloat()).
     *  @param sc the scanner from which end of line is scanned
     *  @param m gives the message to output if there was no float
     *  @return the value if there was one
     *  @throws NotFound if there wasn't one
     */
    public static float nextFloat( Scanner sc, Message m ) throws NotFound {
        sc.skip( whitespace );
        sc.skip( floatPattern );
        String s = sc.match().group();
        if ("".equals( s )) {
            Errors.warn( "Float expected: " + m.myString() );
            sc.nextLine();
            throw new NotFound();
        }
        return Float.parseFloat( s );
    }

    /** Advance to next line and complain if is junk at the line end.
     *  @see Errors
     *  @param sc the scanner from which end of line is scanned
     *  @param message gives a prefix to give context to error messages
     */
    public static void lineEnd( Scanner sc, Message message ) {
        sc.skip( whitespace );
        String lineEnd = "";
        if (sc.hasNextLine()) {
            lineEnd = sc.nextLine();
        }
        if ( (!lineEnd.equals( "" ))
        &&   (!lineEnd.startsWith( "--" )) ) {
            Errors.warn(
                message.myString() +
                " followed unexpected by '" + lineEnd + "'"
            );
        }
    }
}
