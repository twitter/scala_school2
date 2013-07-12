package io.utils

import java.io.File
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.twitter.scaffold.Document

// Testing documentation
// http://doc.scalatest.org/1.9.1/org/scalatest/WordSpec.html
// http://doc.scalatest.org/1.9.1/org/scalatest/matchers/MustMatchers.html

class ContentSpec extends WordSpec with MustMatchers {
  
  private[this] val / = sys.props("file.separator")
  private[this] def markdownRoot() = new File(getClass().getResource(/ + "markdown").getPath)
  private[this] def allMarkdownFiles() = {
    def allFilesInDirectory(root: File, prefix: String): Seq[String] = root.listFiles() flatMap {
      case file if file.isDirectory =>
        allFilesInDirectory(file, prefix + / + file.getName)
      case file if file.getName.endsWith(".md") =>
        Seq(prefix + / + file.getName.stripSuffix(".md"))
    }
    allFilesInDirectory(markdownRoot, "")
  }

  "A resource directory" must {
    "not be empty" in {
      allMarkdownFiles() must not be 'empty
    }

    "have valid markdown resources" in {
      allMarkdownFiles() foreach { Document.render }
    }
  }  
}
