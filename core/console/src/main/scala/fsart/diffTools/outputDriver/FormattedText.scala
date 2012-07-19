package fsart.diffTools.outputDriver

/**
 *
 * User: fabien
 * Date: 19/07/12
 * Time: 11:05
 *
 */

case class FormattedText(val text:String,
                    val kind: TextKind.Value) {

  def this(text:String) {
    this(text, TextKind.Normal)
  }

}
