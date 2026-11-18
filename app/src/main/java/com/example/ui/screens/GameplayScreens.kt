package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.QuestionEntity
import com.example.data.local.StageEntity
import com.example.data.model.MiniGameType
import com.example.data.repository.StageCompletionSummary
import com.example.data.seed.WorldAndMiniGameSeedData
import com.example.ui.components.AnimatedGoldenSparkles
import com.example.ui.components.FriendlyMonsterArtwork
import com.example.ui.components.PersianActionPillButton
import com.example.ui.components.PersianOrnateBluePanel
import com.example.ui.components.PersianParchmentCard
import com.example.ui.theme.CreamText
import com.example.ui.theme.DeepPersianBlue
import com.example.ui.theme.InkBrown
import com.example.ui.theme.JadeEmerald
import com.example.ui.theme.LapisBlue
import com.example.ui.theme.MutedInk
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCream
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.PersianTurquoiseLight
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.WarmGoldLight
import com.example.util.GameAudioManager
import com.example.util.PersianUtils

@Composable
fun MiniGamePlayScreen(
    stage: StageEntity,
    questions: List<QuestionEntity>,
    soundEnabled: Boolean,
    onBack: () -> Unit,
    onFinishStage: (correctCount: Int, totalCount: Int, mistakes: Int, durationSeconds: Int) -> Unit
) {
    BackHandler(onBack = onBack)
    val startTimeMillis = remember { System.currentTimeMillis() }
    val miniGame = MiniGameType.entries.firstOrNull { it.name == stage.miniGameType } ?: MiniGameType.VAULT_PUZZLE

    var phaseIndex by remember { mutableIntStateOf(0) } // 0 = specialized mini-game challenge (for Vault/MissingFile), 1..2 = follow-up questions; for MonsterBattle = 3 questions
    var correctCount by remember { mutableIntStateOf(0) }
    var mistakes by remember { mutableIntStateOf(0) }

    val totalSteps = 3

    fun completeGame() {
        val elapsedSec = ((System.currentTimeMillis() - startTimeMillis) / 1000L).toInt().coerceAtLeast(5)
        GameAudioManager.playVictory(soundEnabled)
        onFinishStage(correctCount.coerceAtLeast(1), totalSteps, mistakes, elapsedSec)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, LapisBlue)))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        AnimatedGoldenSparkles()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "خروج از مرحله", tint = CreamText)
                    }
                    Column {
                        Text(
                            text = stage.title,
                            color = WarmGoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${miniGame.persianTitle} • گام ${PersianUtils.toPersianDigits(phaseIndex + 1)} از ${PersianUtils.toPersianDigits(totalSteps)}",
                            color = PersianTurquoiseLight,
                            fontSize = 12.sp
                        )
                    }
                }

                // Player Opportunities Hearts
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val remainingHearts = (3 - mistakes).coerceAtLeast(0)
                    repeat(3) { idx ->
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = if (idx < remainingHearts) RubyCrimson else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(modifier = Modifier.widthIn(max = 620.dp)) {
                when {
                    miniGame == MiniGameType.VAULT_PUZZLE && phaseIndex == 0 -> {
                        val vault = WorldAndMiniGameSeedData.vaultChallenges[stage.stageNumber]
                            ?: WorldAndMiniGameSeedData.vaultChallenges.values.first()
                        VaultPuzzleChallengeView(
                            title = vault.title,
                            scenario = vault.scenario,
                            lines = vault.lines,
                            questionPrompt = vault.questionPrompt,
                            correctAnswerNumber = vault.correctAnswerNumber,
                            unitLabel = vault.unitLabel,
                            choices = vault.choices,
                            explanation = vault.explanation,
                            soundEnabled = soundEnabled,
                            onSolved = { wasCorrect ->
                                if (wasCorrect) correctCount++ else mistakes++
                                if (questions.isEmpty()) completeGame() else phaseIndex = 1
                            }
                        )
                    }

                    miniGame == MiniGameType.MISSING_FILE && phaseIndex == 0 -> {
                        val doc = WorldAndMiniGameSeedData.missingFileChallenges[stage.stageNumber]
                            ?: WorldAndMiniGameSeedData.missingFileChallenges.values.first()
                        MissingFileChallengeView(
                            challenge = doc,
                            soundEnabled = soundEnabled,
                            onSolved = { wasCorrect ->
                                if (wasCorrect) correctCount++ else mistakes++
                                if (questions.isEmpty()) completeGame() else phaseIndex = 1
                            }
                        )
                    }

                    else -> {
                        val qIndex = if (miniGame == MiniGameType.MONSTER_BATTLE) phaseIndex else (phaseIndex - 1)
                        val question = questions.getOrNull(qIndex) ?: questions.firstOrNull()
                        if (question != null) {
                            MonsterAndQuizBattleView(
                                stage = stage,
                                question = question,
                                roundIndex = phaseIndex,
                                monsterHitsRemaining = (3 - correctCount).coerceAtLeast(1),
                                isBoss = stage.isBossStage,
                                soundEnabled = soundEnabled,
                                onNextRound = { wasCorrect ->
                                    if (wasCorrect) correctCount++ else mistakes++
                                    if (phaseIndex + 1 >= totalSteps) {
                                        completeGame()
                                    } else {
                                        phaseIndex++
                                    }
                                }
                            )
                        } else {
                            completeGame()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultPuzzleChallengeView(
    title: String,
    scenario: String,
    lines: List<Pair<String, String>>,
    questionPrompt: String,
    correctAnswerNumber: Long,
    unitLabel: String,
    choices: List<Long>,
    explanation: String,
    soundEnabled: Boolean,
    onSolved: (Boolean) -> Unit
) {
    var typedInput by remember { mutableStateOf("") }
    var selectedChoice by remember { mutableStateOf<Long?>(null) }
    var submitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LockOpen, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, color = InkBrown, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(scenario, color = MutedInk, fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(12.dp))

            // Ledger table
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepPersianBlue,
                border = BorderStroke(1.5.dp, SaffronGold)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    lines.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, color = CreamText.copy(alpha = 0.9f), fontSize = 13.sp)
                            Text(value, color = WarmGoldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(questionPrompt, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))

            // Numeric input option
            OutlinedTextField(
                value = typedInput,
                onValueChange = { raw ->
                    typedInput = raw.filter { it.isDigit() }
                    selectedChoice = typedInput.toLongOrNull()
                },
                enabled = !submitted,
                label = { Text("عدد را وارد کنید (به $unitLabel)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PersianTurquoise,
                    unfocusedBorderColor = ParchmentBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("vault_number_input")
            )

            Spacer(Modifier.height(10.dp))
            Text("یا یکی از گزینه‌ها را انتخاب کنید:", color = MutedInk, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                choices.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { choice ->
                            val isSelected = selectedChoice == choice
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(enabled = !submitted) {
                                        selectedChoice = choice
                                        typedInput = choice.toString()
                                    }
                                    .testTag("vault_choice_$choice"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PersianTurquoise else Color.White.copy(alpha = 0.8f),
                                border = BorderStroke(1.5.dp, if (isSelected) WarmGoldLight else ParchmentBorder)
                            ) {
                                Text(
                                    text = "${PersianUtils.formatPersianNumber(choice)} $unitLabel",
                                    color = if (isSelected) Color.White else InkBrown,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            if (!submitted) {
                PersianActionPillButton(
                    text = "باز کردن گاوصندوق",
                    onClick = {
                        val answer = selectedChoice ?: typedInput.toLongOrNull() ?: -1L
                        isCorrect = answer == correctAnswerNumber
                        submitted = true
                        if (isCorrect) GameAudioManager.playSuccessChime(soundEnabled)
                        else GameAudioManager.playError(soundEnabled)
                    },
                    enabled = selectedChoice != null || typedInput.isNotBlank(),
                    containerColor = JadeEmerald,
                    modifier = Modifier.fillMaxWidth().testTag("vault_submit_button")
                )
            } else {
                FeedbackExplanationBox(
                    isCorrect = isCorrect,
                    explanation = explanation,
                    onContinue = { onSolved(isCorrect) }
                )
            }
        }
    }
}

@Composable
private fun MissingFileChallengeView(
    challenge: com.example.data.model.MissingFileChallenge,
    soundEnabled: Boolean,
    onSolved: (Boolean) -> Unit
) {
    var inspectedFieldKey by remember { mutableStateOf<String?>(null) }
    var foundSuspiciousField by remember { mutableStateOf(false) }
    var selectedFixIndex by remember { mutableStateOf<Int?>(null) }
    var submitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Description, contentDescription = null, tint = PersianTurquoise, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(challenge.documentTitle, color = InkBrown, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                    Text(challenge.documentCode, color = MutedInk, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(challenge.briefing, color = MutedInk, fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(12.dp))

            // Document fields
            challenge.fields.forEach { field ->
                val isInspected = inspectedFieldKey == field.key
                val borderColor = when {
                    isInspected && field.isSuspicious -> JadeEmerald
                    isInspected -> RubyCrimson
                    else -> ParchmentBorder
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !submitted) {
                            inspectedFieldKey = field.key
                            if (field.isSuspicious) {
                                foundSuspiciousField = true
                                GameAudioManager.playSuccessChime(soundEnabled)
                            } else {
                                GameAudioManager.playClick(soundEnabled)
                            }
                        }
                        .testTag("doc_field_${field.key}"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = BorderStroke(1.8.dp, borderColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(field.label, color = MutedInk, fontSize = 11.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(field.value, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (isInspected) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (field.isSuspicious) "کشف شد! ${field.correctionHint}" else "این بخش ظاهراً درست است؛ بخش دیگری را بررسی کنید.",
                                color = if (field.isSuspicious) JadeEmerald else RubyCrimson,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = foundSuspiciousField) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(challenge.fixQuestion, color = InkBrown, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    challenge.fixOptions.forEachIndexed { idx, opt ->
                        val selected = selectedFixIndex == idx
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !submitted) { selectedFixIndex = idx }
                                .testTag("doc_fix_option_$idx"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) PersianTurquoise else ParchmentCream,
                            border = BorderStroke(1.5.dp, if (selected) WarmGoldLight else ParchmentBorder)
                        ) {
                            Text(
                                text = opt,
                                color = if (selected) Color.White else InkBrown,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    if (!submitted) {
                        PersianActionPillButton(
                            text = "ثبت اصلاح سند",
                            onClick = {
                                isCorrect = selectedFixIndex == challenge.correctFixIndex
                                submitted = true
                                if (isCorrect) GameAudioManager.playSuccessChime(soundEnabled)
                                else GameAudioManager.playError(soundEnabled)
                            },
                            enabled = selectedFixIndex != null,
                            containerColor = RoyalViolet,
                            modifier = Modifier.fillMaxWidth().testTag("doc_submit_button")
                        )
                    } else {
                        FeedbackExplanationBox(
                            isCorrect = isCorrect,
                            explanation = challenge.explanation,
                            onContinue = { onSolved(isCorrect) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonsterAndQuizBattleView(
    stage: StageEntity,
    question: QuestionEntity,
    roundIndex: Int,
    monsterHitsRemaining: Int,
    isBoss: Boolean,
    soundEnabled: Boolean,
    onNextRound: (Boolean) -> Unit
) {
    var selectedOption by remember(question.id, roundIndex) { mutableStateOf<Int?>(null) }
    var submitted by remember(question.id, roundIndex) { mutableStateOf(false) }
    var isCorrect by remember(question.id, roundIndex) { mutableStateOf(false) }

    val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)
    val monsterName = when (stage.stageNumber) {
        5 -> "اژدهای مغایرت"
        10 -> "غول اضافه‌کاری"
        else -> "نگهبان کاغذی"
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Monster Battle Stage Card
        PersianOrnateBluePanel(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FriendlyMonsterArtwork(
                    isBoss = isBoss,
                    stageNumber = stage.stageNumber,
                    isHit = submitted && isCorrect,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(monsterName, color = SaffronGold, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                    Text(
                        text = if (question.category == "ENTERTAINMENT") "چالش هوش و سرگرمی • ${question.subCategory}"
                        else "چالش تخصصی تخیلی • ${question.subCategory}",
                        color = PersianTurquoiseLight,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "توان باقیمانده حریف: ${PersianUtils.toPersianDigits(monsterHitsRemaining)} ضربه",
                        color = CreamText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Question Card
        PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = question.questionText,
                    color = InkBrown,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.height(12.dp))

                options.forEachIndexed { idx, optText ->
                    val chosen = selectedOption == idx
                    val bgColor = when {
                        submitted && idx == question.correctOptionIndex -> JadeEmerald
                        submitted && chosen && idx != question.correctOptionIndex -> RubyCrimson
                        chosen -> PersianTurquoise
                        else -> Color.White.copy(alpha = 0.85f)
                    }
                    val txtColor = if (chosen || (submitted && idx == question.correctOptionIndex)) Color.White else InkBrown

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !submitted) { selectedOption = idx }
                            .testTag("quiz_option_$idx"),
                        shape = RoundedCornerShape(14.dp),
                        color = bgColor,
                        border = BorderStroke(1.5.dp, if (chosen) WarmGoldLight else ParchmentBorder)
                    ) {
                        Text(
                            text = optText,
                            color = txtColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(13.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                if (!submitted) {
                    PersianActionPillButton(
                        text = "ارسال پاسخ قهرمان",
                        onClick = {
                            isCorrect = selectedOption == question.correctOptionIndex
                            submitted = true
                            if (isCorrect) GameAudioManager.playSuccessChime(soundEnabled)
                            else GameAudioManager.playError(soundEnabled)
                        },
                        enabled = selectedOption != null,
                        containerColor = RoyalViolet,
                        modifier = Modifier.fillMaxWidth().testTag("quiz_submit_button")
                    )
                } else {
                    FeedbackExplanationBox(
                        isCorrect = isCorrect,
                        explanation = question.explanation,
                        onContinue = { onNextRound(isCorrect) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackExplanationBox(
    isCorrect: Boolean,
    explanation: String,
    onContinue: () -> Unit
) {
    Column {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isCorrect) JadeEmerald.copy(alpha = 0.14f) else RubyCrimson.copy(alpha = 0.14f),
            border = BorderStroke(1.5.dp, if (isCorrect) JadeEmerald else RubyCrimson),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (isCorrect) JadeEmerald else RubyCrimson
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isCorrect) "آفرین! پاسخ کاملاً درست است" else "نکته آموزشی",
                        color = if (isCorrect) JadeEmerald else RubyCrimson,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(explanation, color = InkBrown, fontSize = 13.sp, lineHeight = 20.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        PersianActionPillButton(
            text = "ادامه",
            onClick = onContinue,
            containerColor = JadeEmerald,
            modifier = Modifier.fillMaxWidth().testTag("challenge_continue_button")
        )
    }
}

@Composable
fun MissionResultScreen(
    summary: StageCompletionSummary,
    onBackToMap: () -> Unit,
    onOpenFutureWorlds: () -> Unit,
    onGoHome: () -> Unit
) {
    BackHandler(onBack = onGoHome)
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPersianBlue, RoyalViolet.copy(alpha = 0.85f))))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        AnimatedGoldenSparkles()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = revealed, enter = fadeIn() + scaleIn()) {
                Box(modifier = Modifier.widthIn(max = 540.dp)) {
                    PersianParchmentCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_treasure_chest),
                                contentDescription = "صندوقچه پاداش",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 160.dp, height = 108.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "پیروزی در ${summary.stageTitle}!",
                                color = InkBrown,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(summary.rating.stars) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(30.dp))
                                }
                            }
                            Text(
                                text = summary.rating.persianTitle,
                                color = PersianTurquoise,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.height(12.dp))

                            // Score & Reward breakdown
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = DeepPersianBlue,
                                border = BorderStroke(1.5.dp, SaffronGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    RewardRow("امتیاز پایه", "+${PersianUtils.formatPersianNumber(summary.baseScore)}")
                                    RewardRow("پاداش سرعت", "+${PersianUtils.formatPersianNumber(summary.speedBonus)}")
                                    RewardRow("پاداش دقت بی‌نقص", "+${PersianUtils.formatPersianNumber(summary.noMistakeBonus)}")
                                    if (summary.specialMissionBonus > 0) {
                                        RewardRow("پاداش مأموریت روزانه", "+${PersianUtils.formatPersianNumber(summary.specialMissionBonus)}")
                                    }
                                    RewardRow("مجموع امتیاز مرحله", PersianUtils.formatPersianNumber(summary.totalScoreAwarded))
                                    RewardRow("امتیاز تجربه (XP)", "+${PersianUtils.toPersianDigits(summary.xpAwarded)}")
                                    RewardRow("سکه زرین", "+${PersianUtils.toPersianDigits(summary.coinsAwarded)}")
                                }
                            }

                            if (!summary.isFirstCompletion) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "این مرحله قبلاً تکمیل شده بود؛ پاداش تمرینی لحاظ شد.",
                                    color = MutedInk,
                                    fontSize = 11.sp
                                )
                            }

                            if (summary.unlockedWorld2) {
                                Spacer(Modifier.height(10.dp))
                                Surface(shape = RoundedCornerShape(12.dp), color = JadeEmerald.copy(alpha = 0.16f), border = BorderStroke(1.dp, JadeEmerald)) {
                                    Text(
                                        text = "تبریک! دروازه قلمرو دوم «شهر کارگزینی» باز شد!",
                                        color = JadeEmerald,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            if (summary.unlockedWorld3Preview) {
                                Spacer(Modifier.height(10.dp))
                                PersianActionPillButton(
                                    text = "مشاهده پیش‌نمایش قلمرو سوم: جنگل بخشنامه‌ها",
                                    onClick = onOpenFutureWorlds,
                                    containerColor = PersianTurquoise,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PersianTurquoise.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = PersianTurquoise, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "نتیجه با شناسه یکتا در دستگاه ثبت شد و برای تأیید ابری در صف قرار گرفت.",
                                        color = InkBrown,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PersianActionPillButton(
                                    text = "نقشه مراحل",
                                    onClick = onBackToMap,
                                    containerColor = PersianTurquoise,
                                    modifier = Modifier.weight(1f).testTag("result_map_button")
                                )
                                PersianActionPillButton(
                                    text = "بازگشت به خانه",
                                    onClick = onGoHome,
                                    containerColor = RoyalViolet,
                                    modifier = Modifier.weight(1f).testTag("result_home_button")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = CreamText, fontSize = 12.sp)
        Text(value, color = WarmGoldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
