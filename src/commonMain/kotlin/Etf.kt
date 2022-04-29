import kotlinx.serialization.*
import kotlinx.serialization.modules.*
import java.io.ByteArrayInputStream
import kotlin.js.*

public sealed class Etf(
    internal val encodeDefaults: Boolean,
    override val serializersModule: SerializersModule
) : BinaryFormat {

    public companion object Default : ProtoBuf(false, EmptySerializersModule)

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val output = ByteArrayOutput()
        val encoder = ProtobufEncoder(this, ProtobufWriter(output), serializer.descriptor)
        encoder.encodeSerializableValue(serializer, value)
        return output.toByteArray()
    }

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val input = ByteArrayInputStream(bytes)
        val decoder = EtfDecoder(this, EtfReader(input), deserializer.descriptor)
        return decoder.decodeSerializableValue(deserializer)
    }
}

/**
 * Creates an instance of [ProtoBuf] configured from the optionally given [ProtoBuf instance][from]
 * and adjusted with [builderAction].
 */
public fun ProtoBuf(from: ProtoBuf = ProtoBuf, builderAction: ProtoBufBuilder.() -> Unit): ProtoBuf {
    val b = ProtoBufBuilder(from)
    b.builderAction()
    return ProtoBufImpl(b.encodeDefaults, b.serializersModule)
}

/**
 * Builder of the [ProtoBuf] instance provided by `ProtoBuf` factory function.
 */
public class ProtoBufBuilder internal constructor(proto: ProtoBuf) {

    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     */
    public var encodeDefaults: Boolean = proto.encodeDefaults

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [ProtoBuf] instance.
     */
    public var serializersModule: SerializersModule = proto.serializersModule
}
