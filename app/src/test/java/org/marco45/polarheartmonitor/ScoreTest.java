package org.marco45.polarheartmonitor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marco45.polarheartmonitor.Algorithm;

/**
 * Created by garrypolykoff on 1/5/17.
 */
public class ScoreTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testScoreCalculation() {

        int [] testArray = {474, 374, 122, 394, 374,363,897,253, 372};

        int score = Algorithm.calculateHRV_Score(testArray);

        System.out.println("Score: " + score);

    }


}