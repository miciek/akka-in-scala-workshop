# Akka in Scala workshop

## Abstract
The objective of the workshop is to get some theoretical and practical overview of the actor model and asynchronous programming. You will learn how to program real applications using [Akka](http://akka.io/) and [Scala](https://www.scala-lang.org/). Throughout the day we will switch between quick introductions of the core features and longer step-by-step exercises.

## Table of Contents
  * Scala features
  * Actor model
  * Actors in Akka
  * Actor-based applications
  * Akka internals
  * Testing actors
  * Supervision strategy and fault tolerance
  * *(optionally)* [Reactive Manifesto](http://www.reactivemanifesto.org/) and microservices
  * *(optionally)* Overview of more advanced components and features  (persistence and remoting)

## Prerequisites
  * Familiarity with Scala syntax. See weeks 1 & 2 of [Functional Programming Principles in Scala](https://www.coursera.org/course/progfun).
  * No prior Akka experience is required.

## Workshop details
We are going to implement *"Movie: Hot Or Not?"* application which is a comparison-based rating system. The sample output of the application:

```
 > The Transporter (a) or The Missing Person (b) or ? (q to quit)
You chose The Transporter
 > The Transporter (a) or Malcolm X (b) or ? (q to quit)
a
You chose The Transporter
 > The Transporter (a) or The Jungle Book 2 (b) or ? (q to quit)
?
You chose nothing
 > The Transporter (a) or The Matrix (b) or ? (q to quit)
b
You chose The Matrix
```

### Scala features
  * Java `List` vs Scala `List`
  * Immutable collections
  * `Option`
  * Exceptions vs `Try` 
  * Tail recursion
  * `StdIn`
  * Pattern matching
  * `Random`
  
Exercise: implement [HotOrNotApp.scala](src/main/scala/com/michalplachta/workshop/akka/movies/HotOrNotApp.scala)
  
### Actor model
  * Asynchronous programming
  * Actors as entities with state
  * Events and messages
  
Exercise: draw model of asynchronous version of `HotOrNotApp`
  
### Actors in Akka
  * `Actor` class
  * `receive` method
  * Protocol in companion object
  * Sending messages to other actors using `!`
  * `sender`
  * Holding mutable state using `var`
  * Creating actors using `actorOf` and `Props`
  * `preStart`
  
Exercise: implement [MovieMetadataParser.scala](src/main/scala/com/michalplachta/workshop/akka/movies/MovieMetadataParser.scala) and [SessionHost.scala](src/main/scala/com/michalplachta/workshop/akka/movies/SessionHost.scala)

### Actor-based applications
  * `ActorSystem`
  * Asking other actors using `?` (`akka.pattern.ask`)
  * `Timeout` and `Duration`
  * `Future`s in Scala
  * `ExecutionContext`

Exercise: implement [HotOrNotAsyncApp.scala](src/main/scala/com/michalplachta/workshop/akka/movies/HotOrNotAsyncApp.scala)

### Akka internals
  * `ActorLogging` and logging thread names
  * Thread analysis using `jvisualvm`
  * Dispatchers in Akka
  * Naming actors
  * `PoisonPill`
  * Worker actors using `RoundRobinPool`
  
Exercise: fix [HotOrNotAsyncApp.scala](src/main/scala/com/michalplachta/workshop/akka/movies/HotOrNotAsyncApp.scala), [MovieMetadataParser.scala](src/main/scala/com/michalplachta/workshop/akka/movies/MovieMetadataParser.scala) and [SessionHost.scala](src/main/scala/com/michalplachta/workshop/akka/movies/SessionHost.scala)
  
### Testing actors
  * Unit testing using `scalatest`
  * `WordSpec` flavor 
  * `Matchers`
  * Asynchronous integration testing using `TestKit` and `scalatest`
  * `WordSpecLike`
  * `BeforeAndAfterAll`
  * Asserting using `expectMsg` and `ImplicitSender`
  * Asserting using `expectMsgPF`
  * Making the code testable using parametrized `Props`
  
 Exercise: implement [MovieSpec.scala](test/main/scala/com/michalplachta/workshop/akka/movies/MovieSpec.scala), [MovieMetadataParserSpec.scala](test/main/scala/com/michalplachta/workshop/akka/movies/MovieMetadataParserSpec.scala) and [SessionHostSpec.scala](test/main/scala/com/michalplachta/workshop/akka/movies/SessionHostSpec.scala)

## Running the application
Before launching the application you need to generate `csv` files. Please run [generate_movie_data.sh](generate_movie_data.sh) script first. Then run the app by executing `sbt run`.

## Acknowledgments
This project uses data from [IMDB 5000 Movie Dataset](https://www.kaggle.com/deepmatrix/imdb-5000-movie-dataset).
