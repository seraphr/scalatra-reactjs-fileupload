package jp.seraphr.fileupload.dispatch

/**
 */
trait Dispatcher[T] {
  def dispatch(aMessage: T): Unit

  /**
   *
   * @param aToken handler's token from [[register]]
   */
  def waitFor(aToken: String): Unit
  def waitFor(aTokens: List[String]): Unit

  /**
   *
   * @param aHandler
   * @return handler's token
   */
  def register(aHandler: T => Unit): String
}
