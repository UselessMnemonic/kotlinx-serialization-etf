import kotlinx.serialization.*
import kotlinx.serialization.modules.*

public sealed class Etf(
    internal val encodeDefaults: Boolean,
    override val serializersModule: SerializersModule
) : BinaryFormat {
/*
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
    }*/
}
