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

-keep class com.flurry.** { *; }
-keep class com.flurry.android.** { *; }
-keepattributes *Annotation*
#Code obfuscation
-keepclassmembers class  com.AflamTvMaroc.moviesdarija.model** { <fields>; }
-keep public class  com.AflamTvMaroc.moviesdarija.model** {
  public protected *;
}
-keep class androidx.palette.** {*;}
-keep class com.squareup.moshi.** {*;}
-keep class com.applovin.** { *; }
