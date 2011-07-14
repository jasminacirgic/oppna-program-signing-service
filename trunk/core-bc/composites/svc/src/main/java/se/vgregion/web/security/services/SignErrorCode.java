/**
 * 
 */
package se.vgregion.web.security.services;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anders
 *
 */
public class SignErrorCode {

    private static final Map<Integer, String> errorCodeMap = new HashMap<Integer, String>();
    private static final String UNKNOWN_ERROR = "Unknown Error";

    static {
        errorCodeMap.put(0, "OK");
        errorCodeMap.put(1, "User Abort");
        errorCodeMap.put(2, "No PKI Client found");
    }

    public static String getErrorMessage(int errorCode) {
        return errorCodeMap.containsKey(errorCode) ? errorCodeMap.get(errorCode) : UNKNOWN_ERROR;
    }

}
