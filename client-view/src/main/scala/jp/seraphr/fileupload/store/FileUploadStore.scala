package jp.seraphr.fileupload.store

import jp.seraphr.fileupload.dispatch.Dispatcher
import jp.seraphr.fileupload.event.FileUploadEvent

/**
 */
trait FileUploadStore {
  def subscribe(f: FileUploadState => Unit): Subscription
}

class DefaultFileUploadStore(aDispatcher: Dispatcher[FileUploadEvent]) extends FileUploadStore {
  private var mSubscribers = List[FileUploadState => Unit]()
  private var mState: FileUploadState = FileUploadState.Init("")
  def state = mState

  aDispatcher.register { tEvent =>
    val tNewState: FileUploadState = tEvent match {
      case FileUploadEvent.ResetUI(tParam)                       => FileUploadState.Init(tParam)
      case FileUploadEvent.Progress(tCurrent, tSize)             => FileUploadState.Uploading(tCurrent, tSize)
      case FileUploadEvent.UploadFinished(tName, tHSize, tRSize) => FileUploadState.Uploaded(tName, tHSize, tRSize)
    }

    if (tNewState != mState) {
      mState = tNewState
      mSubscribers.foreach(_(tNewState))
    }
  }

  override def subscribe(f: FileUploadState => Unit): Subscription = {
    mSubscribers = f :: mSubscribers
    new DefaultSubscription(f)
  }

  class DefaultSubscription(val f: FileUploadState => Unit) extends Subscription {
    override def unsubscribe(): Unit = {
      mSubscribers = mSubscribers.filter(_ != f)
    }
  }
}

trait Subscription {
  def unsubscribe(): Unit
}

sealed trait FileUploadState
object FileUploadState {
  val init: FileUploadState = Init("")
  case class Init(fileParam: String) extends FileUploadState
  case class Uploading(current: Long, size: Long) extends FileUploadState
  case class Uploaded(filename: String, headerSize: Long, realSize: Long) extends FileUploadState
}
