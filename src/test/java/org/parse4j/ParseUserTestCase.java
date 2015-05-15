package org.parse4j;

import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class ParseUserTestCase extends Parse4JTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void signupNoUsername() {
        System.out.println("signupNoUsername(): initializing...");

        ParseUser parseUser = new ParseUser();
        try {
            parseUser.signUp();
        } catch (ParseException e) {
            assertNull("ParseException should be null", e);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void signupNoPassword() {
        System.out.println("signupNoPassword(): initializing...");

        ParseUser parseUser = new ParseUser();
        try {
            parseUser.setUsername("parse4j-user");
            parseUser.signUp();
        } catch (ParseException e) {
            assertNull("ParseException should be null", e);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void signupWithObjectId() {
        System.out.println("signupWithObjectId(): initializing...");

        ParseUser parseUser = new ParseUser();
        try {
            parseUser.setUsername("parse4j-user");
            parseUser.setPassword("parse4j-password");
            parseUser.setObjectId("tempObjectId");
            parseUser.signUp();
        } catch (ParseException e) {
            assertNull("ParseException should be null", e);
        }

    }

    @Test public void
    sign_up_and_delete_an_user() throws ParseException {
        final String number = UUID.randomUUID().toString();
        ParseUser parseUser = getParseUser(number);
        parseUser.signUp();
        assertThat(parseUser.getObjectId(), is(notNullValue()));
        assertThat(parseUser.getSessionToken(), is(notNullValue()));
        assertThat(parseUser.getSessionToken(), is(notNullValue()));

        parseUser.delete();
    }

    @Test(expected = ParseException.class)
    public void signupExistingUsername() throws ParseException {
        System.out.println("signupExistingUsername(): initializing...");

        ParseUser parseUser = getParseUser("2");
        parseUser.signUp();

        parseUser = getParseUser("2");
        parseUser.signUp();

        assertNotNull("objectId should not be null", parseUser.getObjectId());
        assertNotNull("createdAt should not be null", parseUser.getCreatedAt());
        assertNotNull("sessionToken should not be null", parseUser.getSessionToken());
    }

    @Test public void
    login() throws ParseException {
        final String number = UUID.randomUUID().toString();
        ParseUser pu = getParseUser(number);
        pu.signUp();

        ParseUser parseUser = ParseUser.login(pu.getUsername(), "parse4j-password");

        assertThat(parseUser.getString("city"), is("westbury"));
        assertThat(parseUser.getString("state"), is("ny"));

        assertThat(parseUser.getObjectId(), is(notNullValue()));
        assertThat(parseUser.getSessionToken(), is(notNullValue()));
        assertThat(parseUser.getSessionToken(), is(notNullValue()));

        pu.delete();
    }

    @Test public void
    verify_email() throws ParseException {
        final String number = UUID.randomUUID().toString();
        ParseUser parseUser = getParseUser(number);
        parseUser.signUp();

        ParseUser.requestPasswordReset(parseUser.getEmail());

        parseUser.delete();
    }

    @Test(expected = ParseException.class)
    public void verifyInvalidEmail() throws ParseException {
        System.out.println("verifyEmail(): initializing...");

        try {
            ParseUser.requestPasswordReset("invalid@gamil.com");
        } catch (ParseException e) {
            throw e;
        }

    }


}
