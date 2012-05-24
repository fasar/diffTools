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

import collection.immutable.Queue

/**
 *
 * User: fabien
 * Date: 23/05/12
 * Time: 14:24
 *
 */

abstract class CsvDataSorted[E <% Ordered[E]] extends CsvData[E] {

  val semantics: CsvData[E]

  val sortCol:List[Int]

  def headers:List[String] = semantics.headers

  def separator:String = semantics.separator

  override def array = {
    sortList(semantics.array, sortCol)
  }

  private def sortList[E <% Ordered[E]](array:List[List[E]], sortCol:List[Int]): List[List[E]] = {
    array.sortWith(
      (elem1:List[E], elem2:List[E]) =>
        sortCol.foldRight(true) {
          (numElem, ret) =>
            if(ret == false) ret else { elem1(numElem) <=  elem2(numElem) }
        }
    )
  }


  def getKeys = semantics.getKeys
  def getDuplicatedKeys_map = semantics.getDuplicatedKeys_map
  def getDuplicatedKeys = semantics.getDuplicatedKeys
  def getKeysOfDuplicatedLines_map = semantics.getKeysOfDuplicatedLines_map
  def getDuplicatedLines = semantics.getDuplicatedLines
  def getLinesOfKey(key: CsvKey[E]) = semantics.getLinesOfKey(key)
}


case class CsvDataSortedImpl[E](val semantics: CsvData[E], val sortCol:List[Int])
                               (implicit orderer: E => Ordered[E])
  extends CsvDataSorted[E] {

  {
    if(!semantics.isEmpty) {
      val sizeLine = semantics.array(0).size
      val colIdCantReach = sortCol.collect{ case colId if colId >= sizeLine => colId }
      if(colIdCantReach.size > 0) {
        throw new CsvDataColNumberException("Col id(s) " + colIdCantReach.mkString(",") + " can't be reached to sort")
      }
    }
  }

}
