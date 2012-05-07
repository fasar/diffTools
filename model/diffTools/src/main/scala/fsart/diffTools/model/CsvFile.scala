package fsart.diffTools.model

import org.apache.commons.logging.{LogFactory, Log}
import scala.collection.JavaConversions._

/**
 *
 * User: fabien
 * Date: 23/04/12
 * Time: 19:19
 *
 */

class CsvFile(val lines: List[String], val firstLineIsHeader: Boolean = false, val sep: String = ";")  {

  private val log: Log = LogFactory.getLog(this.getClass)

  val headers =
    if (firstLineIsHeader && lines.size > 0) {
      lines(0).split(sep).toList
    } else {
      Nil
    }

  val array: List[List[String]] = {
    val linesToCompute =
      if (firstLineIsHeader && lines.size > 0) {
        lines.drop(1)
      } else {
        lines
      }
    val res =
      (for (line <- linesToCompute) yield {
        line.split(sep).toList
      }).toList
    val nbMaxCol = res.foldLeft(0) {
      (nbCol, elem) => scala.math.max(nbCol, elem.size)
    }
    res.map {
      elem => elem ++ List.fill[String](nbMaxCol - elem.size) {
        ""
      }
    }
  }

  // Test if lines is a good arg
  if (array.size > 0) {
    array.zipWithIndex.foldLeft(array(0).size) {
      (prevSize, elemZipped) =>
        val elem = elemZipped._1
        val lineNumber = elemZipped._2
        if (prevSize < elem.size) {
          throw new CsvFileNotGoodException("params doesn't have the same numbers of columns at the line " + (lineNumber + 1))
        }
        elem.size
    }
  }

  val getKeys: List[String] = {
    (for (line <- array)
    yield {
      line(0)
    }).toList
  }

  // get duplicated key and number of there apparition
  def getDuplicatedKeys: Map[String, Int] = {
    val res =
      (getKeys.collect {
        case key if (getKeys.count {
          _ == key
        } > 1) =>
          key
      }).toList.sorted

    var mapRes = Map.empty[String, Int]
    for (key <- res) {
      mapRes += (key -> (mapRes.getOrElse(key, 0) + 1))
    }
    mapRes
  }


  // get duplicated lines and number of there apparition
  def getKeysOfDuplicatedLines: Map[String, Int] = {
    val res =
      (array.collect {
        case line
          if (array.count {
            x => x.toList == line.toList
          } > 1) =>
          line
      }).toList

    var mapRes = Map.empty[String, Int]
    for (lineArr <- res) {
      mapRes += (lineArr(0) -> (mapRes.getOrElse(lineArr(0), 0) + 1))
    }
    mapRes
  }

  def getLinesOfKey(key: String): List[List[String]] = {
    array.collect {
      case lineF2 if (lineF2(0) == key) => lineF2
    }
  }
}
