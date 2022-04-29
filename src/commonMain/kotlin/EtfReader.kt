import EtfType as TYPE

class EtfReader(input: ByteArray) {

    private val data = ByteArrayReader(input)

    init {
        val version = input[0]
        if (version != 131.toByte()) {
            throw InvalidFormatVersionException(version)
        }
    }

    fun readType() = when (val value = data.readi8()) {
        TYPE.NEW_FLOAT_EXT.code -> TYPE.NEW_FLOAT_EXT
        TYPE.BIT_BINARY_EXT.code -> TYPE.BIT_BINARY_EXT
        TYPE.COMPRESSED.code -> TYPE.COMPRESSED
        TYPE.NEW_PID_EXT.code -> TYPE.NEW_PID_EXT
        TYPE.NEWER_REFERENCE_EXT.code -> TYPE.NEWER_REFERENCE_EXT
        TYPE.SMALL_INTEGER_EXT.code -> TYPE.SMALL_INTEGER_EXT
        TYPE.INTEGER_EXT.code -> TYPE.INTEGER_EXT
        TYPE.FLOAT_EXT.code -> TYPE.FLOAT_EXT
        TYPE.ATOM_EXT.code -> TYPE.ATOM_EXT
        TYPE.REFERENCE_EXT.code -> TYPE.REFERENCE_EXT
        TYPE.PORT_EXT.code -> TYPE.PORT_EXT
        TYPE.PID_EXT.code -> TYPE.PID_EXT
        TYPE.SMALL_TUPLE_EXT.code -> TYPE.SMALL_TUPLE_EXT
        TYPE.LARGE_TUPLE_EXT.code -> TYPE.LARGE_TUPLE_EXT
        TYPE.NIL_EXT.code -> TYPE.NIL_EXT
        TYPE.STRING_EXT.code -> TYPE.STRING_EXT
        TYPE.LIST_EXT.code -> TYPE.LIST_EXT
        TYPE.BINARY_EXT.code -> TYPE.BINARY_EXT
        TYPE.SMALL_BIG_EXT.code -> TYPE.SMALL_BIG_EXT
        TYPE.LARGE_BIG_EXT.code -> TYPE.LARGE_BIG_EXT
        TYPE.NEW_FUN_EXT.code -> TYPE.NEW_FUN_EXT
        TYPE.EXPORT_EXT.code -> TYPE.EXPORT_EXT
        TYPE.NEW_REFERENCE_EXT.code -> TYPE.NEW_REFERENCE_EXT
        TYPE.SMALL_ATOM_EXT.code -> TYPE.SMALL_ATOM_EXT
        TYPE.MAP_EXT.code -> TYPE.MAP_EXT
        TYPE.FUN_EXT.code -> TYPE.FUN_EXT
        TYPE.ATOM_UTF8_EXT.code -> TYPE.ATOM_UTF8_EXT
        TYPE.SMALL_ATOM_UTF8_EXT.code -> TYPE.SMALL_ATOM_UTF8_EXT
        else -> throw UnknownTypeException(value)
    }

    fun unpack() = when (val type = readType()) {
        TYPE.SMALL_INTEGER_EXT -> decodeSmallInteger()
        TYPE.INTEGER_EXT -> decodeInteger()
        TYPE.FLOAT_EXT -> decodeFloat()
        TYPE.NEW_FLOAT_EXT -> decodeNewFloat()
        TYPE.ATOM_EXT,
        TYPE.ATOM_UTF8_EXT,
        TYPE.SMALL_ATOM_EXT,
        TYPE.SMALL_ATOM_UTF8_EXT -> decodeAtom(type)
        TYPE.SMALL_TUPLE_EXT -> decodeSmallTuple()
        TYPE.LARGE_TUPLE_EXT -> decodeLargeTuple()
        TYPE.NIL_EXT -> Array(0) {}
        TYPE.STRING_EXT -> decodeStringAsList()
        TYPE.LIST_EXT -> decodeList()
        TYPE.MAP_EXT -> decodeMap()
        TYPE.BINARY_EXT -> decodeBinaryAsString()
        TYPE.SMALL_BIG_EXT -> decodeSmallBig()
        TYPE.LARGE_BIG_EXT -> decodeLargeBig()
        TYPE.NEW_PID_EXT -> decodeNewPID()
        TYPE.NEWER_REFERENCE_EXT -> decodeNewerReference()

        TYPE.REFERENCE_EXT -> decodeReference()
        TYPE.NEW_REFERENCE_EXT -> decodeNewReference()
        TYPE.PORT_EXT -> decodePort()
        TYPE.PID_EXT -> decodePID()
        TYPE.EXPORT_EXT -> decodeExport()
        TYPE.COMPRESSED -> decodeCompressed()
        else -> throw UnsupportedTypeException(type)
    }

    private fun decodeAtom(type: TYPE): Any? {
        val size = when(type) {
            TYPE.SMALL_ATOM_EXT, TYPE.SMALL_ATOM_UTF8_EXT -> data.readi8().toInt()
            TYPE.ATOM_EXT, TYPE.ATOM_UTF8_EXT -> data.readi16().toInt()
            else -> throw UnknownAtomTypeException(type)
        }
        return when (val value = data.readString(size)) {
            "nil", "null" -> null
            "true" -> true
            "false" -> false
            else -> value
        }
    }

    fun decodeArray(size: Int): Array<out Any?> {
        return Array(size) { unpack() }
    }

    fun decodeSmallInteger() = data.readi8()

    fun decodeInteger() = data.readi32()

    fun decodeFloat() = data.readString(31).toFloat()

    fun decodeNewFloat() = data.readf64()

    fun decodeAtom() = decodeAtom(readType())

    fun decodeSmallTuple() = decodeArray(data.readi8().toInt())

    fun decodeLargeTuple() = decodeArray(data.readi32())
}
