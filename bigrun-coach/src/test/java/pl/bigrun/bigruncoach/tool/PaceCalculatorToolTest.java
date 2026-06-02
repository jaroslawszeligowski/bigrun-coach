/**
 * Project: bigrun-coach
 * File: PaceCalculatorToolTest
 * ------------------------------------------------------------------------
 * Description:
 * Unit tests for PaceCalculatorTool.
 * ------------------------------------------------------------------------
 *
 * @author bigrun
 * @version 1.0
 * Copyright (c) 2026 bigrun. All rights reserved.
 * @since 15/05/2026
 */
package pl.bigrun.bigruncoach.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaceCalculatorToolTest {

    private PaceCalculatorTool tool;

    @BeforeEach
    void setUp() {
        tool = new PaceCalculatorTool(new PaceFormatter());
    }

    @Test
    void shouldCalculatePaceForMarathon() {
        // 42.195 km in 240 min → ~5:41 min/km
        String result = tool.calculatePace(42.195, 240.0);

        assertThat(result).contains("5:41");
        assertThat(result).contains("min/km");
    }

    @Test
    void shouldCalculatePaceForHalfMarathon() {
        // 21.0975 km in 120 min → ~5:41 min/km
        String result = tool.calculatePace(21.0975, 120.0);

        assertThat(result).contains("5:41");
        assertThat(result).contains("min/km");
    }

    @Test
    void shouldCalculatePaceForTenKm() {
        // 10 km in 50 min → 5:00 min/km
        String result = tool.calculatePace(10.0, 50.0);

        assertThat(result).contains("5:00");
        assertThat(result).contains("min/km");
    }

    @Test
    void shouldReturnErrorWhenDistanceIsZero() {
        String result = tool.calculatePace(0.0, 60.0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    @Test
    void shouldReturnErrorWhenTimeIsZero() {
        String result = tool.calculatePace(10.0, 0.0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    @Test
    void shouldReturnAllQuarterSplitsForMarathon() {
        String result = tool.calculateSplitStrategy(42.195, 240.0);

        assertThat(result).contains("Q1");
        assertThat(result).contains("Q2");
        assertThat(result).contains("Q3");
        assertThat(result).contains("Q4");
        assertThat(result).containsIgnoringCase("halfway");
        assertThat(result).contains("120");
    }

    @Test
    void shouldIncludeTargetPaceInSplitStrategy() {
        String result = tool.calculateSplitStrategy(10.0, 50.0);

        assertThat(result).contains("5:00 min/km");
    }

    @Test
    void shouldReturnErrorForInvalidSplitStrategyInput() {
        String result = tool.calculateSplitStrategy(-5.0, 60.0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    @Test
    void shouldSuggestConservativeStartAndAggressiveFinish() {
        String result = tool.calculateSplitStrategy(10.0, 50.0);

        assertThat(result).contains("conservatively");
        assertThat(result).contains("push");
    }
}
