import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.DataView
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import org.w3c.dom.CharacterData
import kotlin.math.exp

actual class ByteArrayReader actual constructor(private val array: ByteArray) {

    private val buffer = DataView(array.unsafeCast<Uint8Array>().buffer)
    private var offset = 0

    actual fun readi8(): Byte {
        val result = array[offset]
        offset += 1
        return result
    }

    actual fun readi16(): Short {
        val result = buffer.getInt16(offset)
        offset += 2
        return result
    }

    actual fun readi32(): Int {
        val result = buffer.getInt32(offset)
        offset += 4
        return result
    }

    actual fun readi64(): Long {
        val high = readi32().toLong()
        val low = readi32().toUInt().toLong()
        return (high shl 32) or low
    }

    actual fun readf32(): Float {
        val result = buffer.getFloat32(offset)
        offset += 8
        return result
    }

    actual fun readf64(): Double {
        val result = buffer.getFloat64(offset)
        offset += 8
        return result
    }

    actual fun readMulti(length: Int): ByteArray {
        val result = array.sliceArray(offset until offset + length)
        offset += length
        return result
    }

    actual fun readString(length: Int): String {
        val result = array.decodeToString(offset, offset + length)
        offset += length
        return result
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
        TODO("Not yet implemented")
    }
}
