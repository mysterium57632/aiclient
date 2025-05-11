package de.paull.gui

import java.awt.Color
import java.awt.Graphics2D

abstract class Drawable(
    protected val master: Master,
    protected var x: Int = 0,
    protected var y: Int = 0,
    protected var width: Int = Frame.SIZE.width,
    protected var height: Int = Frame.SIZE.height) {

    abstract fun draw(g2d: Graphics2D)
    abstract fun start()
    abstract fun stop()

    fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun drawBorderForDebug(g2d: Graphics2D) {
        val c = g2d.color
        g2d.color = Color.RED
        g2d.drawRect(x, y, width - 1, height - 1)
        g2d.color = c
    }
}