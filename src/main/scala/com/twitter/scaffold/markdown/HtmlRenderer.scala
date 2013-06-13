package com.twitter.scaffold.markdown

import org.pegdown._
import ast._

class HtmlRenderer extends ToHtmlSerializer(new LinkRenderer) {
  def toHtml(nodes: Seq[Node]): String = {
    nodes foreach { _.accept(this) }
    printer.getString
  }

  override def visit(node: VerbatimNode): Unit = {
    printTag(node, "textarea")
  }
}
