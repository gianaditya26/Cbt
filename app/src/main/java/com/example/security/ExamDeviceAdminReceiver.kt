package com.example.security

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ExamDeviceAdminReceiver : DeviceAdminReceiver() {
  override fun onEnabled(context: Context, intent: Intent) {
    super.onEnabled(context, intent)
    Toast.makeText(context, "EXAMBROWSER: Device Administrator diaktifkan", Toast.LENGTH_SHORT).show()
  }

  override fun onDisabled(context: Context, intent: Intent) {
    super.onDisabled(context, intent)
    Toast.makeText(context, "EXAMBROWSER: Device Administrator dinonaktifkan", Toast.LENGTH_SHORT).show()
  }

  override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
    super.onLockTaskModeEntering(context, intent, pkg)
  }

  override fun onLockTaskModeExiting(context: Context, intent: Intent) {
    super.onLockTaskModeExiting(context, intent)
  }
}
