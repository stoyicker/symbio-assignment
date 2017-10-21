# symbio-assignment - Jorge Antonio Diaz-Benito Soriano

[![Build Status](https://travis-ci.org/stoyicker/symbio-assignment.svg?branch=master)](https://travis-ci.org/stoyicker/symbio-assignment)

This project is developed on top of my master-slave demo for the New York Times reactive cache 
framework for Android ([Store](https://github.com/NYTimes/Store)), [stoyicker/master-slave-clean-store](github.com/stoyicker/master-slave-clean-store).

## Build instructions
`./gradlew assemble` (or something like `gradlew.bat assemble` on Windows I guess).
You can also get an APK from the [Releases](https://github.com/stoyicker/master-slave-clean-store/releases) 
tab, courtesy of Travis. 

## Architecture
This is a reactive app: it runs by reacting to user interactions. Here
is how:

![Architecture](Diagram1.png)

which reads as:

> The user interacts with the content view of an activity, which delegates to coordinators, which are 
implementations of functionalities. These trigger actions on the domain layer whose implementations 
reside in the data layer - usually consisting of retrieving data from somewhere (like the cloud), 
mapping it to domain-defined business entities and moving them back up to the coordinator, which 
updates the view accordingly via an interface.

## Language choice
I chose Kotlin over Java because:
* It is less verbose than Java.
* It is more natural both to read and write, which makes writing code easier and faster while still 
allowing Java developers who have never seen it to understand it.
* It can be configured to generate Java 6/8 bytecode, which means its evolution is independent from that of the platform.
* It is [officially supported by Google as a first-class language for Android](https://blog.jetbrains.com/kotlin/2017/05/kotlin-on-android-now-official/).

## Documentation
Documentation is generated using [Dokka](https://github.com/Kotlin/dokka), which is the
code documentation generation tool for Kotlin, similar to what Javadoc is for Java.
`index.html` for the documentation of each module can be found in their `build` directories:
 `module_name/build/dokka/module_name/index.html`.

## Tests
Unit and integration tests are written using [Spek](https://spekframework.org), the specification
framework for Kotlin (just some syntactic sugar for JUnit really). Run them with the `test` Gradle 
task in each module.
Instrumentation tests are only present in the `app` module and can be run using the `cAT` task.

## Decisions decisions (you probably want to read the relevant parts of the code first)

#### Splash screen - Why didn't you use ApplicationLifecycleCallback to perform a runtime theme change in the main activity instead of using a separate one?
1. I'm using the splash activity to perform prefetch. I could do this on the main activity too of 
course, but in detriment of higher coupling.
2. Even if coupling wasn't a problem (suppose I wasn't doing anything in the splash), runtime 
configuration changes are slow and are the differences between apps that run only on Pixel and whatnot 
and apps that run also in budget phones.

#### When transitioning from master to slave, you're sending the entire item instead of an id
I am indeed. I know it is recommended that you pass only the id and then use this to retrieve the id 
from the database as this is faster than Parcel-induced marshalling and demarshalling for objects 
that are not hierarchically complex, like the ones in this project. However, I haven't spent the 
time setting up a database.

#### A requirement was to show the country location on some sort of map-like view. Where is it?
I left this out on purpose. I don't usually do assignments as companies agree that my very complete 
GitHub profile shows my technical capabilities and as such they aren't necessary, but I can accept 
doing a standard one as I've heard from a friend that this is a good company. However, requiring 
use of a concrete component is no standard and, most importantly, doesn't help this assessment in 
any way. Therefore, although I'll be happy to help writing code for any specific components were I 
to end up employed by Symbio, now it is not the time to make such a demand.

Feel free to bring this or any other points up for discussion at an interview if you disagree with 
my thoughts.
