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

import fsart.diffTools.csvModel._



object CsvBuilderDsl {

  type Data[E] = List[List[E]]
  type Res[E] = CsvData[E]

  class DataHelper[E](val data:Data[E]) {
    def toCsv(): Res[E] = {
      new CsvDataImpl[E] (data, null,null)
    }
  }


  class ResHelper[E](val res:Res[E]) {
    def ignoreDuplicatedLines(): Res[E] = {
      CsvDataIgnoreDuplicatedImpl[E](res)
    }

    def firstLineAsHeader(): Res[E] = {
      firstLineAsHeader(true)
    }

    def firstLineAsHeader(isFirstLineAsHeader:Boolean = true): Res[E] = {
      if(isFirstLineAsHeader) {
        CsvDataFirstLineAsHeaderImpl[E](res)
      }else { res }
    }

    def firstLineAsData(): Res[E] = {
      res
    }

    def withKeysCol( cols:Int* ): Res[E] = {
      CsvDataSpecialKeyImpl[E](res, cols.toList)
    }

    def concatWith( toAdd:Res[E]) : Res[E] = {
      CsvDataConcatenedImpl[E](res, toAdd)
    }
  }

  class ResSortedHelper[E <% Ordered[E]](val res:Res[E]) {
    def sortedByCol( cols:Int* ): Res[E] = {
      CsvDataSortedImpl[E](res, cols.toList)
    }
  }


  implicit def Data2DataHelper[E](value: Data[E]) = new DataHelper(value)
  implicit def Res2ResHelper[E](value:Res[E]) = new ResHelper(value)
  implicit def Res2ResSortedHelper[E <% Ordered[E]](value:Res[E]) = new ResSortedHelper(value)


}
