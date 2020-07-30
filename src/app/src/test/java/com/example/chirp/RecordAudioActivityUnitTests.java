package com.example.chirp;

import android.os.Bundle;
import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for Record Audio Activity.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RecordAudioActivityUnitTests {

    RecordAudioActivity myObj = new RecordAudioActivity();

    @Test
    public void startPlayingCreatesPlayerInstance() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        myObj.startPlaying();
        assertNotNull(myObj.player);
    }

    @Test
    public void stopPlayingSetsPlayerToNull() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        myObj.startPlaying();
        myObj.stopPlaying();
        assertNull(myObj.player);
    }

    @Test
    public void startPlayingSetsRecordButtonFalse() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        myObj.startPlaying();
        assertEquals(false, myObj.recordButton.isEnabled());
    }

    @Test
    public void startRecordingCreatesRecorderInstance() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        myObj.startRecording();
        assertNotNull(myObj.recorder);
    }

    @Test
    public void startRecordingSetsPlayButtonFalse() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        myObj.startRecording();
        assertEquals(false, myObj.playButton.isEnabled());
    }

    @Test
    public void stopRecordingSetsRecorderToNull() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        myObj.startRecording();
        myObj.stopRecording();
        assertNull(myObj.recorder);
    }

    @Test
    public void startPlayingSetsDataSourceWithFilename() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        String testFileName = myObj.createFilename();
        myObj.startPlaying();
        //assertNotNull();
    }


    /**
    @Test
    public void recordingFilenameIsCorrect() {

        myObj.createFilename();

        String myTestFilename = "/recordedTweet.3gpp";

        assertEquals(myTestFilename, myObj.fileName);
    }

    @Test
    public void createFileNameReturnsNotNull() {
        String testFileName = myObj.createFilename();
        assertNotNull(testFileName);
    }

    @Test
    public void createFileNameSetsFileName() {
        myObj.createFilename();
        assertNotNull(myObj.fileName);
    }

    @Test
    public void onStopWithNonNullPlayerSetsPlayerToNull() {
        RecordAudioActivity.PlayButton button = mock(RecordAudioActivity.PlayButton.class);
        myObj.playButton = button;

        RecordAudioActivity.RecordButton recordButton = mock(RecordAudioActivity.RecordButton.class);
        myObj.recordButton = recordButton;

        //Player is now NOT NULL [See above tests to confirm]
        myObj.startPlaying();

        myObj.onStop();
        assertNull(myObj.player);
    }**/
}