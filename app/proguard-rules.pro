# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# PDFBox Android
-keep class com.tom_roush.pdfbox.** { *; }
-dontwarn com.tom_roush.pdfbox.**
-keep class com.tom_roush.pdfbox.android.** { *; }
-keep class com.tom_roush.pdfbox.resources.** { *; }
-keep class com.tom_roush.pdfbox.pdmodel.font.encoding.** { *; }

# SLF4J - Ignore missing implementation (optional logging dependency)
-dontwarn org.slf4j.**
-dontwarn org.slf4j.impl.**
-keep class org.slf4j.** { *; }

# Jsoup - Web scraping library
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**

# Readability4J - Article extraction library
-keep class net.dankito.readability4j.** { *; }
-dontwarn net.dankito.readability4j.**

# Keep Nutshell classes (updated package name)
-keep class com.nutshell.data.model.** { *; }
-keep class com.nutshell.data.local.database.** { *; }
-keep class com.nutshell.data.remote.api.** { *; }
-keep class com.nutshell.utils.** { *; }