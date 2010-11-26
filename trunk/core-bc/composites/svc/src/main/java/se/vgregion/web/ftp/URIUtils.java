package se.vgregion.web.ftp;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

public class URIUtils {
    private enum UserInfoType {
        USERNAME(0), PASSWORD(1);
        int value;

        UserInfoType(int value) {
            this.value = value;
        }
    }

    public static String extractUserName(URI uri) {
        return extractUserInfo(uri.getUserInfo(), UserInfoType.USERNAME);
    }

    public static String extractPassword(URI uri) {
        return extractUserInfo(uri.getUserInfo(), UserInfoType.PASSWORD);
    }

    public static int extractPort(URI uri, int defaultPort) {
        return uri.getPort() < 0 ? defaultPort : uri.getPort();
    }

    private static String extractUserInfo(String userInfo, UserInfoType userInfoType) {
        if (!StringUtils.isBlank(userInfo)) {
            if (userInfo.contains(":")) {
                userInfo = userInfo.split(":")[userInfoType.value];
            }
        }
        return userInfo;
    }

    public static String extractHost(URI uri) {
        return uri.getHost();
    }

    public static String extractPath(URI uri) {
        return uri.getPath();
    }
}
