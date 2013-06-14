package com.twitter.scaffold.markdown

import org.pegdown._
import ast._

class HtmlRenderer extends ToHtmlSerializer(new LinkRenderer) {
  def toHtml(nodes: Seq[Node]): String = {
    nodes foreach { _.accept(this) }
    printer.getString
  }

  override def visit(node: BlockQuoteNode): Unit = {
    printIndentedTag(node, "div", "class" -> "alert alert-block alert-info")
  }

  override def visit(node: VerbatimNode): Unit = {
    printTag(node, "textarea")
  }

  private def printIndentedTag(node: SuperNode, tag: String, attributes: (String, String)*) = {
    printer.println().print('<').print(tag)
    for ((name, value) <- attributes)
      printer.print(' ').print(name).print('=').print('"').print(value).print('"')
    printer.print('>').indent(+2)

    visitChildren(node)
    printer.indent(-2).println().print('<').print('/').print(tag).print('>')
  }
}
