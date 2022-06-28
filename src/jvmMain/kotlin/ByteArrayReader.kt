import java.nio.ByteBuffer
import java.util.zip.Inflater

actual class ByteArrayReader actual constructor(array: ByteArray) {

    private val buffer = ByteBuffer.wrap(array)

    actual fun readi8() = buffer.get()

    actual fun readi16() = buffer.short

    actual fun readi32() = buffer.int

    actual fun readi64() = buffer.long

    actual fun readf32() = buffer.float

    actual fun readf64() = buffer.double

    actual fun readMulti(length: Int): ByteArray {
        val result = ByteArray(length)
        buffer.get(result)
        return result
    }

    actual fun readString(length: Int): String {
        val result = ByteArray(length)
        buffer.get(result)
        return result.decodeToString()
    }

    actual fun readBigNumber(digits: Int): String {
        val builder = StringBuilder(digits + 1)
        val sign = readi8()
        if (sign != 0.toByte()) {
            builder.append('-')
        }
        repeat(digits) {
            builder.append(digitFor(readi8().toInt()))
        }
        return builder.toString()
    }

    actual fun readInflated(expect: Int): ByteArray {
        val result = ByteArray(expect)
        val inflater = Inflater()
        inflater.setInput(buffer.array(), buffer.position(), buffer.remaining())
        inflater.inflate(result)
        return result
    }
}
