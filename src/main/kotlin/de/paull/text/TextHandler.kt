package de.paull.gui.components

import de.paull.gui.Master
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.regex.Matcher
import java.util.regex.Pattern

class TextHandler {

    private val g2d: Graphics2D
    var img: BufferedImage? = null

    init {
        val dummyImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        g2d = dummyImage.createGraphics()
        g2d.font = Master.FONT
    }

    fun add(s: String) {
        val t = TextElement(s)
    }

    fun extractLatex(input: String?): List<String> {
        val latexSnippets: MutableList<String> = ArrayList()
        val pattern: Pattern = Pattern.compile("(\\\\\\(.*?\\\\\\))|(\\$\\$.*?\\$\\$)", Pattern.DOTALL)
        val matcher: Matcher = pattern.matcher(input)
        while (matcher.find()) {
            latexSnippets.add(matcher.group())
        }
        return latexSnippets
    }


}