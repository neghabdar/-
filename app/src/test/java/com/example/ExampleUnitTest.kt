package com.example

import com.example.data.model.HeroCatalog
import com.example.data.model.LevelProgression
import com.example.data.seed.QuestionSeedData
import com.example.data.seed.WorldAndMiniGameSeedData
import com.example.util.PersianUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun persianDigitsAndTomanFormatting_areAccurate() {
        assertEquals("۱۲٬۳۴۵", PersianUtils.formatPersianNumber(12345))
        assertEquals("۲٬۵۰۰٬۰۰۰ تومان", PersianUtils.formatToman(2_500_000L))
        assertEquals("مرحله ۴ از ۱۰", PersianUtils.toPersianDigits("مرحله 4 از 10"))
    }

    @Test
    fun jalaliConversion_handlesKnownDate() {
        // 2025-03-21 is 1404-01-01 (Nowruz 1404)
        val (jy, jm, jd) = PersianUtils.gregorianToJalali(2025, 3, 21)
        assertEquals(1404, jy)
        assertEquals(1, jm)
        assertEquals(1, jd)
    }

    @Test
    fun levelProgression_coversAll8RanksCorrectly() {
        assertEquals(8, LevelProgression.ranks.size)
        assertEquals(1, LevelProgression.rankForXp(0).level)
        assertEquals("تازه‌وارد سرزمین صفر", LevelProgression.rankForXp(0).title)
        assertEquals(2, LevelProgression.rankForXp(200).level)
        assertEquals(5, LevelProgression.rankForXp(1100).level)
        assertEquals(8, LevelProgression.rankForXp(3500).level)
        assertEquals("افسانه سرزمین صفر", LevelProgression.rankForXp(3500).title)
    }

    @Test
    fun questionBank_has60PlusFictionalPersianQuestionsWithExpectedMix() {
        val questions = QuestionSeedData.allQuestions()
        assertTrue("Expected at least 60 questions, got ${questions.size}", questions.size >= 60)

        val entertainmentCount = questions.count { it.category == "ENTERTAINMENT" }
        val specializedCount = questions.count { it.category == "SPECIALIZED" }
        assertEquals(questions.size, entertainmentCount + specializedCount)
        assertTrue(entertainmentCount > specializedCount)

        for (q in questions) {
            assertTrue(q.correctOptionIndex in 0..3)
            assertFalse(q.questionText.contains("شعبه صفر"))
            assertFalse(q.questionText.contains("ZERO BRANCH", ignoreCase = true))
        }
    }

    @Test
    fun worldsStagesAndHeroes_matchSpecification() {
        val worlds = WorldAndMiniGameSeedData.worlds()
        val stages = WorldAndMiniGameSeedData.stages()
        val heroes = HeroCatalog.heroes

        assertEquals(6, worlds.size)
        assertEquals(30, stages.size)
        assertEquals(6, heroes.size)

        // First 10 stages belong to playable Worlds 1 and 2; only Stage 1 unlocked initially
        assertTrue(stages.first { it.stageNumber == 1 }.isUnlocked)
        assertFalse(stages.first { it.stageNumber == 2 }.isUnlocked)
        assertEquals(2, worlds.count { !it.isFutureContent })
        assertEquals(4, worlds.count { it.isFutureContent })
        assertEquals(10, stages.count { it.worldId <= 2 })
        assertEquals(20, stages.count { it.worldId >= 3 })
    }
}
