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

import java.lang.reflect.Method
import fsart.diffTools.csvAlgo.CsvTools
import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.Operation
import fsart.diffTools.csvModel.{CsvDataImpl, CsvData}

/**
 *
 * User: fabien
 * Date: 22/05/12
 * Time: 15:32
 *
 */

object CsvRulesDsl {

  type Data = CsvData[String]

  object duplicatedLines {
    def of(data: Data): Data = {
      data.getDuplicatedLines
    }
  }

  object duplicatedKeys {
    def of(data: Data): Data = {
      data.getDuplicatedKeys
    }
  }


  type Invoker = (Method, Data)
  type Couple = (Method, Data, Data)
  type DiffData = CsvData[List[diff_match_patch.Diff]]



  class InvokerHelper(invoker: Invoker) {
    val method = invoker._1
    val dataA = invoker._2

    def withRef(dataRef: Data): Couple = {
      (method, dataA, dataRef)
    }
  }


  implicit def data2InvokerHelper(data1: Invoker) = new InvokerHelper(data1)
  implicit def invokeCouple(couple:Couple): DiffData = {
    val method = couple._1
    val dataNew = couple._2
    val dataRef = couple._3
    val res = method.invoke(CsvTools, dataNew, dataRef)
    res.asInstanceOf[DiffData]
  }



  object additionsMade {
    val cls = CsvTools.getClass

    def by(data: Data): Invoker = {
      (cls.getMethod("getAddedLines", classOf[CsvData[String]], classOf[CsvData[String]]), data)
    }
  }



  object suppressionsMade {
    val cls = CsvTools.getClass

    def by(data: Data): Invoker = {
      (cls.getMethod("getSupprimedLines", classOf[CsvData[String]], classOf[CsvData[String]]), data)
    }
  }



  object modificationsMade {
    val cls = CsvTools.getClass

    def by(data: Data): Invoker = {
      (cls.getMethod("getDifferenceLines", classOf[CsvData[String]], classOf[CsvData[String]]), data)
    }
  }



  class DiffDataHelper(couple:Couple) {
    val method = couple._1
    val dataA = couple._2
    val dataB = couple._3

    def mapValuesDuringComparison(mapVals:List[(String, String)]): DiffData = {
      CsvTools.getDifferenceLinesWithMapedDatas(dataA, dataB, mapVals)
    }
  }

  implicit def couple2diffDataHelper(data:Couple) = new DiffDataHelper(data)


}
