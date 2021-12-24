package com.example.voiceassistent;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = "MainActivity";
    @Test
    public void addition_isCorrect() {
        String p = ParsingHtmlServiceWiki.getDescription("игра");
        assertEquals(4, 2 + 2);
    }
}