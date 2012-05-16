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
 * Date: 07/05/12
 * Time: 17:28
 *
 */

class CsvBuilder(var separator:String = ";")  {myObj=>

  var headers:List[String] = List.empty

  private var array:List[List[String]] = List.empty

  def appendLine(line:String) {
    val split:List[String] = line.split(separator).toList
    array :+= split
  }

  def appendLines(lines:List[String], firstLineAsHeader:Boolean = true) {
    if(firstLineAsHeader) {
      appendLinesWithHeaders(lines)
    }else {
      appendLinesWithoutHeaders(lines)
    }
  }
  def appendLinesWithoutHeaders(lines:List[String]) {
    for(line <- lines) {
      appendLine(line)
    }
  }
  def appendLinesWithHeaders(lines:List[String]) {
    setHeaders(lines(0))
    for(line <- lines.drop(1)) {
      appendLine(line)
    }
  }

  def setHeaders(line:String) {
    headers = line.split(separator).toList
  }


  def getCvsData(): CsvData[String] = {
    val nbMaxCol = array.foldLeft(0) {
      (nbCol, elem) => scala.math.max(nbCol, elem.size)
    }
    val resArray = array.map {
      elem => elem ++ List.fill[String](nbMaxCol - elem.size) {
        ""
      }
    }
    new CsvData[String]() {
      override val headers = myObj.headers
      override val array = resArray
      override val separator = myObj.separator
    }
  }
}
