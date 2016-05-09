package jp.seraphr.fileupload.view

import japgolly.scalajs.react
import jp.seraphr.fileupload.action.FileUploadActions
import jp.seraphr.fileupload.store.{ FileUploadState, FileUploadStore, Subscription }
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLFormElement

/**
 */
class FileUploadComponentModule(aActions: FileUploadActions, aStore: FileUploadStore) {
  sealed trait FileUploadProp
  case object Init extends FileUploadProp
  case class DefaultProp(fileParam: String) extends FileUploadProp
  case class UploadingProp(current: Long, size: Long) extends FileUploadProp
  case class UploadedProp(filename: String, headerSize: Long, realSize: Long) extends FileUploadProp

  import react._
  import japgolly.scalajs.react.vdom.prefix_<^._

  private def onSubmit(e: ReactEventI): Callback = {
    e.preventDefaultCB >> Callback {
      val tFormElement = e.target.parentNode.domCast[HTMLFormElement]
      val tFormData = new dom.FormData(tFormElement)
      aActions.uploadFile(tFormData)
    }
  }

  private def onReset(): Callback = {
    Callback {
      aActions.resetForm()
    }
  }

  private def onReset(e: ReactEventI): Callback = {
    e.preventDefaultCB >> onReset()
  }

  private val mDefaultComponent =
    ReactComponentB[DefaultProp]("fileupload")
      .stateless
      .noBackend
      .render_P { tDefaultProp =>
        <.form(
          <.input(^.`type` := "file", ^.size := "100px", ^.name := tDefaultProp.fileParam),
          <.input(^.`type` := "button", ^.value := "send", ^.onClick ==> onSubmit)
        )
      }.build

  private val mUploadingComponent =
    ReactComponentB[UploadingProp]("uploading")
      .stateless
      .noBackend
      .render_P { tUploadingProp =>
        val tCurrent = tUploadingProp.current
        val tSize = tUploadingProp.size
        val tRatio = (tCurrent * 100.0) / tSize

        <.div(
          <.div("uploading... "),
          <.div(s"${tUploadingProp.current} / ${tUploadingProp.size} (${tRatio}%)")
        )
      }.build

  private val mUploadedComponent =
    ReactComponentB[UploadedProp]("uploaded")
      .stateless
      .noBackend
      .render_P { tUploadedProp =>
        <.div(
          <.div(s"uploaded filename=${tUploadedProp.filename} headeSize=${tUploadedProp.headerSize} realSize=${tUploadedProp.realSize}"),
          <.button(^.`type` := "button", "reset", ^.onClick ==> onReset)
        )
      }.build

  class Backend(aScope: BackendScope[Unit, FileUploadState]) {
    private var mSubscription: Subscription = _

    def render(s: FileUploadState): ReactElement = s match {
      case FileUploadState.Init("")                        => <.div("initializing...")
      case FileUploadState.Init(tParam)                    => mDefaultComponent(DefaultProp(tParam))
      case FileUploadState.Uploading(tCurrent, tSize)      => mUploadingComponent(UploadingProp(tCurrent, tSize))
      case FileUploadState.Uploaded(tName, tHSize, tRSize) => mUploadedComponent(UploadedProp(tName, tHSize, tRSize))
    }

    def start: Callback =
      Callback { mSubscription = aStore.subscribe(s => aScope.setState(s).runNow()) } >> onReset()

    def stop: Callback = Callback {
      mSubscription.unsubscribe()
    }

  }

  val Component =
    ReactComponentB[Unit]("upload-root")
      .initialState(FileUploadState.init)
      .renderBackend[Backend]
      .componentDidMount(_.backend.start)
      .componentWillUnmount(_.backend.stop)
      .build
}
