/**
 * 16 juin 2011
 */
package ch.msf.util;

/**
 * @author
 * 
 */
public class CompareBytes {
    public static boolean matchData(byte[] srcData, byte[] dataToFind) {
	int iDataLen = srcData.length;
	int iDataToFindLen = dataToFind.length;
	// boolean bGotData = false;
	int iMatchDataCntr = 0;
	for (int i = 0; i < iDataLen; i++) {
	    if (srcData[i] == dataToFind[iMatchDataCntr]) {
		iMatchDataCntr++;
		// bGotData = true;
	    } else {
		if (srcData[i] == dataToFind[0]) {
		    iMatchDataCntr = 1;
		} else {
		    iMatchDataCntr = 0;
		    // bGotData = false;
		}

	    }

	    if (iMatchDataCntr == iDataToFindLen) {
		return true;
	    }
	}

	return false;
    }

}
