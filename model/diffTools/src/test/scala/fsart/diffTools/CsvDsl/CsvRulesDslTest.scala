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

package fsart.diffTools.CsvDsl

/**
 *
 * User: fabien
 * Date: 22/05/12
 * Time: 15:40
 *
 */

import org.apache.commons.logging.{LogFactory, Log}
import org.junit.Test
import org.junit.Assert._
import name.fraser.neil.plaintext._
import name.fraser.neil.plaintext.diff_match_patch.Operation
import fsart.diffTools.CsvBuilder.CsvBuilder
import fsart.diffTools.csvModel.CsvData



class CsvRulesDslTest {
  private val log: Log = LogFactory.getLog(this.getClass)


  @Test
  def duplicatedLinesTest {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.appendLine("1;2;3;4")
    cvsBuilder.appendLine("5;6;7;8")
    cvsBuilder.appendLine("1;2;3;4")
    cvsBuilder.appendLine("1;2;3;500")
    cvsBuilder.appendLine("5;6;7;900")
    var csv1: CsvData[String] = cvsBuilder.getCvsData

    import CsvRulesDsl._
    var res = duplicatedLines of csv1
    assertTrue(res.array.size == 2)

    cvsBuilder.appendLine("5;6;7;900")
    csv1 = cvsBuilder.getCvsData
    res = duplicatedLines of csv1
    assertTrue(res.array.size == 4)
  }

  @Test
  def duplicatedKeysTest {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.appendLine("1;2;3;4")
    cvsBuilder.appendLine("5;6;7;8")
    cvsBuilder.appendLine("1;2;3;4") //duplicated line, it is not in res
    cvsBuilder.appendLine("1;2;3;500")
    var csv1: CsvData[String] = cvsBuilder.getCvsData

    import CsvRulesDsl._
    var res = duplicatedKeys of csv1
    System.out.println("coucou " + res)
    assertTrue(res.array.size == 2)

    cvsBuilder.appendLine("5;6;7;900")
    csv1 = cvsBuilder.getCvsData
    res = duplicatedKeys of csv1
    assertTrue(res.array.size == 4)
  }


  @Test
  def addedTest {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.appendLine("1;2;3;4")
    cvsBuilder.appendLine("5;6;7;8")
    cvsBuilder.appendLine("7;8;9;10")
    var csv1: CsvData[String] = cvsBuilder.getCvsData
    val cvsBuilder2 = new CsvBuilder()
    cvsBuilder2.appendLine("1;2;3;4")
    cvsBuilder2.appendLine("5;6;7;8")
    var csv2: CsvData[String] = cvsBuilder2.getCvsData

    import CsvRulesDsl._
    var res = additionsMade by csv1 withRef csv1
    println("Mon tab est " + res.array)
    assertTrue(res.array.size == 0)

    res = additionsMade by csv2 withRef csv1
    assertTrue(res.array.size == 0)
    // csv1 have 2 lines more and one key more
    cvsBuilder.appendLine("7;8;9;10")
    csv1 = cvsBuilder.getCvsData
    res = additionsMade by csv2 withRef csv1
    assertTrue(res.array.size == 0)

    // csv1 have 1 lines more but same number of key
    cvsBuilder2.appendLine("7;8;9;10")
    csv2 = cvsBuilder2.getCvsData
    res = additionsMade by csv2 withRef csv1
    assertTrue(res.array.size == 0)

    // csv2 have 1 key more
    cvsBuilder2.appendLine("10;8;9;10")
    csv2 = cvsBuilder2.getCvsData
    res = additionsMade by csv2 withRef csv1
    println("res : " + res.array)
    assertTrue(res.array.size == 1)
  }

  @Test
  def suppressedTest {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.appendLine("1;2;3;4")
    cvsBuilder.appendLine("5;6;7;8")
    var csv1: CsvData[String] = cvsBuilder.getCvsData
    val cvsBuilder2 = new CsvBuilder()
    var csv2: CsvData[String] = cvsBuilder2.getCvsData

    import CsvRulesDsl._
    var res = suppressionsMade by csv2 withRef csv1
    assertTrue(res.array.size == 2)

    // Check for duplicated lines
    cvsBuilder2.appendLine("1;2;3;4")
    csv2 = cvsBuilder2.getCvsData
    res = suppressionsMade by csv2 withRef csv1
    System.out.println("Salut coucou " + res.array.size)

    assertTrue(res.array.size == 1)

    // Check for 3 added lines with 2 duplicated
    cvsBuilder2.appendLine("5;6;7;8")
    csv2 = cvsBuilder2.getCvsData
    res = suppressionsMade by csv2 withRef csv1
    assertTrue(res.array.size == 0)
  }

  @Test
  def modifiedTest {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.appendLine("1;3;3;4")
    cvsBuilder.appendLine("5;6;7;8")
    cvsBuilder.appendLine("10;10;10;10")
    var csv1: CsvData[String] = cvsBuilder.getCvsData
    val cvsBuilder2 = new CsvBuilder()
    cvsBuilder2.appendLine("1;2;3;2")
    cvsBuilder2.appendLine("5;6;7;8")
    cvsBuilder2.appendLine("11;11;11;11")
    var csv2: CsvData[String] = cvsBuilder2.getCvsData

    import CsvRulesDsl._
    var res = modificationsMade by csv2 withRef csv1
    System.out.println("Salut coucou " + res.array.size)

    assertTrue(res.array.size == 2)
    assertTrue(res.array(0)(1)(0).operation == Operation.DELETE)
    assertTrue(res.array(0)(1)(0).text == "3")
    assertTrue(res.array(0)(1)(1).operation == Operation.INSERT)
    assertTrue(res.array(0)(1)(1).text == "2")
  }

  @Test
  def modifiedAndFiltredTest {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.appendLine("1;3;3;4")
    cvsBuilder.appendLine("2;3;4;5")
    cvsBuilder.appendLine("3;10;9;10")
    var csv1: CsvData[String] = cvsBuilder.getCvsData
    val cvsBuilder2 = new CsvBuilder()
    cvsBuilder2.appendLine("1;2;3;2")
    cvsBuilder2.appendLine("2;6;7;8")
    cvsBuilder2.appendLine("3;11;11;11")
    var csv2: CsvData[String] = cvsBuilder2.getCvsData

    import CsvRulesDsl._
    var res = modificationsMade by csv2 withRef csv1 mapValuesDuringComparison ( List(
      ("1", "2"),
      ("10", "11")
    ))
    System.out.println("Salut coucou " + res.array.size)
    println("Tb " + res.array(2))

    assertTrue(res.array.size == 3)
    assertTrue(res.array(2)(1)(0).operation == Operation.EQUAL)
    assertTrue(res.array(2)(1)(0).text == "11")
    assertTrue(res.array(2)(2)(0).operation == Operation.DELETE)
    assertTrue(res.array(2)(2)(0).text == "9")
    assertTrue(res.array(2)(2)(1).operation == Operation.INSERT)
    assertTrue(res.array(2)(2)(1).text == "11")
  }


}
