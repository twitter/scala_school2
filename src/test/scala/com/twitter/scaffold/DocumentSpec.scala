package com.twitter.scaffold

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.twitter.scaffold.Document
import DocumentSpecHelpers._

class DocumentSpec extends WordSpec with MustMatchers {
  def render(name: String) = Document.render(name)
  def getHtml(name: String): String = render(name).get.toString

  "Document.render" should {
    "return None for non-existent data file" in {
      render("not_a_file") must equal (None)
    }

    "return None for empty data file" in {
      render("empty") must equal (None)
    }

    "return None for a file not starting with a heading" in {
      render("no_heading") must equal (None)
    }
  }

  "Document.render" can {
    "render a file with only a heading" in {
      val xml = parse(getHtml("only_heading"))
      title(xml) must equal ("Scala School 2 - only heading")
      firstHeading(xml) must equal ("only heading")
      lead(xml) must equal ("")
    }

    "render a heading and lead" in {
      val xml = parse(getHtml("heading_with_lead"))
      title(xml) must equal ("Scala School 2 - the heading")
      firstHeading(xml) must equal ("the heading")
      lead(xml) must equal ("And the lead")
    }

    "render multiple headings" in {
      val xml = parse(getHtml("multiple_headings"))
      title(xml) must equal ("Scala School 2 - first/title")
      firstHeading(xml) must equal ("first/title")
      lead(xml) must equal ("LEAD")
      toc(xml) must equal (
        List(
          ("section 1", "#section-1"),
          ("Sec B", "#sec-b"),
          ("Part III", "#part-iii")
        )
      )
      sections(xml) must equal (
        List(
          ("section-1", "section 1", "<p>Body1</p>"),
          ("sec-b", "Sec B", "<p>Second body</p>"),
          ("part-iii", "Part III", "<p>A section<br/>be here</p>")
        )
      )
    }

    "render code segments" in {
      val xml = parse(getHtml("with_code"))
      toc(xml).head must equal (("code section", "#code-section"))
      sections(xml).head must equal (("code-section", "code section", "<p>Code preface</p><textarea>var actual_code = 0\n</textarea>"))
    }
  }
}
