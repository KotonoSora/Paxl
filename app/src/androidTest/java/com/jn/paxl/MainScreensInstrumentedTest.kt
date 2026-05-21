package com.jn.paxl

import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaxlAppInstrumentedTest {

    @Test
    fun app_context_package_name_is_correct() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals("com.jn.paxl", appContext.packageName)
    }

    @Test
    fun application_class_is_paxl_application() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val applicationClass = appContext.applicationInfo.className

        assertEquals("com.jn.paxl.GameApplication", applicationClass)
    }

    @Test
    fun launch_intent_resolves_main_activity() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val launchIntent =
            appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)

        assertNotNull(launchIntent)
        assertEquals("com.jn.paxl.MainActivity", launchIntent?.component?.className)
    }

    @Test
    fun required_permissions_are_declared_in_manifest() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val packageInfo = appContext.packageManager.getPackageInfo(
            appContext.packageName,
            PackageManager.GET_PERMISSIONS
        )

        val requested = packageInfo.requestedPermissions?.toSet().orEmpty()

        assertTrue(requested.contains("android.permission.INTERNET"))
        assertTrue(requested.contains("com.android.vending.BILLING"))
    }
}
