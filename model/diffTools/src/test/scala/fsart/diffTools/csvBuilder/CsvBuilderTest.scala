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

package fsart.diffTools.csvBuilder

import org.apache.commons.logging.{LogFactory, Log}
import org.junit.Test
import org.junit.Assert._
import fsart.diffTools.CsvBuilder.CsvBuilder
import fsart.diffTools.csvModel.CsvData

/**
 *
 * User: fabien
 * Date: 07/05/12
 * Time: 17:39
 *
 */

class CsvBuilderTest {
  private val log: Log = LogFactory.getLog(this.getClass)

  @Test
  def csvFile_test1 {
    val cvsBuilder = new CsvBuilder()
    var csv: CsvData[String] = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 0)

    cvsBuilder.appendLine("1;2;3;4")
    csv = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 1)
    assertTrue(csv.array(0).size == 4)

    cvsBuilder.appendLine("1;2;3;4")
    assertTrue(csv.array.size == 1)
    csv = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 2)
    assertTrue(csv.array(0).size == 4)
    assertTrue(csv.array(1).size == 4)

    cvsBuilder.appendLine("1;2;3;4;5")
    assertTrue(csv.array.size == 2)
    csv = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 3)
    assertTrue(csv.array(0).size == 5)
    assertTrue(csv.array(1).size == 5)
    assertTrue(csv.array(2).size == 5)
  }

  @Test
  def csvFile_test2 {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.setHeaders("a;b;c;d")

    var csv: CsvData[String] = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 0)
    assertTrue(csv.headers == List("a", "b", "c", "d"))
  }


}
