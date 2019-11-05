package me.mbcu.crawler.happyfresh

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.StandaloneWSResponse
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Future
import scala.reflect.io.File
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

case class Store(name: String, content: String)


object HappyStores extends App{
  import scala.concurrent.duration._
  import scala.language.postfixOps

  val input = List (
    (1, -6.262529d, 106.784251d), // Jaksel
    (2, -6.180511300000001d, 106.8283831d), // Jakarta Pusat
    (3, -6.1554057d, 106.8926634d), // Jakut
    (4, -6.1486651d, 106.7352584d), // Jakbar
    (5, -6.225013800000001d, 106.9004472d), // Jaktim
    (6, -6.917463899999999d, 107.6191228d) // Bandung
  )
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  private val ws = StandaloneAhcWSClient()

  def getAreaStores(lat: Double, lng: Double): Future[StandaloneWSResponse]  =
    ws.url(s"https://gvg1d6u3wk.execute-api.ap-southeast-1.amazonaws.com/prod/sprinkles/v2/stock_locations/nearby?get_next_available_slot=1&lat=$lat&lon=$lng")
      .addHttpHeaders("X-Spree-Client-Token" -> "0115f406e71219ec9ea58e2eaaa4480ef966bdc42e245ec4bf601b23f07bd48e")
      .withRequestTimeout(3 seconds)
      .get()

  val parserAreaStores: (String) => Map[Int, Store] = raw => {
    val jsValue = Json.parse(raw)
    val res = (jsValue \ "stock_locations").as[Array[JsValue]]
    res.map(p => {
      val id   = (p \ "id").as[Int]
      val name = (p \ "slug").as[String]
      id -> Store(id + "-" + name, Json.prettyPrint(Json.toJson(p)))
    }).toMap
  }

  def writeFile(name: String, content: String): Unit = {
    val path = s"./happy/$name"
    File(path).createDirectory(force = false, failIfExists = false)
    Files.write(Paths.get(path + "/info.txt"), content.getBytes(StandardCharsets.UTF_8))
  }

  def createHappyFolder() = {
    val path = "./happy"
    File(path).createDirectory(force = false, failIfExists = false)
  }

  def futAreaStore(in : (Int, Double, Double)): Future[Map[Int, Store]] = {
    val i   = in._1
    val lat = in._2
    val lng = in._3
    Thread.sleep(i * 500)
    getAreaStores(lat, lng) .map(_.body).map(parserAreaStores)  recoverWith{case e => futAreaStore(i, lat, lng)}
  }

  val core = input.map(p => {
    val lat = p._2
    val lng = p._3

    val x = Task.fromFuture(getAreaStores(lat, lng)).map(_.body).map(parserAreaStores).map(p => p.values.map(q => writeFile(q.name, q.content)))
    val pause = Task(Thread.sleep(1000))
    Seq(x, pause)
  })
    .map(p => Task.sequence(p))


  val f = for {
    _ <- Task(createHappyFolder())
    f2 <- Task.sequence(core)
  } yield  f2

  f.runToFuture
  Thread.sleep(20000)

}
