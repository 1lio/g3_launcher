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