package com.zichan.app.data.strategy

interface ValueDepreciationStrategy {
    fun calculate(currentValue: Double, originalPrice: Double, yearsOwned: Int): Double
}

class GeneralDepreciationStrategy : ValueDepreciationStrategy {
    override fun calculate(currentValue: Double, originalPrice: Double, yearsOwned: Int): Double {
        val floor = originalPrice * 0.2
        var value = originalPrice
        repeat(yearsOwned) { value *= 0.9 }
        return maxOf(value, floor)
    }
}

class ElectronicDepreciationStrategy : ValueDepreciationStrategy {
    override fun calculate(currentValue: Double, originalPrice: Double, yearsOwned: Int): Double {
        val rates = listOf(1.0, 0.7, 0.5, 0.3, 0.1)
        val idx = yearsOwned.coerceIn(0, rates.lastIndex)
        return originalPrice * rates[idx]
    }
}

class CollectibleDepreciationStrategy : ValueDepreciationStrategy {
    override fun calculate(currentValue: Double, originalPrice: Double, yearsOwned: Int): Double {
        return originalPrice * 0.95
    }
}

object ValueDepreciationStrategyFactory {
    fun getStrategy(categoryName: String): ValueDepreciationStrategy = when {
        categoryName in listOf("电子产品") -> ElectronicDepreciationStrategy()
        categoryName in listOf("收藏品") -> CollectibleDepreciationStrategy()
        else -> GeneralDepreciationStrategy()
    }
}
