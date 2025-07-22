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