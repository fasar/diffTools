package fsart.diffTools.model

/**
 *
 * User: fabien
 * Date: 04/05/12
 * Time: 09:30
 *
 */

abstract class CsvData[E] {
  def headers:List[E];

  def array:List[List[E]];

  def separator:String;

  //lazy because val are initialised before abstract field are herited from inner class
  lazy val getKeys: List[E] = {
    (for (line <- array)
    yield {
      line(0)
    }).toList
  }


    // get duplicated key and number of there apparition
  def getDuplicatedKeys: Map[E, Int] = {
    val res =
      (getKeys.collect {
        case key if (getKeys.count {
          _ == key
        } > 1) =>
          key
      }).toList

    var mapRes = Map.empty[E, Int]
    for (key <- res) {
      mapRes += (key -> (mapRes.getOrElse(key, 0) + 1))
    }
    mapRes
  }

    // get duplicated lines and number of there apparition
  def getKeysOfDuplicatedLines: Map[E, Int] = {
    val res =
      (array.collect {
        case line
          if (array.count {
            x => x.toList == line.toList
          } > 1) =>
          line
      }).toList

    var mapRes = Map.empty[E, Int]
    for (lineArr <- res) {
      mapRes += (lineArr(0) -> (mapRes.getOrElse(lineArr(0), 0) + 1))
    }
    mapRes
  }

  def getLinesOfKey(key: E): List[List[E]] = {
    array.collect {
      case lineF2 if (lineF2(0) == key) => lineF2
    }
  }


}
