import com.spiritfenix.kambio.shared.RatesResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window

private val client = HttpClient {
	defaultRequest { url(window.location.origin) }
	install(ContentNegotiation) { json() }
}

suspend fun fetchRates(base: String): RatesResponse =
	client.get("/api/rates/$base").body()