package lab2

// O(p + r + e)
fun selectProducts(
    products: List<Product>,
    recommendedIds: List<Long>,
    excludedIds: List<Long>
): List<Product> {
    val productsById: Map<Long, Product> = products.associateBy { it.id }
    val excluded: Set<Long> = excludedIds.toHashSet()
    val addedIds = HashSet<Long>()

    return buildList {
        for (id in recommendedIds) {
            val product = productsById[id] ?: continue

            if (!product.available || id in excluded) continue

            if (addedIds.add(id)) {
                add(product)
            }
        }
    }
}
