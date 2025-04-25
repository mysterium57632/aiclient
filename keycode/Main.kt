import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JLayeredPane

fun main() {
	Frame()
}

typealias L = JLayeredPane

class Frame : JFrame(), KeyListener {

	private val key: JLabel

	init{
		size = Dimension(500, 500)
		setLocationRelativeTo(null)
		addKeyListener(this)
		background = Color(0, 0, 0, 255)
		defaultCloseOperation = EXIT_ON_CLOSE
		isVisible = true

		val layer = L()
		layer.bounds = this.bounds
		layer.isVisible = true
		add(layer)

		val text = JLabel("Press key...")
		text.bounds = Rectangle(10, 10, 200, 30)
		text.font = Font("Monospaced", Font.BOLD, 22)
		layer.add(text, L.MODAL_LAYER)

		key = JLabel("")
		key.bounds = Rectangle(10, 50, 400, 50)
		key.font = Font("Monospaced", Font.PLAIN, 18)
		layer.add(key, L.MODAL_LAYER)
	}

	override fun keyTyped(p0: KeyEvent?) {}

	override fun keyPressed(e: KeyEvent?) {
		if (e == null) return
		key.text = "Key: ${e.keyChar} with code ${e.keyCode}"
	}

	override fun keyReleased(p0: KeyEvent?) {}
}
