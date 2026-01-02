package com.g3.launcher.manager

import java.awt.GraphicsEnvironment
import java.lang.management.ManagementFactory
import kotlin.math.roundToInt

object DeviceManager {

    val FRAME_RATE: Int = getScreenRefreshRate()
    val AVAILABLE_PROCESSOR: Int = getPhysicalCoresCount().takeIf { it <= 4 } ?: 4
    val AVAILABLE_RAM: Int = getTotalRamInGB()?.takeIf { it <= 4 } ?: 4

    private fun getScreenRefreshRate(): Int {
        val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val graphicsDevice = graphicsEnvironment.defaultScreenDevice
        val displayMode = graphicsDevice.displayMode
        return displayMode.refreshRate
    }

    private fun getPhysicalCoresCount(): Int {
        return Runtime.getRuntime().availableProcessors()
    }

    private fun getTotalRamInGB(): Int? {
        return try {
            val osBean = ManagementFactory.getOperatingSystemMXBean()
            val clazz = Class.forName("com.sun.management.OperatingSystemMXBean")
            val method = clazz.getMethod("getTotalMemorySize")
            val totalMemory = method.invoke(osBean) as Long
            val result = totalMemory.toDouble() / (1024.0 * 1024.0 * 1024.0)
            result.roundToInt()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
