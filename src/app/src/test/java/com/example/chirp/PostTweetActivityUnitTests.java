package com.example.chirp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PostTweetActivityUnitTests {

    PostTweetActivity myObj = new PostTweetActivity();

    @Test
    public void chirpHashtagIsCorrect() {
        assertEquals("\n#chirpaudioupdates", myObj.chirpHashtag);
    }

    @Test
    public void driveSharingLinkIsNotNull() {
        assertNotNull(myObj.driveSharingLink);
    }


}