package kotlinx.serialization.etf

import kotlinx.cinterop.*
import platform.zlib.*

fun deflate(input: ByteArray, level: Int): ByteArray {
    val din = input.pin()
    val output = ByteArray(16384)
    val dout = output.pin()

    var (total, result) = memScoped {
        val strm = alloc<z_stream_s> {
            zalloc = null
            zfree = null
            opaque = null
        }

        var ret = deflateInit(strm.ptr, level)
        if (ret != Z_OK) {
            return@memScoped (0u to ret)
        }

        strm.avail_in = input.size.convert()
        strm.next_in = din.addressOf(0) as CPointer<UByteVar>

        strm.avail_out = output.size.convert()
        strm.next_out = dout.addressOf(0) as CPointer<UByteVar>

        ret = deflate(strm.ptr, Z_FINISH)
        deflateEnd(strm.ptr)
        return@memScoped ((16384u - strm.avail_out) to ret)
    }
    din.unpin()
    dout.unpin()

    if (result != Z_STREAM_END && result != Z_OK) {
        throw IllegalStateException("zlib error code $result")
    }

    return output.sliceArray(0 until total.toInt())
}

fun inflate(input: ByteArray): ByteArray {
    val din = input.pin()
    val output = ByteArray(16384)
    val dout = output.pin()

    var (total, result) = memScoped {
        val strm = alloc<z_stream_s> {
            zalloc = null
            zfree = null
            opaque = null
            avail_in = 0u
            next_in = null
        }

        var ret = inflateInit(strm.ptr)0

        strm.avail_in = input.size.convert()
        strm.next_in = din.addressOf(0) as CPointer<UByteVar>

        strm.avail_out = output.size.convert()
        strm.next_out = dout.addressOf(0) as CPointer<UByteVar>

        ret = inflate(strm.ptr, Z_NO_FLUSH)
        inflateEnd(strm.ptr)
        return@memScoped ((16384u - strm.avail_out) to ret)
    }
    din.unpin()
    dout.unpin()

    if (result != Z_STREAM_END && result != Z_OK) {
        throw IllegalStateException("zlib error code $result")
    }

    return output.sliceArray(0 until total.toInt())
}

fun main() {
    val msg = "According to all known laws of aviation, there is no way that a bee should be able to fly. Its wings are too small to get its fat little body off the ground. The bee, of course, flies anyways. Because bees don't care what humans think is impossible."
    val bytes = msg.encodeToByteArray()
    val deflated = deflate(bytes, 3)
    val inflated = inflate(deflated)
    println("     msg = ${bytes.joinToString(", ", "[", "]")}")
    println("deflated = ${deflated.joinToString(", ", "[", "]")}")
    println("inflated = ${inflated.joinToString(", ", "[", "]")}")
}
