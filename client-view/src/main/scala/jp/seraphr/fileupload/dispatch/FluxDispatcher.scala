package jp.seraphr.fileupload.dispatch

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

class FluxDispatcher[T] extends Dispatcher[T] {
  import FluxDispatcher._

  private[this] val mCore = new JsFluxDispatcher()

  override def dispatch(message: T): Unit = mCore.dispatch(message)
  override def register(handler: (T) => Unit): String = mCore.register(handler)
  override def waitFor(token: String): Unit = mCore.waitFor(token)
  override def waitFor(token: List[String]): Unit = mCore.waitFor(token)
}

object FluxDispatcher {
  @JSName("Flux.Dispatcher")
  @js.native
  private class JsFluxDispatcher extends js.Object {
    def dispatch(message: Any): Unit = js.native
    def waitFor(token: String): Unit = js.native
    def waitFor(token: List[String]): Unit = js.native
    def register(handler: js.Function): String = js.native
  }
}