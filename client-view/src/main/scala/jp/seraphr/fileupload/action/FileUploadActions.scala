package jp.seraphr.fileupload.action

import org.scalajs.dom

/**
 */
trait FileUploadActions {
  def uploadFile(aFile: dom.FormData): Unit
  def resetForm(): Unit
}