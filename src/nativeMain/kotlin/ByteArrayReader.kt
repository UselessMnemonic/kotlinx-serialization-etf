import kotlinx.cinterop.*
import platform.zlib.*

actual class ByteArrayReader actual constructor(private val array: ByteArray) {

    private var offset = 0

    actual fun readi8(): Byte {
        val result = array[offset]
        offset += 1
        return result
    }

    actual fun readi16(): Short {
        val result = array.getShortAt(offset)
        offset += 2
        return result
    }

    actual fun readi32(): Int {
        val result = array.getIntAt(offset)
        offset += 4
        return result
    }

    actual fun readi64(): Long {
        val result = array.getLongAt(offset)
        offset += 8
        return result
    }

    actual fun readf32(): Float {
        val result = array.getFloatAt(offset)
        offset += 4
        return result
    }

    actual fun readf64(): Double {
        val result = array.getDoubleAt(offset)
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
        val available = array.size - offset
        val din = array.pin()
        val output = ByteArray(expect)
        val dout = output.pin()

        var result = memScoped {
            val strm = alloc<z_stream_s> {
                zalloc = null
                zfree = null
                opaque = null
                avail_in = 0u
                next_in = null
            }

            var ret = inflateInit(strm.ptr)
            if (ret != Z_OK) {
                return@memScoped ret
            }

            strm.avail_in = available.convert()
            strm.next_in = din.addressOf(offset) as CPointer<UByteVar>

            strm.avail_out = expect.convert()
            strm.next_out = dout.addressOf(0) as CPointer<UByteVar>

            ret = inflate(strm.ptr, Z_NO_FLUSH)
            inflateEnd(strm.ptr)
            return@memScoped ret
        }

        if (result != Z_STREAM_END || result != Z_OK) {
            throw IllegalStateException("zlib error code $result")
        }
        din.unpin()
        dout.unpin()

        offset = array.size
        return output
    }
}
