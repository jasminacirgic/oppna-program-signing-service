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

function longPollForCompletion(orderRef, data) {
    $(document).ready(function () {
        $.ajax('awaitResponse', {
            data: {'orderRef': orderRef, 'data': data},
            type: 'POST'
        }).done(function (msg) {
                $('#responseText').html('done');
                var whetherRedirect = msg.substring(0, 'redirect'.length) === 'redirect';
                if (whetherRedirect) {
                    document.location.href = msg.split('redirect:')[1];
                } else {
                    $('#responseText').html(msg);
                    $('#spinner').css('visibility', 'hidden');
                }
            }).error(function (msg) {
                $('#responseText').html('error ' + msg + " " + msg[0] + " " + msg[1]);
                longPollForCompletion(orderRef, data);
            }).always(function (msg) {
                $('#responseText').html('always ' + msg + " " + msg[0] + " " + msg[1]);
                longPollForCompletion(orderRef, data);
            });
    });
}

function validatePersonalNumber(form) {
    var inputField = $(form).find('#personalNumber');
    var value = inputField.val();

    if (value.length != 12) {
        alert('Personnumret ska bestå av 12 st siffror.')
        inputField.val('');
        return false;
    }

    return true;
}