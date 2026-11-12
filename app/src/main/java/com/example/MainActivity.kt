package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.ExamPreferences
import com.example.security.KioskManager
import com.example.security.NetworkMonitor
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FullscreenExamScreen
import com.example.ui.screens.ManualUrlScreen
import com.example.ui.screens.PermissionScreen
import com.example.ui.screens.QrScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ThemePersonalizationScreen
import com.example.ui.theme.ExamBrowserTheme
import com.example.ui.theme.ThemeMode

enum class AppScreen {
  SPLASH,
  THEME_SETUP,
  PERMISSIONS,
  DASHBOARD,
  QR_SCANNER,
  MANUAL_URL,
  FULLSCREEN_EXAM,
  SETTINGS
}

class MainActivity : ComponentActivity() {

  private lateinit var preferences: ExamPreferences
  private lateinit var networkMonitor: NetworkMonitor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    preferences = ExamPreferences(this)
    networkMonitor = NetworkMonitor(this)
    networkMonitor.startMonitoring()

    setContent {
      var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
      var currentTheme by remember { mutableStateOf(preferences.themeMode) }
      var enforceHttps by remember { mutableStateOf(preferences.enforceHttps) }
      var allowAllHttps by remember { mutableStateOf(preferences.allowAllHttps) }
      var allowedDomains by remember { mutableStateOf(preferences.allowedDomains) }
      var activeExamUrl by remember { mutableStateOf("") }
      var examStartTime by remember { mutableStateOf(0L) }

      val isOnline by networkMonitor.isConnected.collectAsState()
      val kioskStatus = KioskManager.getKioskStatus(this)

      DisposableEffect(currentScreen) {
        if (currentScreen == AppScreen.FULLSCREEN_EXAM) {
          enterImmersiveExamMode()
        } else {
          exitImmersiveExamMode()
        }
        onDispose { }
      }

      ExamBrowserTheme(themeMode = currentTheme) {
        val windowInsetsModifier = if (currentScreen == AppScreen.FULLSCREEN_EXAM) {
          Modifier.fillMaxSize()
        } else {
          Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
        }

        Box(modifier = windowInsetsModifier) {
          when (currentScreen) {
            AppScreen.SPLASH -> {
              SplashScreen(
                onNavigateNext = {
                  if (preferences.themeMode == ThemeMode.UNSET) {
                    currentScreen = AppScreen.THEME_SETUP
                  } else if (!preferences.isOnboardingDone) {
                    currentScreen = AppScreen.PERMISSIONS
                  } else {
                    currentScreen = AppScreen.DASHBOARD
                  }
                }
              )
            }

            AppScreen.THEME_SETUP -> {
              ThemePersonalizationScreen(
                currentTheme = currentTheme,
                onThemeSelected = { newTheme ->
                  currentTheme = newTheme
                  preferences.themeMode = newTheme
                },
                onApplyTheme = {
                  if (!preferences.isOnboardingDone) {
                    currentScreen = AppScreen.PERMISSIONS
                  } else {
                    currentScreen = AppScreen.DASHBOARD
                  }
                }
              )
            }

            AppScreen.PERMISSIONS -> {
              PermissionScreen(
                onNavigateNext = {
                  preferences.isOnboardingDone = true
                  currentScreen = AppScreen.DASHBOARD
                }
              )
            }

            AppScreen.DASHBOARD -> {
              DashboardScreen(
                kioskStatus = kioskStatus,
                onNavigateToScanQr = {
                  currentScreen = AppScreen.QR_SCANNER
                },
                onNavigateToManualUrl = {
                  currentScreen = AppScreen.MANUAL_URL
                },
                onNavigateToSettings = {
                  currentScreen = AppScreen.SETTINGS
                },
                onQuickStartDemo = { demoUrl ->
                  activeExamUrl = demoUrl
                  examStartTime = System.currentTimeMillis()
                  preferences.lastUrl = demoUrl
                  currentScreen = AppScreen.FULLSCREEN_EXAM
                }
              )
            }

            AppScreen.QR_SCANNER -> {
              BackHandler {
                currentScreen = AppScreen.DASHBOARD
              }
              QrScannerScreen(
                enforceHttps = enforceHttps,
                allowedDomains = allowedDomains,
                allowAllHttps = allowAllHttps,
                onNavigateBack = { currentScreen = AppScreen.DASHBOARD },
                onExamConfirmed = { url ->
                  activeExamUrl = url
                  examStartTime = System.currentTimeMillis()
                  preferences.lastUrl = url
                  currentScreen = AppScreen.FULLSCREEN_EXAM
                }
              )
            }

            AppScreen.MANUAL_URL -> {
              BackHandler {
                currentScreen = AppScreen.DASHBOARD
              }
              ManualUrlScreen(
                initialUrl = preferences.lastUrl,
                enforceHttps = enforceHttps,
                allowedDomains = allowedDomains,
                allowAllHttps = allowAllHttps,
                onNavigateBack = { currentScreen = AppScreen.DASHBOARD },
                onExamConfirmed = { url ->
                  activeExamUrl = url
                  examStartTime = System.currentTimeMillis()
                  preferences.lastUrl = url
                  currentScreen = AppScreen.FULLSCREEN_EXAM
                }
              )
            }

            AppScreen.FULLSCREEN_EXAM -> {
              FullscreenExamScreen(
                examUrl = activeExamUrl,
                isOnline = isOnline,
                onExitConfirmed = {
                  val durationMs = System.currentTimeMillis() - examStartTime
                  val durationMinutes = (durationMs / 60000).coerceAtLeast(1)
                  preferences.logExamSession(activeExamUrl, "$durationMinutes menit")
                  currentScreen = AppScreen.DASHBOARD
                }
              )
            }

            AppScreen.SETTINGS -> {
              BackHandler {
                currentScreen = AppScreen.DASHBOARD
              }
              SettingsScreen(
                currentTheme = currentTheme,
                kioskStatus = kioskStatus,
                enforceHttps = enforceHttps,
                allowAllHttps = allowAllHttps,
                allowedDomains = allowedDomains,
                examHistory = preferences.getExamHistory(),
                onThemeChanged = { newTheme ->
                  currentTheme = newTheme
                  preferences.themeMode = newTheme
                },
                onEnforceHttpsChanged = { value ->
                  enforceHttps = value
                  preferences.enforceHttps = value
                },
                onAllowAllHttpsChanged = { value ->
                  allowAllHttps = value
                  preferences.allowAllHttps = value
                },
                onAddDomain = { domain ->
                  preferences.addAllowedDomain(domain)
                  allowedDomains = preferences.allowedDomains
                },
                onRemoveDomain = { domain ->
                  preferences.removeAllowedDomain(domain)
                  allowedDomains = preferences.allowedDomains
                },
                onNavigateBack = { currentScreen = AppScreen.DASHBOARD }
              )
            }
          }
        }
      }
    }
  }

  private fun enterImmersiveExamMode() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.hide(WindowInsetsCompat.Type.systemBars())
    insetsController.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    if (preferences.isKioskEnabled) {
      KioskManager.startLockTaskSafe(this)
    }
  }

  private fun exitImmersiveExamMode() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.show(WindowInsetsCompat.Type.systemBars())

    KioskManager.stopLockTaskSafe(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    networkMonitor.stopMonitoring()
  }
}
