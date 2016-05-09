package jp.seraphr.fileupload.action

import jp.seraphr.fileupload.dispatch.Dispatcher
import jp.seraphr.fileupload.event.FileUploadEvent
import jp.seraphr.fileupload.model.{ FileUploadResult, UploadSettings }
import org.scalajs.dom
import org.scalajs.dom.FormData
import org.scalajs.dom.ext.{ Ajax, AjaxException }

import scala.concurrent.{ ExecutionContext, Promise }
import scala.util.{ Failure, Success }

/**
 */
class DefaultFileUploadActions(aJsonApi: upickle.Api, aContextPath: String, aDispatcher: Dispatcher[FileUploadEvent], aEC: ExecutionContext) extends FileUploadActions {
  private val mSettingsPath = aContextPath + "/settings"
  private val mUploadPath = aContextPath + "/upload"
  private implicit val mEC = aEC

  import aJsonApi._

  override def uploadFile(aFormData: FormData): Unit = {
    dom.console.info("uploadFile")
    val tReq = new dom.XMLHttpRequest()
    val tPromise = Promise[dom.XMLHttpRequest]()

    tReq.onreadystatechange = { (e: dom.Event) =>
      if (tReq.readyState.toInt == 4) {
        if ((tReq.status >= 200 && tReq.status < 300) || tReq.status == 304)
          tPromise.success(tReq)
        else
          tPromise.failure(AjaxException(tReq))
      }
    }

    // こういう ActionCreatorから生成されてしまうActionは、どう処理すべきなのかな。
    tReq.upload.onprogress = { (e: dom.ProgressEvent) =>
      val tProgress = FileUploadEvent.Progress(e.loaded, e.total)
      dom.console.info(s"onProgress / ${tProgress}")
      aDispatcher.dispatch(tProgress)
    }

    tReq.open("POST", mUploadPath)
    tReq.responseType = "text"
    tReq.timeout = 1000000
    tReq.withCredentials = false
    tReq.send(aFormData)

    tPromise.future.onComplete {
      case Success(tReq) =>
        val tResult = aJsonApi.read[FileUploadResult](tReq.responseText)
        aDispatcher.dispatch(FileUploadEvent.UploadFinished(tResult.filename, tResult.headerLength, tResult.realLength))
      case Failure(e) =>
        dom.console.warn(s"fail to upload file to ${mUploadPath} / ${e.getMessage}")
    }
  }

  override def resetForm(): Unit = {
    dom.console.info("resetForm")
    Ajax("GET", mSettingsPath, null, 10000, Map(), false, "text").onComplete {
      case Success(tResult) =>
        val tSettings = aJsonApi.read[UploadSettings](tResult.responseText)
        aDispatcher.dispatch(FileUploadEvent.ResetUI(tSettings.fileParam))
      case Failure(e @ AjaxException(_)) =>
        dom.console.warn(s"fail to get ${mSettingsPath} / timeout: ${e.isTimeout} / status: ${e.xhr.status} ")
      case Failure(e) =>
        dom.console.warn(s"fail to get ${mSettingsPath} / ${e.getMessage}")
    }
  }
}
