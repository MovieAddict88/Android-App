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

# Keep Conscrypt classes
-keep class org.conscrypt.** { *; }

# Ignore missing classes for older platforms (pre-Lollipop)
-dontwarn com.android.org.conscrypt.**
-dontwarn org.apache.harmony.xnet.provider.jsse.**
# Enable alphabetic-only obfuscation
-classobfuscationdictionary dictionary.txt
-obfuscationdictionary dictionary.txt

# Preserve necessary attributes
-keepattributes SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature,Annotation*
-renamesourcefileattribute SourceFile

# Keep Android components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Volley classes
-keep class com.android.volley.** { *; }
-keep class org.apache.http.** { *; }

# Keep Picasso classes
-keep class com.squareup.picasso.** { *; }

# Keep Google Ads classes
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }

# Keep FancyAlertDialog classes
-keep class com.github.shashank02051997.** { *; }

# Keep View binding
-keepclassmembers class * {
    @androidx.viewbinding.BindView *;
}

# Keep Parcelables
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# Keep Kotlin metadata
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class kotlin.** { *; }