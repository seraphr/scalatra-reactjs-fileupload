package jp.seraphr.fileupload

import jp.seraphr.fileupload.model.{ FileUploadResult, UploadSettings }
import org.apache.commons.io.IOUtils
import org.apache.commons.io.output.NullOutputStream
import org.scalatra.{ BadRequest, Ok, RequestEntityTooLarge, ScalatraBase }
import org.scalatra.servlet.{ FileUploadSupport, MultipartConfig, SizeConstraintExceededException }

/**
 */
trait FileUploadApi extends ScalatraBase with FileUploadSupport {
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024 * 1024), fileSizeThreshold = Some(300 * 1024 * 1024)))

  protected val settings: UploadSettings

  import upickle._
  val jsonApi: Api
  import jsonApi._

  post("/upload") {
    fileParams.get(settings.fileParam).fold(BadRequest("no file param")) {
      tFile =>
        val tRealSize = IOUtils.copyLarge(tFile.getInputStream, NullOutputStream.NULL_OUTPUT_STREAM)
        val tResult = FileUploadResult(tFile.name, tFile.getSize, tRealSize)

        Ok(jsonApi.write(tResult))
    }
  }

  get("/settings") {
    Ok(jsonApi.write(settings))
  }

  error {
    case e: SizeConstraintExceededException => RequestEntityTooLarge("too much!")
  }

}
