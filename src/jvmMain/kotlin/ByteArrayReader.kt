import java.nio.ByteBuffer

actual class ByteArrayReader actual constructor(array: ByteArray) {
    private val buffer = ByteBuffer.wrap(array)
    actual fun read8(): Byte = buffer.get()
    actual fun read16(): Short = buffer.short
    actual fun read32(): Int = buffer.int
    actual fun read64(): Long = buffer.long
}
