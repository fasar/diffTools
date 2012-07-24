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

package fsart.diffTools.csvDsl

import org.junit.Test
import org.junit.Assert._
import fsart.diffTools.csvDsl.CsvRulesDsl.modificationsMade
import fsart.diffTools.csvModel.CsvDataSpecialKey

/**
 *
 * User: fabien
 * Date: 25/05/12
 * Time: 11:03
 *
 */

class CsvBuilderAndRulesDsl {


  private val dataA = List(List(4,4,4,4), List(2,2,2,2), List(1,1,1,1), List(1,1,1,1),  List(5,5,5,5),  List(8,8,8,8)).map{_.map{_.toString}}
  private val dataB = List(List(4,5,4,5), List(2,3,3,3), List(1,1,1,1), List(1,2,1,1),  List(5,6,6,6),  List(7,7,7,7)).map{_.map{_.toString}}


  @Test
  def modifiedWithMultipleColAsKeyTest {
    import CsvBuilderDsl._
    import CsvRulesDsl._

    val csv3 = dataB toCsv() withKeysCol (0,2)
    println("class name : " + csv3.getClass.getName)
    assertTrue(csv3.isInstanceOf[CsvDataSpecialKey[_]])

    val csv1 = dataA toCsv() withKeysCol (0,2) ignoreDuplicatedLines()
    val csv2 = csv3 ignoreDuplicatedLines()

    var res2 = modificationsMade by csv2 withRef csv1

    var res = modificationsMade by csv2 withRef csv1 mapValuesDuringComparison (
      List(
      ("10", "11")
    ))
    println("tb : " + res)
    assertTrue(res.array.size > 0)



  }


}
