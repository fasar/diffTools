package fsart.diffTools.Translate

import fsart.diffTools.csvModel.CsvData
import name.fraser.neil.plaintext.diff_match_patch
import fsart.diffTools.outputDriver.FormattedText

/**
 *
 * User: fabien
 * Date: 19/07/12
 * Time: 12:37
 *
 */

trait Translator {
  def translate(csv: CsvData[List[diff_match_patch.Diff]]): CsvData[List[FormattedText]]
}
