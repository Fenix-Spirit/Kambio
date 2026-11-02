import com.spiritfenix.kambio.shared.RatesResponse
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.p
import react.useEffect
import react.useState

val App = FC<Props> {
	val (rates, setRates) = useState<RatesResponse?>(null)

	useEffect(Unit) {
		MainScope().launch {
			val result = fetchRates("EUR")
			setRates(result)
			console.log("EUR rates:", result.rates.size, "updated", result.lastUpdated)
		}
	}

	h1 { +"Hello from Kotlin/JS" }

	if (rates != null) {
		div {
			h1 { +"EUR rates:" }
			for ((key, value) in rates.rates) {
				p { +"${key}: $value" }
			}
			p { +"updated ${rates.lastUpdated}" }
		}
	} else {
		p { +"Loading rates..." }
	}
}