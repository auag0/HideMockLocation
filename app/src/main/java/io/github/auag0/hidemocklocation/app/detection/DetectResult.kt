package io.github.auag0.hidemocklocation.app.detection

data class DetectResult(
    val value: String?,
    val isDetected: Boolean? = null,
    val isNotSupported: Boolean? = null,
    val isDeprecated: Boolean? = null
)