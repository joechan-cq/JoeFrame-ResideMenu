# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in G:\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-libraryjars libs/android-async-http-1.4.8.jar
-keep class com.loopj.android.http.* {*;}

-libraryjars libs/nineoldandroids-library-2.4.0.jar
-keep class com.nineoldandroids.* {*;}

-libraryjars libs/androideventbus-1.0.4.jar
-keep class org.simple.eventbus.* {*;}