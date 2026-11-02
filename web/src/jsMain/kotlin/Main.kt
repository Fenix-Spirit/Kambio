import react.create
import react.dom.client.createRoot
import web.dom.ElementId
import web.dom.document
import web.html.HTMLElement

fun main() {
	val container = document.getElementById(ElementId("root")) as HTMLElement
	createRoot(container).render(App.create())
}