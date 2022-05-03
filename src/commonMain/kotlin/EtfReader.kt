import EtfType as TYPE

class EtfReader(input: ByteArray, checkVersion: Boolean = true) {

    private val data = ByteArrayReader(input)

    init {
        val version = data.readi8()
        if (checkVersion && version != 131.toByte()) {
            throw InvalidFormatVersionException(version)
        }
    }

    fun readType(): TYPE {
        val value = data.readi8()
        return if (value == 131.toByte()) when (value) {
            TYPE.COMPRESSED.code -> TYPE.COMPRESSED
            else -> throw UnknownTypeException(value)
        } else when (value) {
            TYPE.NEW_FLOAT_EXT.code -> TYPE.NEW_FLOAT_EXT
            TYPE.BIT_BINARY_EXT.code -> TYPE.BIT_BINARY_EXT
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
    }

    fun unpack() = when (val type = readType()) {
        TYPE.SMALL_INTEGER_EXT -> readSmallInteger()
        TYPE.INTEGER_EXT -> readInteger()
        TYPE.FLOAT_EXT -> readFloat()
        TYPE.NEW_FLOAT_EXT -> readNewFloat()
        TYPE.ATOM_EXT,
        TYPE.ATOM_UTF8_EXT,
        TYPE.SMALL_ATOM_EXT,
        TYPE.SMALL_ATOM_UTF8_EXT -> readAtom(type)
        TYPE.SMALL_TUPLE_EXT -> readSmallTuple()
        TYPE.LARGE_TUPLE_EXT -> readLargeTuple()
        TYPE.NIL_EXT -> emptyArray<Unit>()
        TYPE.STRING_EXT -> readStringAsList()
        TYPE.LIST_EXT -> readList()
        TYPE.MAP_EXT -> readMap()
        TYPE.BINARY_EXT -> readBinaryAsString()
        //TYPE.BIT_BINARY_EXT -> readBitBinary()
        TYPE.SMALL_BIG_EXT -> readSmallBig()
        TYPE.LARGE_BIG_EXT -> readLargeBig()
        //TYPE.PID_EXT -> readPID()
        //TYPE.NEW_PID_EXT -> readNewPID()
        //TYPE.REFERENCE_EXT -> readReference()
        //TYPE.NEW_REFERENCE_EXT -> readNewReference()
        //TYPE.NEWER_REFERENCE_EXT -> readNewerReference()
        //TYPE.FUN_EXT -> readFun()
        //TYPE.NEW_FUN_EXT -> readNewFun()
        //TYPE.EXPORT_EXT -> readExport()
        //TYPE.PORT_EXT -> readPort()
        TYPE.COMPRESSED -> readCompressed()
        else -> throw UnsupportedTypeException(type)
    }

    private fun readAtom(type: TYPE): Any? {
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

    fun readArray(size: Int): Array<out Any?> {
        return Array(size) { unpack() }
    }

    fun readSmallInteger() = data.readi8()

    fun readInteger() = data.readi32()

    fun readFloat() = data.readString(31).toFloat()

    fun readNewFloat() = data.readf64()

    fun readAtom() = readAtom(readType())

    fun readSmallTuple() = readArray(data.readi8().toInt())

    fun readLargeTuple() = readArray(data.readi32())

    fun readStringAsList(): ByteArray {
        val len = data.readi16()
        return data.readMulti(len.toInt())
    }

    fun readList(): List<Any?> {
        val len = data.readi32()
        val result = readArray(len)
        val tail = unpack()
        if (tail !is Array<*> || tail.isEmpty()) {
            return ImproperList(*result, tail)
        }
        return result.toMutableList()
    }

    fun readMap(): MutableMap<Any?, Any?> {
        val len = data.readi32()
        val pairs = Array(len) {
            unpack() to unpack()
        }
        return mutableMapOf(*pairs)
    }

    fun readBinaryAsString(): String {
        val len = data.readi32()
        return data.readString(len)
    }

    fun readSmallBig(): String {
        val digits = data.readi8()
        return data.readBigNumber(digits.toInt())
    }

    fun readLargeBig(): String {
        val digits = data.readi32()
        return data.readBigNumber(digits)
    }

    fun readCompressed(): Any? {
        val size = data.readi32()
        val inflated = data.readInflated(size)
        return EtfReader(inflated, false).unpack()
    }
}
