/**
 * Copyright Fabien Sartor 
 * Contributors: Fabien Sartor (fabien.sartor@gmail.com)
 *  
 * This software is a computer program whose purpose to compate two 
 * files.
 *
 */
/**
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
 */
package fsart.diffTools.model

/**
 *
 * User: fabien
 * Date: 04/05/12
 * Time: 09:30
 *
 */

abstract class CsvData[E] {
  def headers:List[String];

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
