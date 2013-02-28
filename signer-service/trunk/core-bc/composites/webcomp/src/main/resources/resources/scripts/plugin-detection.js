function isOkBrowserAndOsForTest() {
    if (navigator.userAgent.indexOf("NT 6.2") != -1) {
        if (navigator.userAgent.indexOf("Chrome") != -1 || navigator.userAgent.indexOf("MSIE") != -1) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}
function allowsPlugins() {
    if (window.ActiveXObject) {
        try {
            return !!new ActiveXObject("htmlfile");
        } catch (e) {
        }
    } else {
        try {
            return !!navigator.plugins["Google Update"];
        } catch (e) {
        }
    }
    return false;
}

function isBrowserMetroMode() {
    if (isOkBrowserAndOsForTest() && !allowsPlugins()) {
        return true;
    } else {
        //alert(navigator.userAgent + " Desktop");
        return false;
    }
}