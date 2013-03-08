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

function pollForCompletion(orderRef, data, counter) {

    if (counter < 30) {
        $.ajax('awaitResponse', {
            data: {'orderRef': orderRef, 'data': data},
            dataType: 'json',
            type: 'POST'
        }).done(function (response) {
                var status = response['status'];
                if (status === 'COMPLETE') {
                    if (response['redirect'] != null) {
                        document.location.href = response['redirect'];
                    } else {
                        $('#responseText').html(response['message']);
                    }
                } else if (status === 'FAILURE') {
                    $('#responseText').html(response['message']);
                } else {
                    setTimeout(function () {pollForCompletion(orderRef, data, counter + 1)}, 3000);
                }
            }).error(function (response) {
                setTimeout(function () {pollForCompletion(orderRef, data, counter + 1)}, 3000);
            });
    } else {
        $('#responseText').html('Tiden för signering har gått ut.');
        $('#spinner').css('visibility', 'hidden');
    }
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