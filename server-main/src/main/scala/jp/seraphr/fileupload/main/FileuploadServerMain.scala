package jp.seraphr.fileupload.main

import org.eclipse.jetty.server.{ Handler, Server }
import org.eclipse.jetty.server.handler.{ DefaultHandler, HandlerList, ResourceHandler }
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
 */
object FileUploadServerMain extends App {
  val tPort = 8080

  val tServer = new Server(tPort)
  val tContext = new WebAppContext()
  tContext.setContextPath("/")
  tContext.setResourceBase("src/main/webapp")
  tContext.addEventListener(new ScalatraListener)
  tContext.setInitParameter(ScalatraListener.LifeCycleKey, classOf[ScalatraBootstrap].getName)

  val tResourceHandler = new ResourceHandler()
  tResourceHandler.setDirectoriesListed(true)
  tResourceHandler.setWelcomeFiles(Array("index.html"))
  //  tResourceHandler.setResourceBase("..")
  tResourceHandler.setResourceBase("../webapp/assets/")

  val tHandlers = new HandlerList()
  tHandlers.setHandlers(Array[Handler](tResourceHandler, tContext, new DefaultHandler()))
  tServer.setHandler(tHandlers)

  tServer.start()
  tServer.join()
}
