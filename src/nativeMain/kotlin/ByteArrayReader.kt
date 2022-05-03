import platform.zlib.Z_NULL
import platform.zlib.inflateInit2
import platform.zlib.z_stream

actual class ByteArrayReader(private val array: ByteArray) {

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
        val strm = CPtr<z_stream>()
        val din = array.sliceArray(offset until array.size)
        val dout = ByteArray(expect)

        strm.zalloc = Z_NULL
        strm.zfree = Z_NULL
        strm.opaque = Z_NULL
        strm.avail_in = 0
        strm.next_in = Z_NULL
        ret = inflateInit(strm)
        if (ret != Z_OK) {
            throw IllegateStateException("zlib error code $ret")
        }
    }
}
