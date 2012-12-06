package se.vgregion.web.signaturestorage.impl;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AcceptNoneCookieStore implements CookieStore {

        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public List<Cookie> getCookies() {
            return new ArrayList<Cookie>();
        }

        @Override
        public boolean clearExpired(Date date) {
            return false;
        }

        @Override
        public void clear() {

        }
    }