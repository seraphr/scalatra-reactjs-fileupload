package jp.seraphr.fileupload.main

import japgolly.scalajs.react.ReactDOM
import jp.seraphr.fileupload.action.DefaultFileUploadActions
import jp.seraphr.fileupload.dispatch.FluxDispatcher
import jp.seraphr.fileupload.event.FileUploadEvent
import jp.seraphr.fileupload.store.DefaultFileUploadStore
import jp.seraphr.fileupload.view.FileUploadComponentModule
import org.scalajs.dom

import scala.scalajs.js

/**
 */
object FileUploadApp extends js.JSApp {
  @scala.scalajs.js.annotation.JSExport
  override def main(): Unit = {
    dom.console.info("enter main!!")
    val tDispatcher = new FluxDispatcher[FileUploadEvent]
    val tContextPath = "http://localhost:8080/fileupload"
    val tEC = scala.scalajs.concurrent.JSExecutionContext.queue

    val tActions = new DefaultFileUploadActions(upickle.default, tContextPath, tDispatcher, tEC)
    val tStore = new DefaultFileUploadStore(tDispatcher)
    val tComponentModule = new FileUploadComponentModule(tActions, tStore)

    val tNode = dom.document.getElementById("main")
    ReactDOM.render(tComponentModule.Component(), tNode)
  }
}
