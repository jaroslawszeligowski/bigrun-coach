/**
 * Project: bigrun-coach
 * File: NutritionToolTest
 * ------------------------------------------------------------------------
 * Description:
 * Unit tests for NutritionTool.
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

class NutritionToolTest {

    private NutritionTool tool;

    @BeforeEach
    void setUp() {
        tool = new NutritionTool(new NutritionFormatter());
    }

    @Test
    void shouldRecommendNoMidRunHydrationForShortRun() {
        String result = tool.calculateHydration(25, 15.0);

        assertThat(result).containsIgnoringCase("sufficient");
    }

    @Test
    void shouldRecommendDrinkingIntervalForLongRun() {
        String result = tool.calculateHydration(90, 20.0);

        assertThat(result).contains("15 minutes");
        assertThat(result).contains("ml");
    }

    @Test
    void shouldIncreaseFluidIntakeForHighTemperature() {
        String resultCool = tool.calculateHydration(60, 10.0);
        String resultHot = tool.calculateHydration(60, 30.0);

        int coolMl = extractTotalMl(resultCool);
        int hotMl = extractTotalMl(resultHot);

        assertThat(hotMl).isGreaterThan(coolMl);
    }

    @Test
    void shouldRecommendElectrolytesForRunsOverSixtyMinutes() {
        String result = tool.calculateHydration(70, 20.0);

        assertThat(result).containsIgnoringCase("electrolytes");
    }

    @Test
    void shouldReturnErrorWhenHydrationDurationIsZero() {
        String result = tool.calculateHydration(0, 20.0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    @Test
    void shouldRecommendNoCarbohydratesForRunUnderSixtyMinutes() {
        String result = tool.calculateCarbohydrates(45);

        assertThat(result).containsIgnoringCase("no carbohydrates");
    }

    @Test
    void shouldRecommendOneGelForRunBetween60And90Minutes() {
        String result = tool.calculateCarbohydrates(75);

        assertThat(result).contains("gel");
    }

    @Test
    void shouldCalculateTotalCarbsForLongRun() {
        // 3h run → ~180g carbs → ~7 gels
        String result = tool.calculateCarbohydrates(180);

        assertThat(result).contains("180g");
        assertThat(result).contains("gel");
    }

    @Test
    void shouldReturnErrorWhenCarbohydrateDurationIsZero() {
        String result = tool.calculateCarbohydrates(0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    private int extractTotalMl(String result) {
        for (String line : result.split("\n")) {
            if (line.contains("Total fluid")) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    try {
                        return Integer.parseInt(part.replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return 0;
    }
}
