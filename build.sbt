name := "klic"

import android.Keys._
android.Plugin.androidBuild

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
scalaVersion := "2.11.7"
scalacOptions in Compile += "-feature"

proguardCache in Android ++= Seq("org.scaloid")

proguardOptions in Android ++= Seq(
  "-dontobfuscate",
  "-dontoptimize",
  "-keepattributes Signature",
  "-printseeds target/seeds.txt",
  "-printusage target/usage.txt",
  "-dontwarn scala.collection.**", // required from Scala 2.11.4
  "-dontwarn org.scaloid.**", // this can be omitted if current Android Build target is android-16
  "-dontwarn android.test.**"
)

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "4.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

run <<= run in Android
install <<= install in Android
