package io.utils

import java.io.File
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.pegdown._
import com.twitter.scaffold.Document

// Testing documentation
// http://doc.scalatest.org/1.8/org/scalatest/WordSpec.html
// http://doc.scalatest.org/1.8/org/scalatest/matchers/MustMatchers.html

class ContentSpec extends WordSpec with MustMatchers {
  
  def allDirectories(path: File): List[File] = path :: path.listFiles.filter {
    _.isDirectory
  }.toList.flatMap {
    allDirectories(_)
  }

  def allFilesInDirectories(directories: List[File]) = directories flatMap {
    oneFile => oneFile.listFiles.filter { !_.isDirectory }
  }

  private[this] val / = sys.props("file.separator")
  private[this] val markdownDirectory = / + "markdown" + /
  "All content" must {
    "have a directory available" in {
      getClass().getResource(/ + "markdown" + /) must not be (null)
    }

    "be parsable by the markdown parser" in {
      val mainFile = new File(getClass().getResource(/ + "markdown" + /).getPath())
      allFilesInDirectories(allDirectories(mainFile)).filter {
        _.getName.endsWith(".md")
      } map {
        _.getPath().drop(getClass().getResource(/ + "markdown" + /).getPath().length).dropRight(3)
      } map {
        Document.render(_)
      } 
    }
  }
  
}