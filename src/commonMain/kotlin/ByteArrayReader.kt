expect class ByteArrayReader(array: ByteArray) {
    fun readi8(): Byte
    fun readi16(): Short
    fun readi32(): Int
    fun readi64(): Long
    fun readf32(): Float
    fun readf64(): Double
    fun readMulti(length: Int): ByteArray
    fun readString(length: Int): String
    fun readBigNumber(digits: Int): String
    fun readInflated(expect: Int): ByteArray
}
