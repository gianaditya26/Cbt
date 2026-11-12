package com.example.security

import java.net.URI

sealed class UrlValidationResult {
  data class Valid(val normalizedUrl: String, val host: String) : UrlValidationResult()
  data class Warning(val normalizedUrl: String, val host: String, val warningMessage: String) : UrlValidationResult()
  data class Invalid(val reason: String) : UrlValidationResult()
}

object UrlSecurityValidator {

  private val BLOCKED_SCHEMES = setOf(
    "javascript",
    "file",
    "data",
    "content",
    "intent",
    "market"
  )

  fun validate(
    input: String,
    enforceHttps: Boolean = true,
    allowedDomains: Set<String> = emptySet(),
    allowAllHttps: Boolean = true
  ): UrlValidationResult {
    val trimmed = input.trim()
    if (trimmed.isBlank()) {
      return UrlValidationResult.Invalid("URL ujian tidak boleh kosong.")
    }

    // Prepend https:// if user only typed domain (e.g., cbt.sekolah.id)
    val urlWithScheme = if (!trimmed.startsWith("http://", ignoreCase = true) &&
      !trimmed.startsWith("https://", ignoreCase = true)
    ) {
      if (trimmed.contains("://")) {
        trimmed
      } else {
        "https://$trimmed"
      }
    } else {
      trimmed
    }

    val uri = try {
      URI(urlWithScheme)
    } catch (e: Exception) {
      return UrlValidationResult.Invalid("Format URL tidak valid.")
    }

    val scheme = uri.scheme?.lowercase() ?: ""
    if (scheme in BLOCKED_SCHEMES) {
      return UrlValidationResult.Invalid("Protokol '$scheme://' diblokir demi keamanan ujian.")
    }

    if (scheme != "http" && scheme != "https") {
      return UrlValidationResult.Invalid("Hanya protokol web (HTTPS/HTTP) yang diizinkan.")
    }

    if (enforceHttps && scheme != "https") {
      return UrlValidationResult.Invalid("Kebijakan keamanan mewajibkan protokol aman (HTTPS). URL menggunakan HTTP tidak terenkripsi.")
    }

    val host = uri.host?.lowercase() ?: ""
    if (host.isBlank()) {
      return UrlValidationResult.Invalid("Alamat domain server ujian tidak ditemukan.")
    }

    // Check domain whitelist if not allowing all HTTPS
    if (!allowAllHttps && allowedDomains.isNotEmpty()) {
      val isAllowed = allowedDomains.any { allowed ->
        host == allowed || host.endsWith(".$allowed")
      }
      if (!isAllowed) {
        return UrlValidationResult.Invalid("Domain '$host' tidak terdaftar dalam whitelist ujian sekolah.")
      }
    }

    if (scheme == "http") {
      return UrlValidationResult.Warning(
        normalizedUrl = urlWithScheme,
        host = host,
        warningMessage = "Koneksi tidak terenkripsi (HTTP). Pastikan ini adalah server intranet lokal sekolah Anda."
      )
    }

    return UrlValidationResult.Valid(
      normalizedUrl = urlWithScheme,
      host = host
    )
  }
}
