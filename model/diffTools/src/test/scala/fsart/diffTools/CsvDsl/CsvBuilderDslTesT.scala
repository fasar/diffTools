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

import org.apache.commons.logging.{LogFactory, Log}
import org.junit.Test
import fsart.diffTools.CsvBuilder.CsvBuilder
import org.junit.Assert._
import fsart.diffTools.csvModel.{CsvKey, CsvKeySpecial, CsvDataSpecialKey, CsvData, CsvDataColNumberException}

/**
 *
 * User: fabien
 * Date: 23/05/12
 * Time: 11:17
 *
 */

class CsvBuilderDslTesT {
  private val log: Log = LogFactory.getLog(this.getClass)

  private val datas = List(List(4,5,6,7), List(2,3,4,5), List(1,2,3,4), List(2,3,4,5)).map{_.map{_.toString}}

  @Test
  def createCsvData{
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas toCsv()
    val csv2 = csv1 sortedByCol (1,2,3)
    val csv3 = csv2 ignoreDuplicatedLines
    val csv4 = csv3 firstLineAsData
    val csv5 = csv3 firstLineAsHeader

    val csvAll = datas toCsv() firstLineAsHeader() withKeysCol (1,2) ignoreDuplicatedLines() sortedByCol (2)

  }

  @Test
  def concatCsvData{
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas toCsv() firstLineAsHeader
    val csv2 = datas toCsv()
    val res = csv1 concatWith csv2
    assertTrue(res.array.size == 7)

  }

  @Test
  def createIngoredDuplicatedLinesCsvData{
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas toCsv()
    assertTrue(csv1.array.size == 4)

    val csv2 = datas toCsv()  ignoreDuplicatedLines()
    assertTrue(csv2.array.size == 3)
  }

  @Test
  def sortedLinesCsvData{
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas toCsv()
    assertTrue(csv1.array.size == 4)

    val csv2 = datas toCsv() sortedByCol (0)
    assertTrue(csv2.array.size == 4)
    assertTrue(csv2.array(0)(0) == "1")
    assertTrue(csv2.array(1)(0) == "2")
    assertTrue(csv2.array(2)(0) == "2")
    assertTrue(csv2.array(3)(0) == "4")
  }

  @Test
  def firstLineAsHeaderCsvData{
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas toCsv()
    assertTrue(csv1.array.size == 4)

    val csv2 = datas toCsv() firstLineAsHeader()
    assertTrue(csv2.array.size == 3)
    assertTrue(csv2.headers(0) == "4")
    assertTrue(csv2.headers(1) == "5")
    assertTrue(csv2.headers(2) == "6")
    assertTrue(csv2.headers(3) == "7")


  }
  @Test
  def keysEqualsCsvData{
    import CsvBuilderDsl._

    val csv1 = datas toCsv()
    assertTrue(csv1.array.size == 4)
    val csv2 = datas toCsv()

    val key1 = csv1.getKeys(1)
    val key2 = csv2.getKeys(3)

    assertTrue( key1 ==  key2)


  }


  @Test
  def specialKeysCsvDataGetLines{
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas toCsv() withKeysCol (0,1)
    assertTrue(csv1.array.size == 4)

    val key1 = csv1.getKeys(0)
    val key1Lines = csv1.getLinesOfKey(key1)

    assertTrue(key1Lines.size > 0)

  }


  @Test(expected = classOf[CsvDataColNumberException])
  def testErrorWhenDefineKeyColMoreThanColDatas {
    import CsvBuilderDsl._
    val csv1:CsvData[String] = datas toCsv() withKeysCol (78, 1, 44)
  }

  @Test(expected = classOf[CsvDataColNumberException])
  def testErrorWhenDefineSortedColMoreThanColDatas {
    import CsvBuilderDsl._
    val csv1:CsvData[String] = datas toCsv() sortedByCol(78, 1, 44)
  }

  @Test
  def emptyDataCsvData{
    val datas2:List[List[String]] = List(List.empty[String])
    import CsvBuilderDsl._

    val csv1:CsvData[String] = datas2 toCsv()
    assertTrue(csv1.isEmpty)

    val csv2:CsvData[String] = datas2 toCsv() firstLineAsHeader()
    assertTrue(csv2.isEmpty)
    assertTrue(csv2.headers.size==0)


    val csv3 = datas toCsv() withKeysCol (1,2)
    val csv4 = datas toCsv() ignoreDuplicatedLines()
    val csv5 = datas toCsv() sortedByCol (2)

  }

}
