package com.liabit.test;

import static org.junit.Assert.assertEquals;

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

}
