import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import java.time.Instant
import java.util.UUID

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val side: String, // "buy" or "sell"
    var quantity: Double,
    val price: Double,
    val currencyPair: String = "BTCZAR"
)

data class Trade(
    val price: Double,
    val quantity: Double,
    val currencyPair: String = "BTCZAR",
    val tradedAt: Instant = Instant.now(),
    val takerSide: String
)

class OrderBook {
    private val buyOrders = mutableListOf<Order>()
    private val sellOrders = mutableListOf<Order>()
    private val trades = mutableListOf<Trade>()

    fun getOrderBook() = mapOf(
        "Asks" to sellOrders.sortedBy { it.price },
        "Bids" to buyOrders.sortedByDescending { it.price }
    )

    fun submitOrder(order: Order) {
        if (order.side == "buy") {
            matchOrder(order, sellOrders, "sell")
            if (order.quantity > 0) buyOrders.add(order)
        } else {
            matchOrder(order, buyOrders, "buy")
            if (order.quantity > 0) sellOrders.add(order)
        }
    }

    private fun matchOrder(order: Order, oppositeOrders: MutableList<Order>, takerSide: String) {
        val iterator = oppositeOrders.iterator()
        while (iterator.hasNext()) {
            val oppositeOrder = iterator.next()
            if ((order.side == "buy" && order.price >= oppositeOrder.price) ||
                (order.side == "sell" && order.price <= oppositeOrder.price)
            ) {
                val tradedQuantity = minOf(order.quantity, oppositeOrder.quantity)
                trades.add(
                    Trade(
                        price = oppositeOrder.price,
                        quantity = tradedQuantity,
                        takerSide = takerSide
                    )
                )
                order.quantity -= tradedQuantity
                oppositeOrder.quantity -= tradedQuantity

                if (oppositeOrder.quantity <= 0) iterator.remove()
                if (order.quantity <= 0) break
            }
        }
    }

    fun getRecentTrades(): List<Trade> = trades.takeLast(10)
}

fun main() {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    val orderBook = OrderBook()

    router.get("/orderbook").handler { ctx ->
        ctx.response().putHeader("content-type", "application/json")
            .end(orderBook.getOrderBook().toString())
    }

    router.post("/orders/limit").handler { ctx ->
        val order = ctx.bodyAsJson.mapTo(Order::class.java)
        orderBook.submitOrder(order)
        ctx.response().end("Order submitted")
    }

    router.get("/trades").handler { ctx ->
        ctx.response().putHeader("content-type", "application/json")
            .end(orderBook.getRecentTrades().toString())
    }

    vertx.createHttpServer().requestHandler(router).listen(8080)
}


