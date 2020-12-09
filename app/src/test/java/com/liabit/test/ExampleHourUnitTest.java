package com.liabit.test;

import static org.junit.Assert.assertEquals;

import android.content.Intent;

import org.junit.Test;

/**
 * Author:         songtao
 * CreateDate:     2020/12/2 15:17
 */
public class ExampleHourUnitTest {

    @Test
    public void hourTest() {
        testGenerateHour(true, 12);
        testGenerateHour(false, 13);
        testGenerateHour(true, 9);
        testGenerateHour(false, 15);
    }

    private void testGenerateHour(boolean am, int hour) {
        int genHour = generateHour(am, hour);
        assertEquals(am, isAm(genHour));
        assertEquals(hour, getHour(genHour));
    }

    private int generateHour(boolean am, int hour) {
        if (am) {
            return (hour << 1) | 1;
        } else {
            return hour << 1;
        }
    }

    private boolean isAm(int hour) {
        return (hour & 1) == 1;
    }

    private int getHour(int hour) {
        return hour >> 1;
    }

    private static int STATE_IDLE = 0x00000000;
    private static int STATE_EXPANDING = 0x00000001;
    private static int STATE_EXPANDED = 0x00000010;
    private static int STATE_CLOSING = 0x00000100;
    private static int STATE_CLOSED = 0x00001000;
    private static int STATE_LONG_PRESSING = 0x00010000;
    private static int STATE_LONG_PRESS_END = 0x00100000;
    private static int STATE_LONG_PRESS_ABORT = 0x01000000;

    private int mState = 0;

    private boolean hasState(int state) {
        return (mState & state) == state;
    }

    private void addState(int state) {
        mState |= mState;
    }

    private void clearState(int state) {
        mState &= ~state;
    }

}
