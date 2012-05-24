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


trait CsvDataSpecialKey[E] extends CsvData[E] {

  def semantics: CsvData[E]

  def keysCol: List[Int]

  def headers:List[String] = semantics.headers

  def separator:String = semantics.separator

  def array = semantics.array


    //lazy because otherwise, val are initialised before abstract field inherits from inner class
  override lazy val getKeys: List[CsvKey[E]] = {
    (for (line <- array)
    yield {
      CsvKeySpecial.getKeyOfLine(line, keysCol)
    }).toList
  }

  override def getLinesOfKey(key: CsvKey[E]): List[List[E]] = {
    array.foreach{ case elem =>
      val key2 = CsvKeySpecial.getKeyOfLine(elem, keysCol)
    }
    array.collect {
      case lineF2 if (key == CsvKeySpecial.getKeyOfLine(lineF2, keysCol) ) => lineF2
    }
  }

  def getDuplicatedKeys_map = semantics.getDuplicatedKeys_map
  def getDuplicatedKeys = semantics.getDuplicatedKeys
  def getKeysOfDuplicatedLines_map = semantics.getKeysOfDuplicatedLines_map
  def getDuplicatedLines = semantics.getDuplicatedLines
}

case class CsvDataSpecialKeyImpl[E](val semantics: CsvData[E], val keysCol: List[Int]) extends CsvDataSpecialKey[E] {
  {
    if(!semantics.isEmpty) {
      val sizeLine = semantics.array(0).size
      val keysColCantReach = keysCol.collect{ case keyCol if sizeLine <= keyCol => keyCol }
      if(keysColCantReach.size > 0) {
          throw new CsvDataColNumberException("Key(s) col " + keysColCantReach.mkString(",") + " can't be reached")
      }
    }
  }
}


case class CsvKeySpecial[E](key:List[E], keysCol: List[Int]) extends CsvKey[E] {

  override def equals(obj: Any) =  {
    obj match {
      case that:CsvKeySpecial[E] if that.isInstanceOf[CsvKeySpecial[E]] =>
        this.key == that.key
      case _ => false
    }
  }
}

object CsvKeySpecial {
  def getKeyOfLine[E](line:List[E], keysCol: List[Int]):CsvKey[E] = {
    val keys = (for(keyCol <- keysCol)
      yield {
        line(keyCol)
      }).toList
    CsvKeySpecial(keys, keysCol)
  }
}


