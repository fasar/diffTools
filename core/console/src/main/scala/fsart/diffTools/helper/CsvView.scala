package fsart.diffTools.helper

import fsart.diffTools.model.CsvData
import name.fraser.neil.plaintext.diff_match_patch.Diff
import name.fraser.neil.plaintext.diff_match_patch

/**
 *
 * User: fabien
 * Date: 07/05/12
 * Time: 23:53
 *
 */

trait CsvView {

  def getView(csv:CsvData[List[diff_match_patch.Diff]]): Array[Byte];


}
