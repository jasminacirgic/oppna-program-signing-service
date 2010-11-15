package se.vgregion.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * The intent of this class is to detect the browser of a given user agent.
 * 
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * 
 */
public enum BrowserType {
    SAFARI, CHROME, FIREFOX, MSIE, OTHER;

    public static BrowserType getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        BrowserType browser = OTHER;
        if (StringUtils.isBlank(userAgent)) {
            return browser;
        }
        if (isBrowser(userAgent, MSIE)) {
            browser = MSIE;
        } else if (isBrowser(userAgent, FIREFOX)) {
            browser = FIREFOX;
        } else if (isBrowser(userAgent, CHROME)) {
            browser = CHROME;
        } else if (isBrowser(userAgent, SAFARI)) {
            browser = SAFARI;
        }
        return browser;
    }

    private static boolean isBrowser(String userAgent, BrowserType browser) {
        return userAgent.toLowerCase().indexOf(browser.name().toLowerCase()) > -1;
    }
}
