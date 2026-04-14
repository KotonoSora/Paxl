# Jetpack Compose
-keepclassmembers class androidx.compose.ui.platform.AndroidComposeView {
    void *;
}
-keep class androidx.compose.runtime.Recomposer { *; }

# Hilt
-keep class dagger.hilt.android.internal.managers.ViewComponentManager$Objer_ViewComponentBuilder { *; }
-keep class * extends androidx.lifecycle.ViewModel

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * { @androidx.room.Entity *; }
-keep class * { @androidx.room.Dao *; }
-keep class * { @androidx.room.Database *; }
-keep class * { @androidx.room.TypeConverter *; }

# DataStore
-keep class androidx.datastore.** { *; }

# Moshi / Retrofit (used by dependencies)
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-dontwarn com.squareup.moshi.**

# Google Play Billing
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.api.**

# Game Models (Keep for persistence/serialization)
-keep class com.kotonosora.paxl.model.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**
