package se.vgregion.web.ftp;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class used to extract information from an URI.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public final class URIUtils {
    private enum UserInfoType {
        USERNAME(0), PASSWORD(1);
        private int value;

        UserInfoType(int value) {
            this.value = value;
        }
    }

    /*
     * Hide constructor since a utility class should not be instantiated.
     */
    private URIUtils() {
    }

    /**
     * Extracts the user name from the URI.
     * 
     * @param uri
     *            the URI to the extract user name from
     * @return the user name of the URI, or <code>null</code> if the user name is undefined
     */
    public static String extractUserName(URI uri) {
        return extractUserInfo(uri.getUserInfo(), UserInfoType.USERNAME);
    }

    /**
     * Extracts the password from the URI.
     * 
     * @param uri
     *            the URI to extract the password from
     * @return the password of the URI, or <code>null</code> if the password is undefined
     */
    public static String extractPassword(URI uri) {
        return extractUserInfo(uri.getUserInfo(), UserInfoType.PASSWORD);
    }

    /**
     * Extracts the port from the URI.
     * 
     * @param uri
     *            the URI to extract the port from
     * @param defaultPort
     *            the port to use if no one is supplied in the URI
     * 
     * @return the port of the URI, <code>defaultPort</code> otherwise
     */
    public static int extractPort(URI uri, int defaultPort) {
        return uri.getPort() < 0 ? defaultPort : uri.getPort();
    }

    /**
     * Extracts the host from the URI.
     * 
     * @param uri
     *            the URI to extract the host from
     * @return the host of the URI, or <code>null</code> if the host is undefined
     */
    public static String extractHost(URI uri) {
        return uri.getHost();
    }

    /**
     * Extracts the path from the URI.
     * 
     * @param uri
     *            the URI to extract the path from
     * @return the path of the URI, or <code>null</code> if the path is undefined
     */
    public static String extractPath(URI uri) {
        return uri.getPath();
    }

    private static String extractUserInfo(String userInfo, UserInfoType userInfoType) {
        if (!StringUtils.isBlank(userInfo)) {
            if (userInfo.contains(":")) {
                userInfo = userInfo.split(":")[userInfoType.value];
            }
        }
        return userInfo;
    }
}
