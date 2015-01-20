
package ch.msf.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;

import ch.msf.error.ConfigException;



public class CharacterUtil implements CharacterUtilI {

    private static String[][] _NonPrintableArray = { { "<NUL>", "00" },
	    { "<SOH>", "01" }, { "<STX>", "02" }, { "<ETX>", "03" },
	    { "<EOT>", "04" }, { "<ENQ>", "05" }, { "<ACK>", "06" },
	    { "<BEL>", "07" }, { "<BS>", "08" }, { "<HT>", "09" },
	    { "<LF>", "0a" }, { "<VT>", "0b" }, { "<FF>", "0c" },
	    { "<CR>", "0d" }, { "<SO>", "0e" }, { "<SI>", "0f" },
	    { "<DLE>", "10" }, { "<DC1>", "11" }, { "<DC2>", "12" },
	    { "<DC3>", "13" }, { "<DC4>", "14" }, { "<NAK>", "15" },
	    { "<SYN>", "16" }, { "<ETB>", "17" }, { "<CAN>", "18" },
	    { "<EM>", "19" }, { "<SUB>", "1a" }, { "<ESC>", "1b" },
	    { "<FS>", "1c" }, { "<GS>", "1d" }, { "<RS>", "1e" },
	    { "<US>", "1f" } };

    // a linkedhashmap will keep the order of inserted keys
    private static LinkedHashMap<String, Byte> _TokenToByte = new LinkedHashMap<String, Byte>();

    private static boolean _InitNotDone = true;

    // Map<Byte, String> _ByteToToken = new HashMap<Byte, String>();

    public CharacterUtil() {
	initTables();
    }

    private static void initTables() {
	if (_InitNotDone) {
	    for (String[] tokenByteStr : _NonPrintableArray) {
		_TokenToByte.put(tokenByteStr[0],
			Byte.parseByte(tokenByteStr[1], 16));
	    }
	    // for (String[] tokenByteStr : _NonPrintableArray) {
	    // _ByteToToken.put(Byte.parseByte(tokenByteStr[1], 16),
	    // tokenByteStr[0]);
	    // }
	    _InitNotDone = false;
	}

    }

    public void printTables() {
	System.out.println("Printing tables...");
	System.out.println("=============================");

	// a linkedhashmap will keep the order of inserted keys
	Iterator<String> it = _TokenToByte.keySet().iterator();
	while (it.hasNext()) {
	    String token = it.next();
	    System.out.println(token + " - " + _TokenToByte.get(token));
	}
	System.out.println("=============================");
	// Iterator<Byte> it2 = _ByteToToken.keySet().iterator();
	// while (it2.hasNext()) {
	// Byte b = it2.next();
	// System.out.println(b + " - " + _ByteToToken.get(b));
	// }
	int index = 0;
	for (String[] tokenByteStr : _NonPrintableArray) {
	    System.out.println((index++) + " - " + tokenByteStr[0]);
	}

    }

    /**
     * 
     * @param properties
     * @return true if the message should be cleared with bit 7 in every byte
     *         received
     * @throws ConfigException
     */
    public static boolean readClearBit7Config(Properties properties)
	    throws ConfigException {
	String nOccurencesStr = properties
		.getProperty("protocol.config.message.clearBit7");
	try {
	    if (nOccurencesStr != null && nOccurencesStr.length() > 0)
		return Boolean.parseBoolean(nOccurencesStr);
	} catch (NumberFormatException e) {
	    String errMess = "::readClearBit7Config: format problem, :"
		    + nOccurencesStr;
	    throw new ConfigException(errMess, e);
	}
	return false;
    }

    /**
     * @param bytes
     */
    public static void clearBit7(byte[] bytes) {

	for (int i = 0; i < bytes.length; i++) {

	    bytes[i] &= 0x7F;
	}
    }

    public String getToken(int index) {
	if (index < 0x20) {
	    return _NonPrintableArray[index][0];
	}
	return null;
    }

    public static byte getByte(String token) {
	initTables();
	return _TokenToByte.get(token);
    }

    public static void main(String[] args) {
	CharacterUtil prog = new CharacterUtil();
	prog.printTables();
    }

}
