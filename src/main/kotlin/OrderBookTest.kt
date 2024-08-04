import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderBookTest {

    private lateinit var orderBook: OrderBook

    @BeforeEach
    fun setup() {
        orderBook = OrderBook()
    }

    @Test
    fun `test order book starts empty`() {
        val orderBookData = orderBook.getOrderBook()
        assertTrue(orderBookData["Asks"]!!.isEmpty())
        assertTrue(orderBookData["Bids"]!!.isEmpty())
    }

    @Test
    fun `test submit buy order`() {
        val order = Order(side = "buy", quantity = 0.5, price = 10000.0)
        orderBook.submitOrder(order)

        val bids = orderBook.getOrderBook()["Bids"]!!
        assertEquals(1, bids.size)
        assertEquals(order, bids[0])
    }

    @Test
    fun `test submit sell order`() {
        val order = Order(side = "sell", quantity = 0.5, price = 10000.0)
        orderBook.submitOrder(order)

        val asks = orderBook.getOrderBook()["Asks"]!!
        assertEquals(1, asks.size)
        assertEquals(order, asks[0])
    }

    @Test
    fun `test order matching`() {
        val buyOrder = Order(side = "buy", quantity = 0.5, price = 10000.0)
        val sellOrder = Order(side = "sell", quantity = 0.5, price = 9000.0)

        orderBook.submitOrder(sellOrder)
        orderBook.submitOrder(buyOrder)

        val trades = orderBook.getRecentTrades()
        assertEquals(1, trades.size)
        assertEquals(0.5, trades[0].quantity)
        assertEquals(9000.0, trades[0].price)

        val orderBookData = orderBook.getOrderBook()
        assertTrue(orderBookData["Asks"]!!.isEmpty())
        assertTrue(orderBookData["Bids"]!!.isEmpty())
    }
}
