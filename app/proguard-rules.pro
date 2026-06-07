# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/thangnguyen/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools-proguard.html

# Retrofit 2 rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keep @interface retrofit2.http.** { *; }

# Moshi rules
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
# Keep Moshi's generated adapters
-keep class *JsonAdapter { *; }
-keep class com.squareup.moshi.LinkedHashTreeMap { *; }

# OkHttp rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coroutines rules
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Google Play Billing rules
-keep class com.android.billingclient.api.** { *; }

# Coil rules
-keep class coil.** { *; }
-dontwarn coil.**

# Jetpack Compose rules
-keepclassmembers class * extends androidx.compose.ui.node.RootForTest { *; }

# Project specific rules
# Keep all models as they might be used for serialization (Moshi/Room/etc.)
-keep class com.jn.paxl.model.** { *; }

# DataStore rules
-keep class androidx.datastore.** { *; }

# Keep members of classes that use @Keep annotation
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
