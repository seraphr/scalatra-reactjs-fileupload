package jp.seraphr.fileupload.event

sealed trait FileUploadEvent
/**
 */
object FileUploadEvent {
  case class Progress(current: Long, size: Long) extends FileUploadEvent
  case class UploadFinished(filename: String, headerSize: Long, realSize: Long) extends FileUploadEvent
  case class ResetUI(fileParam: String) extends FileUploadEvent
}
