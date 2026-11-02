import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.useEffect

val App = FC<Props> {
	useEffect(Unit) {
		MainScope().launch {
			val rates = fetchRates("EUR")
			console.log("EUR rates:", rates.rates.size, "updated", rates.lastUpdated)
		}
	}
	h1 { +"Hello from Kotlin/JS" }
}