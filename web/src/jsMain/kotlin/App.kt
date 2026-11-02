import com.spiritfenix.kambio.shared.RatesResponse
import kotlinx.coroutines.CancellationException
import react.FC
import react.Key
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.select
import react.dom.html.ReactHTML.ul
import react.useEffect
import react.useState
import web.html.InputType
import web.html.number

val App = FC<Props> {
	var currencies by useState(emptyList<String>())
	var base by useState("EUR")
	var amountText by useState("1")
	var rates by useState<RatesResponse?>(null)
	var error by useState<String?>(null)
	useEffect(Unit) {
		try {
			currencies = fetchCurrencies()
		} catch (e: CancellationException) {
			throw e
		} catch (e: Exception) {
			error = "Could not load currencies"
		}
	}
	useEffect(base) {
		try {
			rates = fetchRates(base)
			error = null
		} catch (e: CancellationException) {
			throw e
		} catch (e: Exception) {
			error = "Could not load rates"
		}
	}
	val amount = amountText.toDoubleOrNull() ?: 0.0

	h1 { +"Kambio" }
	error?.let { div { +it } }

	div {
		input {
			type = InputType.number
			value = amountText
			onChange = { amountText = it.target.value }
		}
		select {
			value = base
			onChange = { base = it.target.value }
			currencies.forEach { code ->
				option {
					value = code
					+code
				}
			}
		}
	}

	rates?.let { r ->
		p { +"Rates from ${r.date}, fetched ${r.lastUpdated}" }
		ul {
			r.rates.entries.sortedBy { it.key }.forEach { (code, rate) ->
				li {
					key = Key(code)
					+"${(amount * rate).asDynamic().toFixed(2)} $code"
				}
			}
		}
	}
}