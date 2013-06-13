package com.twitter.scaffold

import twirl.api._
import org.pegdown._
import ast._

case class Document(title: HeaderNode, lead: Seq[Node], sections: Seq[(HeaderNode, Seq[Node])])

object Document {

  def render(nodes: Seq[Node]): String = (new Renderer).render(nodes)

  def render(name: String): Option[Html] = for {
    text     <- load(name)
    document <- parse(text)
  } yield html.markdown(document)

  private class Renderer extends ToHtmlSerializer(new LinkRenderer) {
    def render(nodes: Seq[Node]): String = {
      nodes foreach { _.accept(this) }
      printer.getString
    }

    override def visit(node: CodeNode): Unit = {
      printTag(node, "textarea")
    }
  }

  private[this] val / = sys.props("file.separator")
  private def load(name: String): Option[Array[Char]] =
    getClass.getResourceAsStream(/ + "markdown" + / + name + ".md") match {
      case null   => None
      case stream => Some(io.Source.fromInputStream(stream).toArray)
    }

  private def parse(text: Array[Char]): Option[Document] = {
    import collection.JavaConverters.asScalaBufferConverter
    import collection.mutable.Buffer

    val root = new PegDownProcessor(Extensions.SMARTYPANTS).parseMarkdown(text)
    val sections = Buffer.empty[(HeaderNode, Buffer[Node])]

    root.getChildren.asScala foreach {
      case node: HeaderNode if node.getLevel < 3 =>
        sections += node -> Buffer.empty[Node]
      case node =>
        sections.lastOption match {
          case Some((_, buf)) => buf += node
          case None =>
        }
    }

    sections match {
      case (title, lead) +: tail => Some(Document(title, lead, tail))
      case _                     => None
    }
  }
}
