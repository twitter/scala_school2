package io.utils

import java.io.File
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.twitter.scaffold.Document

// Testing documentation
// http://doc.scalatest.org/1.9/org/scalatest/WordSpec.html
// http://doc.scalatest.org/1.9/org/scalatest/matchers/MustMatchers.html

class ContentSpec extends WordSpec with MustMatchers {
  
  def allFilesInDirectory(path: File): Seq[File] = path.listFiles() flatMap { file =>
    if (file.isDirectory) allFilesInDirectory(file)
    else Seq(file)
  }

  private[this] val markdownRoot = getClass().getResource("/markdown")
  private[this] def markdownDirectory = new File(markdownRoot.getPath())
  "A resource directory" must {
    "exist" in {
      markdownRoot must not be (null)
    }

    "not be empty" in {
      allFilesInDirectory(markdownDirectory) filter {_.getName.endsWith(".md")} must not have length (0)
    }

    "have valid markdown resources" in {
      allFilesInDirectory(markdownDirectory) collect {
        case file if file.getName.endsWith(".md") => 
          val fileName = file.getPath().stripPrefix(markdownRoot.getPath()).stripSuffix(".md")
          Document.render(fileName)
      }
    }
  }  
}
