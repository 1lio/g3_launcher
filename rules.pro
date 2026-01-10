# Правила для Compose Desktop
-keep class com.g3.launcher.ui.MainKt { *; }

# Сериализация
-keep class kotlinx.serialization.** { *; }

# Keep Compose entry points and reflection
-keep class androidx.compose.** { *; }
-keep class org.jetbrains.skiko.** { *; }  # For Skia rendering
-keep class org.jetbrains.skia.** { *; }

# For kotlinx-serialization-json
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # required by json converter.
-keep,includedescriptorclasses class com.g3.launcher.**$$serializer { *; } # Replace with your package
-keepclassmembers class com.g3.launcher.** {
    *** Companion;
    *** $state_instance;
    *** $result_instance;
}
-keepclasseswithmembers class com.g3.launcher.** {
    <init>();
    kotlinx.serialization.KSerializer serializer(...);
}

# Coroutines (if used)
-keep class kotlinx.coroutines.** { *; }

# Ignore warnings from compile-only deps
-dontwarn **

# Preserve signatures if needed (broad; use sparingly as it limits shrinking)
-keepattributes Signature

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-keep class org.ocpsoft.prettytime.i18n**

-dontnote okhttp3.**
-keep class okio.Okio__JvmOkioKt { *; }

-keepnames class com.g3.launcher.model.** { *; }
-keepnames class com.g3.launcher.entity.** { *; }