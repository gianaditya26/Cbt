package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.ThemeMode

class ExamPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("exambrowser_settings", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_THEME_MODE = "key_theme_mode"
    private const val KEY_ONBOARDING_DONE = "key_onboarding_done"
    private const val KEY_ENFORCE_HTTPS = "key_enforce_https"
    private const val KEY_ALLOW_ALL_HTTPS = "key_allow_all_https"
    private const val KEY_ALLOWED_DOMAINS = "key_allowed_domains"
    private const val KEY_LAST_URL = "key_last_url"
    private const val KEY_KIOSK_ENABLED = "key_kiosk_enabled"
    private const val KEY_EXAM_FINISH_LOGS = "key_exam_finish_logs"

    val DEFAULT_DOMAINS = setOf(
      "forms.gle",
      "docs.google.com",
      "quizizz.com",
      "kahoot.it",
      "cbt.sekolah.id",
      "ujian.kemdikbud.go.id"
    )
  }

  var themeMode: ThemeMode
    get() {
      val raw = prefs.getString(KEY_THEME_MODE, ThemeMode.UNSET.name)
      return try {
        ThemeMode.valueOf(raw ?: ThemeMode.UNSET.name)
      } catch (e: Exception) {
        ThemeMode.UNSET
      }
    }
    set(value) {
      prefs.edit().putString(KEY_THEME_MODE, value.name).apply()
    }

  var isOnboardingDone: Boolean
    get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
    set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

  var enforceHttps: Boolean
    get() = prefs.getBoolean(KEY_ENFORCE_HTTPS, true)
    set(value) = prefs.edit().putBoolean(KEY_ENFORCE_HTTPS, value).apply()

  var allowAllHttps: Boolean
    get() = prefs.getBoolean(KEY_ALLOW_ALL_HTTPS, true)
    set(value) = prefs.edit().putBoolean(KEY_ALLOW_ALL_HTTPS, value).apply()

  var allowedDomains: Set<String>
    get() = prefs.getStringSet(KEY_ALLOWED_DOMAINS, DEFAULT_DOMAINS) ?: DEFAULT_DOMAINS
    set(value) = prefs.edit().putStringSet(KEY_ALLOWED_DOMAINS, value).apply()

  var lastUrl: String
    get() = prefs.getString(KEY_LAST_URL, "https://docs.google.com/forms") ?: "https://docs.google.com/forms"
    set(value) = prefs.edit().putString(KEY_LAST_URL, value).apply()

  var isKioskEnabled: Boolean
    get() = prefs.getBoolean(KEY_KIOSK_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_KIOSK_ENABLED, value).apply()

  fun addAllowedDomain(domain: String) {
    val clean = domain.trim().lowercase()
    if (clean.isNotEmpty()) {
      val set = allowedDomains.toMutableSet()
      set.add(clean)
      allowedDomains = set
    }
  }

  fun removeAllowedDomain(domain: String) {
    val set = allowedDomains.toMutableSet()
    set.remove(domain.trim().lowercase())
    allowedDomains = set
  }

  fun logExamSession(url: String, durationFormatted: String) {
    val timestamp = System.currentTimeMillis()
    val entry = "$timestamp|$url|$durationFormatted"
    val existing = prefs.getString(KEY_EXAM_FINISH_LOGS, "") ?: ""
    val updated = if (existing.isEmpty()) entry else "$entry;$existing"
    prefs.edit().putString(KEY_EXAM_FINISH_LOGS, updated.take(2000)).apply()
  }

  fun getExamHistory(): List<ExamLogEntry> {
    val raw = prefs.getString(KEY_EXAM_FINISH_LOGS, "") ?: ""
    if (raw.isEmpty()) return emptyList()
    return raw.split(";").mapNotNull { line ->
      val parts = line.split("|")
      if (parts.size >= 3) {
        val time = parts[0].toLongOrNull() ?: 0L
        ExamLogEntry(time, parts[1], parts[2])
      } else null
    }
  }
}

data class ExamLogEntry(
  val timestamp: Long,
  val url: String,
  val duration: String
)
