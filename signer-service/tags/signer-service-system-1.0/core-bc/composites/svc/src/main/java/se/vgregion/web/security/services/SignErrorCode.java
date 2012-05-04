package se.vgregion.web.security.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to convert a sign error code to a corresponding error message.
 * @author anders
 */
public final class SignErrorCode {
    private SignErrorCode() {
    }

    private static final Map<Integer, String> ERROR_CODE_MAP = new HashMap<Integer, String>();
    private static final String UNKNOWN_ERROR = "Unknown Error";

    static {
        ERROR_CODE_MAP.put(0, "OK");
        ERROR_CODE_MAP.put(1, "User Abort");
        ERROR_CODE_MAP.put(2, "No PKI Client found");
    }

    /**
     * Get the error message from the corresponding error code.
     * 
     * @param errorCode
     *            the error code
     * @return the error message from the corresponding error code
     */
    public static String getErrorMessage(int errorCode) {
        return ERROR_CODE_MAP.containsKey(errorCode) ? ERROR_CODE_MAP.get(errorCode) : UNKNOWN_ERROR;
    }

}
