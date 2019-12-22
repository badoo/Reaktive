enum class Target {
    ALL,
    COMMON,
    DARWIN,
    META;

    val common: Boolean
        @JvmName("isCommon")
        get() = this == ALL || this == COMMON

    val darwin: Boolean
        @JvmName("isDarwin")
        get() = this == ALL || this == DARWIN

    val meta: Boolean
        @JvmName("isMeta")
        get() = this == ALL || this == META

    companion object {

        @JvmStatic
        fun currentTarget(): Target {
            val value = System.getProperty("MP_TARGET")
            return values().find { it.name.equals(value, ignoreCase = true) } ?: ALL
        }
    }
}
