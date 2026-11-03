package lab2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductSelectionTest {

    private val keyboard = Product(1, "Keyboard", true)
    private val mouse = Product(2, "Mouse", false)      // недоступен
    private val monitor = Product(3, "Monitor", true)
    private val webcam = Product(4, "Webcam", true)

    // ---------- Стартовые тесты ----------

    @Test
    fun selectsAvailableProductsInRecommendationOrder() {
        val products = listOf(keyboard, mouse, monitor, webcam)

        val result = selectProducts(
            products,
            listOf(3L, 2L, 3L, 99L, 1L, 4L),
            listOf(1L)
        )

        assertEquals(listOf(monitor, webcam), result)
    }

    @Test
    fun returnsEmptyListWhenThereAreNoRecommendations() {
        val products = listOf(Product(1, "Keyboard", true))

        val result = selectProducts(products, emptyList(), emptyList())

        assertEquals(emptyList<Product>(), result)
    }

    // ---------- Порядок ----------
    @Test
    fun orderFollowsRecommendationsNotCatalog() {
        val products = listOf(webcam, monitor, keyboard)

        val result = selectProducts(products, listOf(1L, 3L, 4L), emptyList())

        assertEquals(listOf(keyboard, monitor, webcam), result)
    }

    // ---------- Повторы ----------
    @Test
    fun duplicatesInRecommendationsAndExclusions() {
        val products = listOf(keyboard, monitor, webcam)

        val result = selectProducts(
            products,
            listOf(3L, 1L, 3L, 4L, 1L, 3L),
            listOf(1L, 1L)
        )

        assertEquals(listOf(monitor, webcam), result)
    }

    // ---------- Недоступные, исключённые, неизвестные ----------
    @Test
    fun skipsUnavailableExcludedAndUnknownProducts() {
        val products = listOf(keyboard, mouse, monitor)

        val result = selectProducts(
            products,
            listOf(2L, 3L, 99L, 1L, 100L),
            listOf(3L, 500L)
        )

        assertEquals(listOf(keyboard), result)
    }

    // ---------- Пустые входы ----------
    @Test
    fun returnsEmptyListForEmptyCatalog() {
        val result = selectProducts(emptyList(), listOf(1L, 2L, 3L), listOf(2L))

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun worksWithEmptyExclusions() {
        val products = listOf(keyboard, monitor, webcam)

        val result = selectProducts(products, listOf(4L, 1L), emptyList())

        assertEquals(listOf(webcam, keyboard), result)
    }

    @Test
    fun returnsEmptyListWhenEverythingIsEmpty() {
        val result = selectProducts(emptyList(), emptyList(), emptyList())

        assertEquals(emptyList<Product>(), result)
    }

    // ---------- Ничего не подходит ----------
    @Test
    fun returnsEmptyListWhenNoProductMatches() {
        val products = listOf(keyboard, mouse, monitor)

        val result = selectProducts(
            products,
            listOf(2L, 1L, 3L, 99L),
            listOf(1L, 3L)
        )

        assertEquals(emptyList<Product>(), result)
    }

    // ---------- Сохранность входов и независимость вызовов ----------
    @Test
    fun doesNotModifyInputs() {
        val products = mutableListOf(webcam, mouse, keyboard, monitor)
        val recommended = mutableListOf(3L, 2L, 3L, 99L, 1L, 4L)
        val excluded = mutableListOf(1L, 1L)

        val productsBefore = products.toList()
        val recommendedBefore = recommended.toList()
        val excludedBefore = excluded.toList()

        val result = selectProducts(products, recommended, excluded)

        assertEquals(listOf(monitor, webcam), result)
        assertEquals(productsBefore, products)
        assertEquals(recommendedBefore, recommended)
        assertEquals(excludedBefore, excluded)
        assertEquals(Product(3, "Monitor", true), result[0])
    }

    @Test
    fun repeatedCallsAreIndependent() {
        val products = listOf(keyboard, mouse, monitor, webcam)

        val first = selectProducts(products, listOf(3L, 1L), emptyList())
        val second = selectProducts(products, listOf(4L, 3L, 1L), listOf(3L))
        val third = selectProducts(products, listOf(3L, 1L), emptyList())

        assertEquals(listOf(monitor, keyboard), first)
        assertEquals(listOf(webcam, keyboard), second)
        assertEquals(listOf(monitor, keyboard), third)
    }

    // ---------- Большой объём (ловит случайную квадратичность) ----------
    @Test
    fun handlesLargeInput() {
        val products = (0L until 50_000L).map { Product(it, "P$it", true) }
        val recommended = (0 until 300_000).map { 49_999L - (it % 50_000) }
        val excluded = (0L until 50_000L step 2).toList()

        val result = selectProducts(products, recommended, excluded)

        val expected = (49_999L downTo 1L step 2).map { Product(it, "P$it", true) }
        assertEquals(expected, result)
    }
}