enum class Target {
    ALL,
    COMMON,
    IOS,
    META;

    val common: Boolean
        @JvmName("isCommon")
        get() = this == ALL || this == COMMON

    val ios: Boolean
        @JvmName("isIos")
        get() = this == ALL || this == IOS

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
