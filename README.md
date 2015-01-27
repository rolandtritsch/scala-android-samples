# A Scala implementation of [the Android UI samples]()

The main features of this set of source code are ...

* ported to Scala (but ... I did not go all-in on the Scala (e.g. using Macroid, Scaloid or Akka). Instead I tried to stay close to the original Java structure to make it easier for people to understand what is going on).
* using the [gradle-android-scala-plugin](https://github.com/saturday06/gradle-android-scala-plugin) (again ... I did not port it to sbt (besides ActionBarCompat-Basic) to make it easier for people to assimilate the port)
* NOTE: I am also using an experimental snapshot of [gradle-ensime](https://github.com/rolandtritsch/gradle-ensime) to generate the .ensime file. You probably want to remove that plugin.
* configured a lint.xml file to get rid of all the lint warnings (and the lint error on the scala lib).

To make it work you need to ...

* install git and sbt (using macports or brew)
* clone the repo
* create an avd and start an emulator (needs to be the only device that is connected and needs to be api level 21)
* cd into the sub-directory of your choice
* run `gradle runDebug` (might take a while the first time around)

... and you should be in business.
