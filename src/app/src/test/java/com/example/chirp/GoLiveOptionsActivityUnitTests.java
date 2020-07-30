package com.example.chirp;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Unit Tests for GoLiveOptions
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GoLiveOptionsActivityUnitTests {

    private GoLiveOptionsActivity myObj = new GoLiveOptionsActivity();

    @Test
    public void currentTimeIsCorrect() {
        String currentTimeStringTest = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String myTestObjectString = myObj.displayCurrentTime();

        //Test that the correct CurrentTime String passes
        assertEquals(currentTimeStringTest, myTestObjectString);
    }

    @Test
    public void currentTimeIsNotEqualToEmptyString() {
        String myTestObjectString = myObj.displayCurrentTime();

        //Test that an Empty String is not equal
        assertNotEquals("",myTestObjectString);
    }

    @Test
    public void currentTimeIsNotEqualToNull() {
        String myTestObjectString = myObj.displayCurrentTime();

        //Test that null is not equal
        assertNotEquals(null, myTestObjectString);
    }

    @Test
    public void currentTimeIsNotEqualToInteger() {
        String myTestObjectString = myObj.displayCurrentTime();

        //Test that an Integer is not equal
        assertNotEquals(7, myTestObjectString);
    }

    @Test
    public void currentTimeIsNotEqualToTestString() {
        String myTestObjectString = myObj.displayCurrentTime();

        //Test that an incorrect String is not equal
        assertNotEquals("TestString",myTestObjectString);
    }

    @Test
    public void displayListenUntilTimeIsCorrect() {
        String myTestObjectString = myObj.listenUntilTime("2");

        //Test that a correct addition passes
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,  2);
        String listenUntilTimeStringTest = new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());

        assertEquals(listenUntilTimeStringTest, myTestObjectString);
    }

    @Test
    public void displayListenUntilTimeIsNotEqualToIncorrectAddition() {
        String myTestObjectString = myObj.listenUntilTime("2");

        //Test that an incorrect addition fails
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,  4);
        String listenUntilTimeStringTest = new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());

        assertNotEquals(listenUntilTimeStringTest, myTestObjectString);
    }

    @Test
    public void displayListenUntilTimeIsNotEqualToEmptyString() {
        String myTestObjectString = myObj.listenUntilTime("2");

        //Test that an empty result fails
        assertNotEquals("", myTestObjectString);
    }

    @Test
    public void displayListenUntilTimeIsNotEqualToNull() {
        String myTestObjectString = myObj.listenUntilTime("2");

        //Test that null fails
        assertNotEquals(null, myTestObjectString);
    }
}