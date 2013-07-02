> ### OMG spoilers
>
> This project has not yet been publicly announced. Not that there's anything particularly scary here, but it's still full of [hipster ipsum](http://hipsteripsum.me/) and isn't ready for primetime. For now: the first rule of Scala School is **you do not talk about Scala School**. Don't share links here just yet; we'll announce soon.

# Scala School 2

The goal of Scala School 2 is to provide organized, interactive, reference-quality material for learning the Scala language. It is implemented as a self-contained, locally running web server, which serves book-style material along with live, editable and runnable code snippets. This is intended both for self-paced study as well as for classroom-style lecture and group exercise.

We aim to eventually provide this as a high availability public service, hosted by Twitter, for people everywhere to learn Scala. Currently, however, this is a *very bad idea* for technical reasons: the underlying Scala interpreter is not sandboxed (see issues [#6](https://github.com/twitter/scala_school2/issues/6) and [#7](https://github.com/twitter/scala_school2/issues/7)). So, for now, please download the project and run it locally.

## Running

`sbt run` will start an HTTP server on port 8080.

## License

All original code in Scala School 2 is provided under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html), with the exception of lesson content (`src/main/resources/markdown`), provided under the [Creative Commons (CC BY 3.0)](http://creativecommons.org/licenses/by/3.0/legalcode) license.

## Shout Outs

* Mathias Doenitz ([@sirthias](https://twitter.com/sirthias)), Johannes Rudolph ([@virtualvoid](https://twitter.com/virtualvoid)) and contributors to:
  * The **[Spray](http://spray.io/)** REST/HTTP toolkit for [Akka](http://akka.io/) (Apache 2 license)
  * The **[Twirl](https://github.com/spray/twirl)** SBT plugin, providing simple stand-alone use of the [Play 2.0 template engine](http://www.playframework.com/documentation/2.0/ScalaTemplates) (Apache 2 license)
  * The **[Revolver](https://github.com/spray/sbt-revolver)** SBT plugin, to make development not suck (Apache 2 license)
  * The **[Pegdown](https://github.com/sirthias/pegdown)** [Markdown](http://daringfireball.net/projects/markdown/) processor (Apache 2 license)
* Marijn Haverbeke ([@marijnh](https://twitter.com/marijnjh)) and contributors to the **[CodeMirror](http://codemirror.net/)** text editor component (MIT license)
* Mark Otto ([@mdo](https://twitter.com/mdo)), Jacob Thornton ([@fat](https://twitter.com/fat)) and contributors to the **[Bootstrap](http://twitter.github.io/bootstrap/)** framework (Apache 2 license)
* John Resig ([@jeresig](https://twitter.com/jeresig)), Dave Methvin ([@davemethvin](https://twitter.com/davemethvin)) and contributors to the **[jQuery](http://jquery.com/)** JavaScript library (MIT license)
* Jan Kovařík ([@jankovarik](https://twitter.com/jankovarik)) for **[GLYPHICONS](http://glyphicons.com/)** (CC BY 3.0)
* Alexandar Farkas and contributors to **[html5shiv](https://github.com/aFarkas/html5shiv)** (MIT and GPL2 licensed)

## Real Talk, Special Thanks

* Marko Gargenta ([@markogargenta](https://twitter.com/MarkoGargenta)), Saša Gargenta ([@agargenta](https://twitter.com/agargenta)) and Rob Nikzad from **[Marakana](http://marakana.com/)**, the best open-source technology training company in the world, who incubated the initial portions of the Scala School 2 content for customized, on-location training.
* Marius Eriksen ([@marius](https://twitter.com/marius)), Larry Hosken ([@lahosken](https://twitter.com/lahosken)), Steve Jensen ([@stevej](https://twitter.com/stevej)), Jeff Sarnat ([@eigenvariable](https://twitter.com/eigenvariable)) and many others at Twitter for their work on the several incarnations of Scala School preceding this.
* Martin Odersky ([@odersky](https://twitter.com/odersky)), Paul Phillips ([@extempore2](https://twitter.com/extempore2)), Iulian Dragos ([@jaguarul](https://twitter.com/jaguarul)), Philipp Haller ([@philippkhaller](https://twitter.com/philippkhaller)), Adriaan Moors ([@adriaanm](https://twitter.com/adriaanm)) and contributors to the **[Scala](http://www.scala-lang.org/)** programming language.
* The many members of the greater Scala community who come together and speak at conferences every year, sharing their diverse and often conflicting viewpoints and experiences, to influence our understanding and appreciation for this powerful language. Fair warning: if we ever write a book, some of y'all are getting called out by name for your contributions.
