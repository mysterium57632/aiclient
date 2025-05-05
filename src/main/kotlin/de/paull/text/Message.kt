package de.paull.text

import java.awt.image.BufferedImage
import java.util.regex.Pattern

class TextRender(val rawText: String) {

    companion object {
        private val dummyImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    }

    private val latexInlinePattern: Pattern = Pattern.compile("(\\\\\\(.*?\\\\\\))|(\\$\\$.*?\\$\\$)", Pattern.DOTALL)
    private val lines: MutableList<Line> = mutableListOf()
    private val width = 500
    private val maxWidth = width - 100

    init {
        val lines = rawText.split("\n")
        for (s: String in lines)
            this.lines.add(s.trim())
    }

    private fun editLine(s: String) {

    }

    private fun containsLatex(input: String?): List<String> {
        val latex: String = matcher.group(1)
        val start: Int = matcher.start()
        matches.add(LatexMatch(latex, start))
    }

}