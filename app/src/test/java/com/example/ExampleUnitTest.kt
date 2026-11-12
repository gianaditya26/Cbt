package com.example

import com.example.security.UrlSecurityValidator
import com.example.security.UrlValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testHttpsValidationSuccess() {
    val result = UrlSecurityValidator.validate(
      input = "https://docs.google.com/forms",
      enforceHttps = true
    )
    assertTrue(result is UrlValidationResult.Valid)
    val valid = result as UrlValidationResult.Valid
    assertEquals("docs.google.com", valid.host)
  }

  @Test
  fun testBlockedSchemes() {
    val jsResult = UrlSecurityValidator.validate("javascript:alert(1)")
    assertTrue(jsResult is UrlValidationResult.Invalid)

    val fileResult = UrlSecurityValidator.validate("file:///sdcard/cheat.html")
    assertTrue(fileResult is UrlValidationResult.Invalid)

    val intentResult = UrlSecurityValidator.validate("intent://exam#Intent;scheme=cbt;end")
    assertTrue(intentResult is UrlValidationResult.Invalid)
  }

  @Test
  fun testHttpBlockedWhenHttpsEnforced() {
    val httpResult = UrlSecurityValidator.validate(
      input = "http://insecure-exam.com",
      enforceHttps = true
    )
    assertTrue(httpResult is UrlValidationResult.Invalid)
  }

  @Test
  fun testDomainWhitelist() {
    val allowed = setOf("sekolah.id")
    val validResult = UrlSecurityValidator.validate(
      input = "https://cbt.sekolah.id/exam",
      allowedDomains = allowed,
      allowAllHttps = false
    )
    assertTrue(validResult is UrlValidationResult.Valid)

    val invalidResult = UrlSecurityValidator.validate(
      input = "https://cheatwebsite.com",
      allowedDomains = allowed,
      allowAllHttps = false
    )
    assertTrue(invalidResult is UrlValidationResult.Invalid)
  }
}
