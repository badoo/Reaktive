import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.extra

enum class Target(
    @get:JvmName("isLinuxHosted")
    val linuxHosted: Boolean,
    @get:JvmName("isMacOsHosted")
    val macOsHosted: Boolean
) {
    ALL(linuxHosted = true, macOsHosted = true),
    ALL_LINUX_HOSTED(linuxHosted = true, macOsHosted = false),
    JVM(linuxHosted = true, macOsHosted = false),
    JS(linuxHosted = true, macOsHosted = false),
    LINUX(linuxHosted = true, macOsHosted = false),
    ALL_MACOS_HOSTED(linuxHosted = false, macOsHosted = true),
    IOS(linuxHosted = false, macOsHosted = true),
    WATCHOS(linuxHosted = false, macOsHosted = true),
    TVOS(linuxHosted = false, macOsHosted = true),
    MACOS(linuxHosted = false, macOsHosted = true),
    META(linuxHosted = false, macOsHosted = false);

    companion object {
        private const val PROPERTY = "target"

        @JvmStatic
        fun currentTarget(project: ExtensionAware): Target {
            val value = project.find(PROPERTY).toString()
            return values().find { it.name.equals(value, ignoreCase = true) } ?: ALL
        }

        @JvmStatic
        fun shouldDefineTarget(project: ExtensionAware, targetToDefine: Target): Boolean {
            val currentTarget = currentTarget(project)
            val targetGroupCheck = when {
                currentTarget == ALL -> true
                currentTarget == ALL_LINUX_HOSTED && targetToDefine.linuxHosted -> true
                currentTarget == ALL_MACOS_HOSTED && targetToDefine.macOsHosted -> true
                targetToDefine == ALL_LINUX_HOSTED && currentTarget.linuxHosted -> true
                targetToDefine == ALL_MACOS_HOSTED && currentTarget.macOsHosted -> true
                else -> false
            }
            return currentTarget == targetToDefine || currentTarget == META || targetGroupCheck
        }

        @JvmStatic
        fun shouldPublishTarget(project: ExtensionAware, targetToPublish: Target): Boolean {
            val currentTarget = currentTarget(project)
            if (currentTarget == META) {
                return targetToPublish == META
            }
            return shouldDefineTarget(project, targetToPublish)
        }

        private fun ExtensionAware.find(key: String) =
            if (extra.has(key)) extra.get(key) else null
    }
}
