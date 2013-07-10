package com.twitter.scaffold

import scala.xml._

object DocumentSpecHelpers {
  def parse(html: String) = XML.loadString(html)

  def title(xml: Elem): String = {
    xml.child.find {
      _.label == "head"
    } flatMap {
      _.child.find(_.label == "title")
    } map {
      _.text
    } getOrElse ""
  }

  def firstHeading(xml: Elem): String = {
    xml.child.find {
      _.label == "body"
    } flatMap {
      _.child.find(_.label == "header")
    } flatMap {
      _.child.find(_.label == "div")
    } flatMap {
      _.child.find(_.label == "h1")
    } map {
      _.text
    } getOrElse ""
  }

  def lead(xml: Elem): String = {
    xml.child.find {
      _.label == "body"
    } flatMap {
      _.child.find(_.label == "header")
    } flatMap {
      _.child.find(_.label == "div")
    } flatMap {
      _.child.find(_.label == "p")
    } map {
      _.text
    } getOrElse ""
  }

  def toc(xml: Elem): List[(String, String)] = {
    xml.child.find {
      _.label == "body"
    } flatMap {
      _.child.find { n: Node =>
        n.label == "div" && n.attributes("class") != null && n.attributes("class").head.text == "span3 scaffold-sidebar"
      }
    } flatMap {
      _.child.find(_.label == "ul")
    } map {
      _.child.toList.filter(_.label == "li") map { n: Node =>
        val c = n.child.find(_.label == "a").get
        (c.text.tail, c.attributes("href").head.text)
      }
    } getOrElse List()
  }

  def sections(xml: Elem): List[(String, String, String)] = {
    xml.child.find {
      _.label == "body"
    } flatMap {
      _.child.find { n: Node =>
        n.label == "div" && n.attributes("class") != null && n.attributes("class").head.text == "container"
      }
    } map {
      _.child(1).child(1)
    } map {
      _.child.toList.filter(_.label == "section") map { n: Node =>
        val link = n.attributes("id").head.text
        val header = n.child(1).child(0).text
        val rest = n.child.tail.tail.mkString("").trim
        (link, header, rest)
      }
    } getOrElse List()
  }
}
