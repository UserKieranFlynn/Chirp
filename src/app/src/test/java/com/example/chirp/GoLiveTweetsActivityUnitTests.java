package com.example.chirp;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GoLiveTweetsActivityUnitTests {

    GoLiveTweetsActivity myObj = new GoLiveTweetsActivity();

    @Test
    public void startPlayingCreatesPlayerInstance() {
        myObj.startPlaying(0);
        assertNotNull(myObj.player);
    }

    @Test
    public void stopPlayingSetsPlayerToNull() {
        myObj.startPlaying(0);
        myObj.stopPlaying();
        assertNull(myObj.player);
    }

    @Test
    public void formatTimeReturnsNonNullValue() {
        Date date = myObj.formatTime("10:30:00");
        assertNotNull(date);
    }
}