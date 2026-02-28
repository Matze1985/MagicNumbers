package com.spiritual.magicnumbers

import android.content.Context
import com.spiritual.magicnumbers.Utils.cleanMarkdown

data class CrossSumResult(
    val sumBeforeReduce: Int,
    val reducedSum: Int,
    val fullCalculationText: String
)

data class EngineResult(
    val number: String,
    val crossSum: CrossSumResult,
    val numerologyTitleResId: Int,
    val digitKeywords: List<Pair<Char, Int>>,
    val digitIntroResIds: List<Pair<Char, Int>>,
    val angelResIds: List<Int>,
    val angelImpulseResId: Int,
    val masterCore: Int?,
    val masterAmplifiers: List<Int>,
    val dominantRepeat: Int?,
    val frequencyScore: Float,
    val energyFlowResId: Int,
    val summaryResId: Int,
    val resonanceTitleResId: Int?,
    val resonanceMeaningResId: Int?,
    val resonanceFocusResId: Int?,
    val karmicDetailResId: Int?
)

object NumerologyEngine {

    private val masterCoreNumbers = listOf(11, 22, 33, 44)
    //TODO: example: 55, 66, 77, 88, 99 | For GUI all logics
    private val masterAmplifierNumbers = listOf(11, 22, 33, 44, 55, 66, 77, 88, 99) // Default: 55 to 99

    // =====================================================
    // CROSS SUM
    // =====================================================
    fun calculateCrossSum(number: String): CrossSumResult {
        val clean = number.filter { it.isDigit() }
        if (clean.isEmpty()) return CrossSumResult(0,0,"")

        val digits = clean.map { it.digitToInt() }
        val steps = mutableListOf<String>()
        var sum = digits.sum()
        steps.add(digits.joinToString(" + ") + " = $sum")
        var reduced = sum

        while (reduced > 9 && reduced !in masterCoreNumbers) {
            val newDigits = reduced.toString().map { it.digitToInt() }
            reduced = newDigits.sum()
            steps.add(newDigits.joinToString(" + ") + " = $reduced")
        }

        return CrossSumResult(sum, reduced, steps.joinToString(" → "))
    }

    // =====================================================
    // NUMEROLOGY TITLE
    // =====================================================
    fun getNumerologyTitleResId(value: Int) = when (value) {
        1 -> R.string.numerology_1
        2 -> R.string.numerology_2
        3 -> R.string.numerology_3
        4 -> R.string.numerology_4
        5 -> R.string.numerology_5
        6 -> R.string.numerology_6
        7 -> R.string.numerology_7
        8 -> R.string.numerology_8
        9 -> R.string.numerology_9
        11 -> R.string.numerology_11
        22 -> R.string.numerology_22
        33 -> R.string.numerology_33
        44 -> R.string.numerology_44
        55 -> R.string.numerology_55
        66 -> R.string.numerology_66
        77 -> R.string.numerology_77
        88 -> R.string.numerology_88
        99 -> R.string.numerology_99
        else -> R.string.numerology_unknown
    }

    // =====================================================
    // DIGITS
    // =====================================================
    fun getDigitKeywords(number: String) =
        number.toCharArray().distinct().mapNotNull {
            val res = when (it) {
                '0' -> R.string.keyword_0
                '1' -> R.string.keyword_1
                '2' -> R.string.keyword_2
                '3' -> R.string.keyword_3
                '4' -> R.string.keyword_4
                '5' -> R.string.keyword_5
                '6' -> R.string.keyword_6
                '7' -> R.string.keyword_7
                '8' -> R.string.keyword_8
                '9' -> R.string.keyword_9
                else -> 0
            }
            if (res != 0) it to res else null
        }

    fun getDigitIntroResIds(number: String) =
        number.groupingBy { it }.eachCount().mapNotNull { (digit, _) ->
            val res = when (digit) {
                '0' -> R.string.digit_0_intro
                '1' -> R.string.digit_1_intro
                '2' -> R.string.digit_2_intro
                '3' -> R.string.digit_3_intro
                '4' -> R.string.digit_4_intro
                '5' -> R.string.digit_5_intro
                '6' -> R.string.digit_6_intro
                '7' -> R.string.digit_7_intro
                '8' -> R.string.digit_8_intro
                '9' -> R.string.digit_9_intro
                else -> 0
            }
            if (res != 0) digit to res else null
        }

    // =====================================================
    // ANGEL NUMBERS (RUN DETECTION)
    // =====================================================
    // ANGEL IMPULSE FROM REDUCED CROSS SUM (1–9)
    fun getAngelImpulseFromReduced(reduced: Int): Int {
        return when (reduced) {
            0 -> R.string.angel_0_1
            1 -> R.string.angel_1_1
            2 -> R.string.angel_2_1
            3 -> R.string.angel_3_1
            4 -> R.string.angel_4_1
            5 -> R.string.angel_5_1
            6 -> R.string.angel_6_1
            7 -> R.string.angel_7_1
            8 -> R.string.angel_8_1
            9 -> R.string.angel_9_1
            11 -> R.string.angel_1_2
            22 -> R.string.angel_2_2
            33 -> R.string.angel_3_2
            44 -> R.string.angel_4_2
            55 -> R.string.angel_5_2
            66 -> R.string.angel_6_2
            77 -> R.string.angel_7_2
            88 -> R.string.angel_8_2
            99 -> R.string.angel_9_2
            else -> 0
        }
    }

    fun getAngelResIds(number: String): List<Int> {
        val results = mutableSetOf<Int>()
        if (number.length < 2) return emptyList()
        detectRuns(number, results)  // Run Detection (22, 333, 4444...)
        detectSequences(number, results) // Ascending / Descending Sequences
        detectMirror(number, results) // Mirror / Palindrome
        detectAlternating(number, results) // Alternating Patterns (1212, 2323...)
        return results.toList()
    }

    // ANGEL RUN DETECTION
    private fun detectRuns(number: String, results: MutableSet<Int>) {
        var i = 0
        while (i < number.length) {
            val digit = number[i]
            var run = 1
            while (i + run < number.length && number[i + run] == digit) {
                run++
            }

            if (run >= 2) {
                val res = when (digit) {
                    '0' -> angelZero(run)
                    '1' -> angelOne(run)
                    '2' -> angelTwo(run)
                    '3' -> angelThree(run)
                    '4' -> angelFour(run)
                    '5' -> angelFive(run)
                    '6' -> angelSix(run)
                    '7' -> angelSeven(run)
                    '8' -> angelEight(run)
                    '9' -> angelNine(run)
                    else -> 0
                }
                if (res != 0) results.add(res)
            }

            i += run
        }
    }

    // ASCENDING / DESCENDING SEQUENCES
    private fun detectSequences(number: String, results: MutableSet<Int>) {
        for (start in 0 until number.length - 2) {
            for (end in start + 3..number.length) {
                val sub = number.substring(start, end)
                val digits = sub.map { it.digitToInt() }
                val ascending = digits.zipWithNext().all { it.second == it.first + 1 }
                val descending = digits.zipWithNext().all { it.second == it.first - 1 }

                if (ascending) {
                    results.add(
                        when (sub) {
                            "123" -> R.string.angel_123
                            "1234" -> R.string.angel_1234
                            "12345" -> R.string.angel_12345
                            "123456" -> R.string.angel_123456
                            else -> R.string.angel_sequence_ascending
                        }
                    )
                }

                if (descending) {
                    results.add(R.string.angel_sequence_descending)
                }
            }
        }
    }

    // MIRROR / PALINDROME
    private fun detectMirror(number: String, results: MutableSet<Int>) {
        if (number.length >= 4) {
            if (number == number.reversed()) {
                results.add(R.string.angel_palindrome)
            }

            if (number.length == 4 &&
                number[0] == number[3] &&
                number[1] == number[2]) {
                results.add(R.string.angel_mirror)
            }
        }
    }

    // ALTERNATING PATTERN
    private fun detectAlternating(number: String, results: MutableSet<Int>) {
        if (number.length >= 4) {
            val pattern = number.substring(0, 2)
            val repeated = pattern.repeat(number.length / 2)
            if (number == repeated) {
                results.add(R.string.angel_alternating)
            }
        }
    }

    private fun angelZero(r:Int)= when(r){1->R.string.angel_0_1;2->R.string.angel_0_2;3->R.string.angel_0_3;4->R.string.angel_0_4;5->R.string.angel_0_5;6->R.string.angel_0_6;else->0}
    private fun angelOne(r:Int)= when(r){1->R.string.angel_1_1;2->R.string.angel_1_2;3->R.string.angel_1_3;4->R.string.angel_1_4;5->R.string.angel_1_5;6->R.string.angel_1_6;else->0}
    private fun angelTwo(r:Int)= when(r){1->R.string.angel_1_2;2->R.string.angel_2_2;3->R.string.angel_2_3;4->R.string.angel_2_4;5->R.string.angel_2_5;6->R.string.angel_2_6;else->0}
    private fun angelThree(r:Int)= when(r){1->R.string.angel_3_1;2->R.string.angel_3_2;3->R.string.angel_3_3;4->R.string.angel_3_4;5->R.string.angel_3_5;6->R.string.angel_3_6;else->0}
    private fun angelFour(r:Int)= when(r){1->R.string.angel_4_1;2->R.string.angel_4_2;3->R.string.angel_4_3;4->R.string.angel_4_4;5->R.string.angel_4_5;6->R.string.angel_4_6;else->0}
    private fun angelFive(r:Int)= when(r){1->R.string.angel_5_1;2->R.string.angel_5_2;3->R.string.angel_5_3;4->R.string.angel_5_4;5->R.string.angel_5_5;6->R.string.angel_5_6;else->0}
    private fun angelSix(r:Int)= when(r){1->R.string.angel_6_1;2->R.string.angel_6_2;3->R.string.angel_6_3;4->R.string.angel_6_4;5->R.string.angel_6_5;6->R.string.angel_6_6;else->0}
    private fun angelSeven(r:Int)= when(r){1->R.string.angel_7_1;2->R.string.angel_7_2;3->R.string.angel_7_3;4->R.string.angel_7_4;5->R.string.angel_7_5;6->R.string.angel_7_6;else->0}
    private fun angelEight(r:Int)= when(r){1->R.string.angel_8_1;2->R.string.angel_8_2;3->R.string.angel_8_3;4->R.string.angel_8_4;5->R.string.angel_8_5;6->R.string.angel_8_6;else->0}
    private fun angelNine(r:Int)= when(r){1->R.string.angel_9_1;2->R.string.angel_9_2;3->R.string.angel_9_3;4->R.string.angel_9_4;5->R.string.angel_9_5;6->R.string.angel_9_6;else->0}

    // =====================================================
    // MASTER / DOMINANT
    // =====================================================
    fun getMasterCore(sum:Int,reduced:Int)= when{
        sum in masterCoreNumbers->sum
        reduced in masterCoreNumbers->reduced
        else->null
    }

    fun getMasterAmplifiers(number:String) = masterAmplifierNumbers.filter{number.contains(it.toString())}

    // =====================================================
    // ORDERED MASTER NUMBERS
    // =====================================================
    fun getOrderedMasters(
        number: String,
        crossReduced: Int,
        masterCore: Int?,
        masterAmplifiers: List<Int>
    ): List<Int> {

        val result = mutableListOf<Int>()
        // 1. Meisterzahl aus Quersumme zuerst
        if (crossReduced in masterCoreNumbers) { result.add(crossReduced) }
        // 2. Core falls noch nicht enthalten
        masterCore?.let { if (!result.contains(it)) result.add(it) }
        // 3. Amplifier nach Auftreten im String sortieren
        masterAmplifiers
            .distinct()
            .sortedBy { number.indexOf(it.toString()) }
            .forEach {
                if (!result.contains(it)) result.add(it)
            }

        return result
    }

    fun getDominantRepeatDigit(number:String):Int?{
        val dominant=number.groupingBy{it}.eachCount().maxByOrNull{it.value}
        return if(dominant!=null && dominant.value>=3) dominant.key.digitToInt() else null
    }

    // =====================================================
    // FREQUENCY
    // =====================================================
    fun calculateFrequencyScore(number:String):Float{
        if(number.isEmpty()) return 0f
        val avg=number.map{it.digitToInt()}.average()/9.0
        return avg.coerceIn(0.1,1.0).toFloat()
    }

    // =====================================================
    // ENERGY FLOW
    // =====================================================
    fun getEnergyFlowResId(masterCore:Int?,dominant:Int?,reduced:Int):Int{
        masterCore?.let{
            return when(it){
                11->R.string.energy_11
                22->R.string.energy_22
                33->R.string.energy_33
                44->R.string.energy_44
                else->R.string.energy_default
            }
        }

        dominant?.let{
            return when(it){
                0->R.string.energy_repeat_0
                1->R.string.energy_repeat_1
                2->R.string.energy_repeat_2
                3->R.string.energy_repeat_3
                4->R.string.energy_repeat_4
                5->R.string.energy_repeat_5
                6->R.string.energy_repeat_6
                7->R.string.energy_repeat_7
                8->R.string.energy_repeat_8
                9->R.string.energy_repeat_9
                else->R.string.energy_default
            }
        }

        return when(reduced){
            0->R.string.energy_0
            1->R.string.energy_1
            2->R.string.energy_2
            3->R.string.energy_3
            4->R.string.energy_4
            5->R.string.energy_5
            6->R.string.energy_6
            7->R.string.energy_7
            8->R.string.energy_8
            9->R.string.energy_9
            else->R.string.energy_default
        }
    }

    // =====================================================
    // RESONANCE
    // =====================================================
    private fun getResonance(number:String):Triple<Int,Int,Int>?{
        if(number.length!=4 && number.length!=6) return null
        val d=number.toCharArray()

        fun allSame()=d.all{it==d[0]}
        fun isAABB()=d[0]==d[1] && d[2]==d[3] && d[0]!=d[2]
        fun isABBA()=d[0]==d[3] && d[1]==d[2] && d[0]!=d[1]
        fun isABAB()=d[0]==d[2] && d[1]==d[3] && d[0]!=d[1]
        fun isAAABBB()=number.length==6 && d[0]==d[1]&&d[1]==d[2]&&d[3]==d[4]&&d[4]==d[5]&&d[0]!=d[3]
        fun isABCABC()=number.length==6 && number.substring(0,3)==number.substring(3,6)
        fun isMirror()=number==number.reversed()

        return when{
            allSame()-> if(number.length==4)
                Triple(R.string.resonance4_title_mono,R.string.resonance4_meaning_mono,R.string.resonance_focus_none)
            else
                Triple(R.string.resonance6_title_mono,R.string.resonance6_meaning_mono,R.string.resonance_focus_none)

            isAABB()->Triple(R.string.resonance4_title_aabb,R.string.resonance4_meaning_aabb,R.string.resonance_focus_frame)
            isABAB()->Triple(R.string.resonance4_title_abab,R.string.resonance4_meaning_abab,R.string.resonance_focus_center)
            isABBA()->Triple(R.string.resonance4_title_abba,R.string.resonance4_meaning_abba,R.string.resonance_focus_center)
            isAAABBB()->Triple(R.string.resonance6_title_aaabbb,R.string.resonance6_meaning_aaabbb,R.string.resonance_focus_frame)
            isABCABC()->Triple(R.string.resonance6_title_abcabc,R.string.resonance6_meaning_abcabc,R.string.resonance_focus_center)
            isMirror()->Triple(R.string.resonance6_title_mirror,R.string.resonance6_meaning_mirror,R.string.resonance_focus_center)
            else-> if(number.length==4)
                Triple(R.string.resonance4_title_none,R.string.resonance4_meaning_none,R.string.resonance_focus_none)
            else
                Triple(R.string.resonance6_title_none,R.string.resonance6_meaning_none,R.string.resonance_focus_none)
        }
    }

    // =====================================================
    // BUILD RESULT
    // =====================================================
    fun buildEngineResult(number:String):EngineResult{
        val cross=calculateCrossSum(number)
        val reduced=cross.reducedSum
        val masterCore=getMasterCore(cross.sumBeforeReduce,reduced)
        val amplifiers=getMasterAmplifiers(number)
        val dominant=getDominantRepeatDigit(number)
        val frequency=calculateFrequencyScore(number)
        val resonance=getResonance(number)
        val summaryResId = getSummaryResId(masterCore,amplifiers,dominant,reduced)

        return EngineResult(
            number,
            cross,
            getNumerologyTitleResId(reduced),
            getDigitKeywords(number),
            getDigitIntroResIds(number),
            getAngelResIds(number),
            angelImpulseResId = getAngelImpulseFromReduced(reduced),
            masterCore,
            amplifiers,
            dominant,
            frequency,
            getEnergyFlowResId(masterCore,dominant,reduced),
            summaryResId,
            resonance?.first,
            resonance?.second,
            resonance?.third,
            getKarmicDetailResId(number)
        )
    }

    fun getSummaryResId(masterCore:Int?,amps:List<Int>,dom:Int?,reduced:Int)=
        when{
            masterCore!=null->when(masterCore){
                11->R.string.summary_master_11
                22->R.string.summary_master_22
                33->R.string.summary_master_33
                44->R.string.summary_master_44
                else->R.string.summary_default}
            amps.size>=2->R.string.summary_multi_master
            dom!=null->when(dom){
                0->R.string.summary_repeat_0
                1->R.string.summary_repeat_1
                2->R.string.summary_repeat_2
                3->R.string.summary_repeat_3
                4->R.string.summary_repeat_4
                5->R.string.summary_repeat_5
                6->R.string.summary_repeat_6
                7->R.string.summary_repeat_7
                8->R.string.summary_repeat_8
                9->R.string.summary_repeat_9
                else->R.string.summary_default}
            else->when(reduced){
                1->R.string.summary_1
                2->R.string.summary_2
                3->R.string.summary_3
                4->R.string.summary_4
                5->R.string.summary_5
                6->R.string.summary_6
                7->R.string.summary_7
                8->R.string.summary_8
                9->R.string.summary_9
                else->R.string.summary_default}
        }

    fun getKarmicDetailResId(number:String):Int?{
        val sum=calculateCrossSum(number).sumBeforeReduce
        return when(sum){
            1->R.string.karmic_detail_1
            2->R.string.karmic_detail_2
            3->R.string.karmic_detail_3
            4->R.string.karmic_detail_4
            5->R.string.karmic_detail_5
            6->R.string.karmic_detail_6
            7->R.string.karmic_detail_7
            8->R.string.karmic_detail_8
            9->R.string.karmic_detail_9
            10->R.string.karmic_detail_10
            11->R.string.karmic_detail_11
            12->R.string.karmic_detail_12
            13->R.string.karmic_detail_13
            14->R.string.karmic_detail_14
            15->R.string.karmic_detail_15
            16->R.string.karmic_detail_16
            17->R.string.karmic_detail_17
            18->R.string.karmic_detail_18
            19->R.string.karmic_detail_19
            20->R.string.karmic_detail_20
            21->R.string.karmic_detail_21
            22->R.string.karmic_detail_22
            23->R.string.karmic_detail_23
            24->R.string.karmic_detail_24
            25->R.string.karmic_detail_25
            26->R.string.karmic_detail_26
            27->R.string.karmic_detail_27
            28->R.string.karmic_detail_28
            29->R.string.karmic_detail_29
            30->R.string.karmic_detail_30
            31->R.string.karmic_detail_31
            32->R.string.karmic_detail_32
            33->R.string.karmic_detail_33
            else->null
        }
    }

    // Get special meaning
    fun getSpecialMeaningResId(value: Int): Int? {
        return when (value) {
            11 -> R.string.master_number_11
            22 -> R.string.master_number_22
            33 -> R.string.master_number_33
            44 -> R.string.master_number_44
            55 -> R.string.special_number_55
            66 -> R.string.special_number_66
            77 -> R.string.special_number_77
            88 -> R.string.special_number_88
            99 -> R.string.special_number_99
            else -> null
        }
    }

    // Copy-Renderer
    fun EngineResult.buildVisibleCopyText(context: Context): String {

        val builder = StringBuilder()
        // --------------------------------------------------
        // TITLE
        // --------------------------------------------------
        builder.appendLine(context.getString(numerologyTitleResId))
        builder.appendLine()
        // --------------------------------------------------
        // MESSAGE FOR THE MOMENT
        // --------------------------------------------------
        builder.appendLine(
            context.getString(
                R.string.section_message_for_the_moment,
                number
            )
        )
        builder.appendLine()
        // --------------------------------------------------
        // CROSS SUM
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.your_cross_sum_is))
        builder.appendLine(crossSum.fullCalculationText)
        builder.appendLine()
        // --------------------------------------------------
        // MASTER ENERGY
        // --------------------------------------------------
        val masterTexts = mutableListOf<String>()
        val shown = mutableSetOf<Int>()

        masterCore?.let { core ->
            getSpecialMeaningResId(core)?.let { resId ->
                masterTexts.add(context.getString(resId).cleanMarkdown())
                shown.add(core)
            }
        }

        masterAmplifiers.forEach { amp ->
            if (!shown.contains(amp)) {
                getSpecialMeaningResId(amp)?.let { resId ->
                    masterTexts.add(context.getString(resId).cleanMarkdown())
                }
            }
        }
        if (masterTexts.isNotEmpty()) {
            builder.appendLine(context.getString(R.string.section_master_energy))
            masterTexts.forEach { builder.appendLine(it) }
            builder.appendLine()
        }
        // -------------------------------------------------
        // ANGEL SECTION
        // -------------------------------------------------
        if (angelImpulseResId != 0 || angelResIds.isNotEmpty()) {

            builder.appendLine(context.getString(R.string.section_angel))

            val shownAngelIds = mutableSetOf<Int>()

            if (angelImpulseResId != 0) {
                shownAngelIds.add(angelImpulseResId)
                builder.appendLine(context.getString(angelImpulseResId).cleanMarkdown())
            }

            angelResIds
                .distinct()
                .forEach { resId ->

                    if (!shownAngelIds.contains(resId)) {
                        shownAngelIds.add(resId)
                        builder.appendLine(context.getString(resId).cleanMarkdown()
                        )
                    }
                }

            builder.appendLine()
        }


        builder.appendLine()
        // --------------------------------------------------
        // FREQUENCY
        // --------------------------------------------------
        val percent = (frequencyScore * 100).toInt()
        builder.appendLine("${context.getString(R.string.section_frequency)} $percent%")
        builder.appendLine()
        // --------------------------------------------------
        // DIGIT VIBRATION
        // --------------------------------------------------
        val uniqueDigits: List<Char> =
            number.filter { it.isDigit() }.toList().distinct()

        if (uniqueDigits.isNotEmpty()) {
            builder.appendLine(context.getString(R.string.section_vibration))

            uniqueDigits.forEach { digit: Char ->
                val res = when (digit) {
                    '0' -> R.string.keyword_0
                    '1' -> R.string.keyword_1
                    '2' -> R.string.keyword_2
                    '3' -> R.string.keyword_3
                    '4' -> R.string.keyword_4
                    '5' -> R.string.keyword_5
                    '6' -> R.string.keyword_6
                    '7' -> R.string.keyword_7
                    '8' -> R.string.keyword_8
                    '9' -> R.string.keyword_9
                    else -> null
                }

                res?.let {
                    builder.appendLine(
                        "$digit – ${context.getString(it).cleanMarkdown()}"
                    )
                }
            }

            builder.appendLine()
        }
        // --------------------------------------------------
        // DIGIT DESCRIPTION
        // --------------------------------------------------
        val counts = number.groupingBy { it }.eachCount()

        if (digitIntroResIds.isNotEmpty()) {

            builder.appendLine(context.getString(R.string.section_description_numbers))

            digitIntroResIds.forEach { (digit, resId) ->

                val count = counts[digit] ?: 1
                val amplifyText =
                    if (count >= 2)
                        context.getString(R.string.digit_occurrence, count)
                    else ""

                val formatted =
                    context.getString(resId, " $amplifyText")

                builder.appendLine(formatted.cleanMarkdown())
                builder.appendLine()
            }
        }
        // --------------------------------------------------
        // RESONANCE
        // --------------------------------------------------
        if (resonanceTitleResId != null) {

            builder.appendLine(context.getString(resonanceTitleResId))

            resonanceMeaningResId?.let {
                builder.appendLine(context.getString(it).cleanMarkdown())
            }

            resonanceFocusResId?.let {
                builder.appendLine(context.getString(it).cleanMarkdown())
            }

            builder.appendLine()
        }
        // --------------------------------------------------
        // SUMMARY
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.section_summary))
        builder.appendLine(context.getString(summaryResId).cleanMarkdown())
        builder.appendLine()
        // --------------------------------------------------
        // KARMIC DETAIL
        // --------------------------------------------------
        karmicDetailResId?.let {
            builder.appendLine(context.getString(it).cleanMarkdown())
            builder.appendLine()
        }
        // --------------------------------------------------
        // ENERGY FLOW
        // --------------------------------------------------
        builder.appendLine(context.getString(R.string.section_energy))
        builder.appendLine(context.getString(energyFlowResId).cleanMarkdown())
        return builder.toString().trim()
    }
}