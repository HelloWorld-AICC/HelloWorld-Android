package com.example.model.community

data class WriteFileRequest(
    val bytes: ByteArray,
    val mimeType: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WriteFileRequest

        if (!bytes.contentEquals(other.bytes)) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        return result
    }
}
