private val digits = CharArray(10) { '0' + it }

public fun digitFor(number: Int): Char {
    return digits[number]
}
