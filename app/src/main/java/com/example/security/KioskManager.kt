package com.example.security

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.os.Build

enum class KioskStatus {
  ACTIVE_LOCKED, // Device Owner LockTask
  ACTIVE_PINNED, // App Pinning
  INACTIVE,      // Supported but not active
  NOT_CONFIGURED // Device owner not yet set
}

object KioskManager {

  fun getKioskStatus(activity: Activity): KioskStatus {
    val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val state = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      am?.lockTaskModeState ?: ActivityManager.LOCK_TASK_MODE_NONE
    } else {
      ActivityManager.LOCK_TASK_MODE_NONE
    }

    return when (state) {
      ActivityManager.LOCK_TASK_MODE_LOCKED -> KioskStatus.ACTIVE_LOCKED
      ActivityManager.LOCK_TASK_MODE_PINNED -> KioskStatus.ACTIVE_PINNED
      else -> {
        if (isDeviceOwnerConfigured(activity)) {
          KioskStatus.INACTIVE
        } else {
          KioskStatus.NOT_CONFIGURED
        }
      }
    }
  }

  fun isDeviceOwnerConfigured(context: Context): Boolean {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
    return dpm?.isDeviceOwnerApp(context.packageName) == true
  }

  fun isLockTaskPermitted(context: Context): Boolean {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
    return dpm?.isLockTaskPermitted(context.packageName) == true
  }

  fun startLockTaskSafe(activity: Activity): Boolean {
    return try {
      activity.startLockTask()
      true
    } catch (e: Exception) {
      // Handled gracefully if not allowed by system
      false
    }
  }

  fun stopLockTaskSafe(activity: Activity): Boolean {
    return try {
      activity.stopLockTask()
      true
    } catch (e: Exception) {
      false
    }
  }

  fun getAdbCommand(context: Context): String {
    val adminComponent = ComponentName(context, ExamDeviceAdminReceiver::class.java).flattenToShortString()
    return "adb shell dpm set-device-owner $adminComponent"
  }
}
