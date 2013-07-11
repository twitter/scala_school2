package io.utils

import java.io.File
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import com.twitter.scaffold.Document

// Testing documentation
// http://doc.scalatest.org/1.8/org/scalatest/WordSpec.html
// http://doc.scalatest.org/1.8/org/scalatest/matchers/MustMatchers.html

class ContentSpec extends WordSpec with MustMatchers {
  
  def allFilesInDirectory(path: File): Seq[File] = path.listFiles() flatMap { file =>
    if (file.isDirectory) allFilesInDirectory(file)
    else Seq(file)
  }

  private[this] val markdownDirectory = "/markdown"
  private[this] val markdownRoute = getClass().getResource(markdownDirectory)
  "A resource directory" must {
    "exist" in {
      markdownRoute must not be (null)
    }

    "not be empty" in {
      val mainFile = new File(markdownRoute.getPath())
      allFilesInDirectory(mainFile) filter {_.getName.endsWith(".md")} must not have length (0)
    }

    "have valid markdown resources" in {
      val mainFile = new File(getClass().getResource(markdownDirectory).getPath())
      allFilesInDirectory(mainFile) collect {
        case file if file.getName.endsWith(".md") => 
          val fileName = file.getPath().drop(getClass().getResource(/ + "markdown" + /).getPath().length).dropRight(3)
          Document.render(fileName)
      }
    }
  }  
}
