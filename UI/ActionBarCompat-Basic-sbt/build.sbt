import android.Keys._

android.Plugin.androidBuild

name := "ActionBarCompat-Basic"

version := "0.1"

scalaVersion := "2.11.4"

platformTarget in Android := "android-21"

minSdkVersion in Android := "7"

targetSdkVersion in Android := "21"

libraryDependencies ++= Seq(
  "com.android.support" % "support-v4" % "21.0.2",
  "com.android.support" % "gridlayout-v7" % "21.0.2",
  "com.android.support" % "cardview-v7" % "21.0.2",
  "com.android.support" % "appcompat-v7" % "21.0.2"
)

proguardOptions in Android ++= Seq(
  "-dontobfuscate",
  "-dontoptimize",
  "-keepattributes Signature",
  "-printseeds target/seeds.txt",
  "-printusage target/usage.txt",
  "-dontwarn scala.collection.**"
)

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android
