package com.twitter.scaffold.markdown

import org.pegdown._
import ast._

class TextRenderer extends Visitor {
  private[this] val printer = new Printer

  def toText(nodes: Seq[Node]): String = {
    nodes foreach { _.accept(this) }
    printer.getString
  }

  def visit(node: AbbreviationNode): Unit = visitChildren(node)
  def visit(node: AutoLinkNode): Unit = visit(node: TextNode)
  def visit(node: BlockQuoteNode): Unit = visitChildren(node)
  def visit(node: BulletListNode): Unit = visitChildren(node)
  def visit(node: CodeNode): Unit = visit(node: TextNode)
  def visit(node: DefinitionListNode): Unit = visitChildren(node)
  def visit(node: DefinitionNode): Unit = visitChildren(node)
  def visit(node: DefinitionTermNode): Unit = visitChildren(node)
  def visit(node: ExpImageNode): Unit = visitChildren(node)
  def visit(node: ExpLinkNode): Unit = visitChildren(node)
  def visit(node: HeaderNode): Unit = visitChildren(node)
  def visit(node: HtmlBlockNode): Unit = visit(node: TextNode)
  def visit(node: InlineHtmlNode): Unit = visit(node: TextNode)
  def visit(node: ListItemNode): Unit = visitChildren(node)
  def visit(node: MailLinkNode): Unit = visit(node: TextNode)
  def visit(node: OrderedListNode): Unit = visitChildren(node)
  def visit(node: ParaNode): Unit = visitChildren(node)
  def visit(node: QuotedNode): Unit = visitChildren(node)
  def visit(node: ReferenceNode): Unit = visitChildren(node)
  def visit(node: RefImageNode): Unit = visitChildren(node)
  def visit(node: RefLinkNode): Unit = visitChildren(node)
  def visit(node: RootNode): Unit = visitChildren(node)
  def visit(node: SimpleNode): Unit = printer print {
    import SimpleNode.Type._
    node.getType match {
      case Apostrophe => "'"
      case Ellipsis   => "..."
      case Emdash     => "---"
      case Endash     => "--"
      case _          => ""
    }
  }
  def visit(node: SpecialTextNode): Unit = visit(node: TextNode)
  def visit(node: StrongEmphSuperNode): Unit = visitChildren(node)
  def visit(node: TableBodyNode): Unit = visitChildren(node)
  def visit(node: TableCaptionNode): Unit = visitChildren(node)
  def visit(node: TableCellNode): Unit = visitChildren(node)
  def visit(node: TableColumnNode): Unit = visitChildren(node)
  def visit(node: TableHeaderNode): Unit = visitChildren(node)
  def visit(node: TableNode): Unit = visitChildren(node)
  def visit(node: TableRowNode): Unit = visitChildren(node)
  def visit(node: VerbatimNode): Unit = visit(node: TextNode)
  def visit(node: WikiLinkNode): Unit = visit(node: TextNode)

  def visit(node: TextNode): Unit = printer print node.getText
  def visit(node: SuperNode): Unit = visitChildren(node)
  def visit(node: Node): Unit = {}

  private def visitChildren(node: SuperNode): Unit = {
    import collection.JavaConverters.asScalaBufferConverter
    node.getChildren.asScala foreach { _.accept(this) }
  }
}
