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
configuration changes are slow and are the differences between apps that run only on Pixel phones 
and whatnot and apps that run also in budget phones.

#### Caching - Store vs client-side header manipulation
This project implements caching by using ([Store](https://github.com/NYTimes/Store)), an abstraction
layer that sits between your Retrofit interface and your domain logic and implements the Store 
pattern with a reactive approach. It fits very nicely with reactive architectures and its 1:1: 
mapping to endpoints make it a very nice tool for low coupling, which is a factor I consider 
important.

A more traditional, some would say simpler approach, is client-side header manipulation. Something 
like ([this](https://github.com/stoyicker/template/blob/2c9b3517a31c897fe167827bad98df752dff810b/app/src/main/kotlin/app/network/NetworkClient.kt)).
However that requires modifying the very own network client for each different endpoint needs, which 
I don't quite like.

Note that image caching is not handled by Store, but Picasso instead.

#### Single&lt;List&lt;T&gt;&gt; vs Observable&lt;T&gt;, do you not understand data streams?
Well enough to know that the response from the only request this app executes is not a data stream. 
We get an ordered bunch of items, that is, a _list_ of items, but only in _one_ bunch at a time. 
Just because there are several items it doesn't mean we should use a stream-like representation - we 
are not observing for interactions with a UI element or leave an open port for incoming connections 
or similar - therefore using Observable/Flowable is conceptually incorrect. Moreover, supposing we 
used one of these classes instead, there would be a rather unnecessary overhead, because on our 
subscriber we will either queue a UI update for every element (country, in this case) that is 
received in onNext, or manually collect them and then request a single UI update, which is what is 
happening in the actual implementation, only that we don't unfold the list into an unnecessary 
Observable/Flowable and therefore we don't need to put it back together either.

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
to end up employed by Symbio, that's not the case just yet.

Feel free to bring this or any other points up for discussion at an interview if you disagree with 
my thoughts.
