package jp.seraphr.fileupload.main

import javax.servlet.ServletContext

import jp.seraphr.fileupload.FileUploadApi
import jp.seraphr.fileupload.model.UploadSettings
import org.scalatra.{ LifeCycle, ScalatraServlet }
import upickle.Api

/**
 */
class ScalatraBootstrap extends LifeCycle {
  class FileUploadServlet extends ScalatraServlet with FileUploadApi {
    override protected val settings: UploadSettings = UploadSettings("fileparam")
    override val jsonApi: Api = upickle.default
  }

  override def init(context: ServletContext): Unit = {
    val tServlet = new FileUploadServlet
    context.mount(tServlet, "/fileupload")
  }
}
