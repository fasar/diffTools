/****************************************************************************
 * Copyright Fabien Sartor 
 * Contributors: Fabien Sartor (fabien.sartor@gmail.com)
 *               http://fasar.fr
 *  
 * This software is a computer program whose purpose to compute differences 
 * between two files.
 *
 ****************************************************************************
 *
 *  This software is governed by the CeCILL license under French law and
 *  abiding by the rules of distribution of free software.  You can  use, 
 *  modify and/ or redistribute the software under the terms of the CeCILL
 *  license as circulated by CEA, CNRS and INRIA at the following URL: 
 *  "http://www.cecill.info". 
 *  
 *  As a counterpart to the access to the source code and  rights to copy,
 *  modify and redistribute granted by the license, users are provided only
 *  with a limited warranty  and the software's author,  the holder of the
 *  economic rights,  and the successive licensors  have only  limited
 *  liability. 
 *  
 *  In this respect, the user's attention is drawn to the risks associated
 *  with loading,  using,  modifying and/or developing or reproducing the
 *  software by the user in light of its specific status of free software,
 *  that may mean  that it is complicated to manipulate,  and  that  also
 *  therefore means  that it is reserved for developers  and  experienced
 *  professionals having in-depth computer knowledge. Users are therefore
 *  encouraged to load and test the software's suitability as regards their
 *  requirements in conditions enabling the security of their systems and/or 
 *  data to be ensured and,  more generally, to use and operate it in the 
 *  same conditions as regards security. 
 *  
 *  The fact that you are presently reading this means that you have had
 *  knowledge of the CeCILL license and that you accept its terms. 
 *
 ****************************************************************************
 */

package fsart.diffTools.csvModel


trait CsvData[E] {

  def headers:List[String];

  def array:List[List[E]];

  def separator:String;


  def getKeys: List[CsvKey[E]]

  // get duplicated key and number of there apparition
  def getDuplicatedKeys_map: Map[CsvKey[E], Int]

  def getDuplicatedKeys: CsvData[E]

  // get duplicated lines and number of there apparition
  def getKeysOfDuplicatedLines_map: Map[CsvKey[E], Int]


  // get duplicated lines and number of there apparition
  def getDuplicatedLines: CsvData[E]

  def getLinesOfKey(key: CsvKey[E]): List[List[E]]

  def isEmpty = (array.size == 0 || array(0).size==0)

}





case class CsvDataImpl[E](val array:List[List[E]], val headers:List[String], val separator:String = ";") extends CsvData[E] {

  //lazy because val are initialised before abstract field inherits from inner class
  override lazy val getKeys: List[CsvKey[E]] = {
    (for (line <- array)
    yield {
      CsvKeySimple.getKeyOfLine(line)
    }).toList
  }


  // get duplicated key and number of there apparition
  override def getDuplicatedKeys_map: Map[CsvKey[E], Int] = {
    val res =
      (getKeys.collect {
        case key if (getKeys.count {
          _ == key
        } > 1) =>
          key
      }).toList

    var mapRes = Map.empty[CsvKey[E], Int]
    for (key <- res) {
      mapRes += (key -> (mapRes.getOrElse(key, 0) + 1))
    }
    mapRes
  }

  override def getDuplicatedKeys: CsvData[E] = {
    val dupKeys =
      (getKeys.collect {
        case key if (getKeys.count {
          _ == key
        } > 1) =>
          getLinesOfKey(key)
      }).toList.flatten.distinct

    CsvDataImpl[E](dupKeys, this.headers, this.separator)
  }

    // get duplicated lines and number of there apparition
  override def getKeysOfDuplicatedLines_map: Map[CsvKey[E], Int] = {
    val res =
      (array.collect {
        case line
          if (array.count {
            x => x.toList == line.toList
          } > 1) =>
          line
      }).toList

    var mapRes = Map.empty[CsvKey[E], Int]
    for (lineArr <- res) {
      mapRes += (CsvKeySimple.getKeyOfLine(lineArr)
        -> (mapRes.getOrElse(CsvKeySimple.getKeyOfLine(lineArr), 0) + 1))
    }
    mapRes
  }


    // get duplicated lines and number of there apparition
  //Todo: refactor val arr = ... I think it can be supp as dupLine have same values inside
  override def getDuplicatedLines: CsvData[E] = {
    val dupLine =
      (array.collect {
        case line if (array.count { _ == line } > 1) =>
          line
      }).toList

    val arr = for(line <- this.array if(dupLine.contains(line)) ) yield {
      line
    }
    CsvDataImpl[E](arr, this.headers, this.separator)
  }

  override def getLinesOfKey(key: CsvKey[E]): List[List[E]] = {
    array.collect {
      case lineF2 if (key == CsvKeySimple.getKeyOfLine(lineF2)) => lineF2
    }
  }
}


case class CsvKeySimple[E](key:E) extends CsvKey[E]
object CsvKeySimple {

  def getKeyOfLine[E](line:List[E]):CsvKey[E] = {
    CsvKeySimple(line(0))
  }


}
