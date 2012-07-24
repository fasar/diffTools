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

import java.net.URL
import java.io.File
import io.Source
import fsart.helper.Loader
import fsart.diffTools.csvModel.{CsvDataImpl, CsvDataFirstLineAsHeaderImpl, CsvData}

/**
 *
 * User: fabien
 * Date: 04/05/12
 * Time: 09:34
 *
 */

object CsvFactory {

  def getCsvFile(file: String, firstLineAsHeader: Boolean): CsvData[String] = {
    //val file1URL = Loader.getFile(file)
    val bufSrc: Source = Source.fromString(file)
    getCsvFile(bufSrc, firstLineAsHeader)
  }

  def getCsvFile(file: URL, firstLineAsHeader: Boolean): CsvData[String] = {
    val bufSrc: Source = Source.fromURL(file)
    getCsvFile(bufSrc, firstLineAsHeader)
  }

  def getCsvFile(file: File, firstLineAsHeader: Boolean): CsvData[String] = {
    val bufSrc: Source = Source.fromFile(file)
    getCsvFile(bufSrc, firstLineAsHeader)
  }

  def getCsvFile(src: Source, firstLineAsHeader: Boolean): CsvData[String] = {
    val lines = src.getLines.toList
    val builder = new CsvBuilder
    builder.appendLines(lines, firstLineAsHeader)
    builder.getCvsData()
  }

  def getCsvData(data: List[List[String]], firstLineAsHeader: Boolean): CsvData[String] = {
    var header: List[String] = null
    var dataRes: List[List[String]] = data

    if (dataRes.size > 0 && firstLineAsHeader) {
      header = dataRes(0)
      dataRes = dataRes.drop(1)
    }

    CsvDataImpl [String](dataRes, header, ";")
  }
}
