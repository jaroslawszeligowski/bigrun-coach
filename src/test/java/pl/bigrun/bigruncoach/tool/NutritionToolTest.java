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

    // --- calculateHydration: kategorie ---

    @Test
    void shouldRecommendNoMidRunHydrationForShortRun() {
        String result = tool.calculateHydration(25, 15.0);

        assertThat(result).containsIgnoringCase("sufficient");
    }

    @Test
    void shouldRecommendDrinkingIntervalForExtendedRun() {
        String result = tool.calculateHydration(90, 20.0);

        assertThat(result).contains("15 minutes");
        assertThat(result).contains("ml");
    }

    @Test
    void shouldRecommendElectrolytesForLongRun() {
        String result = tool.calculateHydration(70, 20.0);

        assertThat(result).containsIgnoringCase("electrolytes");
    }

    @Test
    void shouldRecommendAidStationsForUltraRun() {
        String result = tool.calculateHydration(300, 20.0);

        assertThat(result).containsIgnoringCase("aid stations");
    }

    @Test
    void shouldReturnErrorWhenHydrationDurationIsZero() {
        String result = tool.calculateHydration(0, 20.0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    // --- calculateHydration: granice kategorii ---

    @Test
    void shouldTreatThirtyMinutesAsShortRun() {
        String result = tool.calculateHydration(30, 15.0);

        assertThat(result).containsIgnoringCase("sufficient");
    }

    @Test
    void shouldTreatThirtyOneMinutesAsMediumRun() {
        String result = tool.calculateHydration(31, 15.0);

        assertThat(result).contains("15 minutes");
    }

    @Test
    void shouldTreatSixtyMinutesAsLongRun() {
        String result = tool.calculateHydration(60, 20.0);

        assertThat(result).containsIgnoringCase("electrolytes");
    }

    @Test
    void shouldTreatTwoHundredFortyMinutesAsUltra() {
        String result = tool.calculateHydration(240, 20.0);

        assertThat(result).containsIgnoringCase("aid stations");
    }

    // --- calculateHydration: temperatura ---

    @Test
    void shouldIncreaseFluidIntakeForHighTemperature() {
        String resultCool = tool.calculateHydration(60, 10.0);
        String resultHot  = tool.calculateHydration(60, 30.0);

        assertThat(extractTotalMl(resultHot)).isGreaterThan(extractTotalMl(resultCool));
    }

    @Test
    void shouldReduceFluidIntakeInColdWeather() {
        String resultFreezing  = tool.calculateHydration(60, -5.0);
        String resultTemperate = tool.calculateHydration(60, 15.0);

        assertThat(extractTotalMl(resultFreezing)).isLessThan(extractTotalMl(resultTemperate));
    }

    @Test
    void shouldReduceFluidIntakeInCoolWeather() {
        String resultCool      = tool.calculateHydration(60, 5.0);
        String resultTemperate = tool.calculateHydration(60, 15.0);

        assertThat(extractTotalMl(resultCool)).isLessThan(extractTotalMl(resultTemperate));
    }

    // --- calculateCarbohydrates: kategorie ---

    @Test
    void shouldRecommendNoCarbohydratesForRunUnderSixtyMinutes() {
        String result = tool.calculateCarbohydrates(45);

        assertThat(result).containsIgnoringCase("no carbohydrates");
    }

    @Test
    void shouldRecommendOneGelForLongRun() {
        String result = tool.calculateCarbohydrates(75);

        assertThat(result).contains("gel");
    }

    @Test
    void shouldCalculateTotalCarbsForExtendedRun() {
        // 3h run → 180g carbs → 7 gels
        String result = tool.calculateCarbohydrates(180);

        assertThat(result).contains("180g");
        assertThat(result).contains("gel");
    }

    @Test
    void shouldRecommendSolidFoodForUltraRun() {
        String result = tool.calculateCarbohydrates(300);

        assertThat(result).containsIgnoringCase("solid food");
    }

    @Test
    void shouldReturnErrorWhenCarbohydrateDurationIsZero() {
        String result = tool.calculateCarbohydrates(0);

        assertThat(result).containsIgnoringCase("invalid");
    }

    // --- calculateCarbohydrates: granice kategorii ---

    @Test
    void shouldTreatFiftyNineMinutesAsMedium() {
        String result = tool.calculateCarbohydrates(59);

        assertThat(result).containsIgnoringCase("no carbohydrates");
    }

    @Test
    void shouldTreatSixtyMinutesAsLongRunForCarbs() {
        String result = tool.calculateCarbohydrates(60);

        assertThat(result).contains("gel");
    }

    @Test
    void shouldTreatNinetyMinutesAsExtended() {
        String result = tool.calculateCarbohydrates(90);

        assertThat(result).containsIgnoringCase("gels");
        assertThat(result).doesNotContainIgnoringCase("no carbohydrates");
    }

    @Test
    void shouldTreatTwoHundredFortyMinutesAsUltraForCarbs() {
        String result = tool.calculateCarbohydrates(240);

        assertThat(result).containsIgnoringCase("solid food");
    }

    // --- helpers ---

    private int extractTotalMl(String result) {
        for (String line : result.split("\n")) {
            if (line.contains("Total fluid")) {
                for (String part : line.split("\\s+")) {
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
