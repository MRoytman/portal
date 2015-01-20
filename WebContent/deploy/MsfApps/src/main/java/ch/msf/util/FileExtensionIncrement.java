/**
 * 25 mai 2011
 */
package ch.msf.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * 
 * @author cmiville
 * 
 */
public class FileExtensionIncrement {

    // public static int _ExtensionCount = -1;
    private static HashMap<String, Integer> _TokenMap = new HashMap<String, Integer>();

    static private final NumberFormat _Formatter = new DecimalFormat("000");

    static private int incrExtensionCount(int count) {
	return (count + 1) % 1000;
    }

    public static String nextExtensionCount(String token) {

	// check if it is a new token
	if (_TokenMap.get(token) == null) {
	    _TokenMap.put(token, -1);
	}
	// incr value first
	int nextValue = incrExtensionCount(_TokenMap.get(token));
	// store value
	_TokenMap.put(token, nextValue);
	// return formated string
	return "." + _Formatter.format(nextValue);
    }

}
