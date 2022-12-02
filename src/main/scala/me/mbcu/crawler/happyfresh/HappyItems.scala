package me.mbcu.crawler.happyfresh

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import me.mbcu.crawler.utils.Utils
import monix.eval.Task
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.collection.mutable
import monix.execution.Scheduler.Implicits.global
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import play.api.libs.json.JsValue
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr, elementList}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.concurrent.duration._
import scala.language.postfixOps

case class RequestItem(storeCode: Int, taxonName: String, taxonCode:Int, page: Int = 1)

case class Cart(reqs: ListBuffer[RequestItem], res: ListBuffer[JsValue], dir: File)


case class Hit(cart: Cart)

case class DeqStore(dir: File)

object HappyItems extends App{
  import scala.concurrent.duration._
  import scala.language.postfixOps
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  private val ws = StandaloneAhcWSClient()

  val root = "./happy/"
  val mode = Mode.MISSING_ONLY

  object Mode extends Enumeration {
    type Mode = Value
    val MISSING_ONLY, REWRITE = Value
  }


  def buildUrl(storeCode:Int, taxonCode: Int, page: Int) = s"https://gvg1d6u3wk.execute-api.ap-southeast-1.amazonaws.com/prod/catalog/stock_locations/$storeCode/taxons/$taxonCode/products?=undefined&page=$page&taxon_id=$taxonCode&popular=true&per_page=100"
  val jDir = new File(root)








  def getTaxons(in: (Int, String)): List[RequestItem] =  {
    val storeCode = in._1
    val html = in._2
    val browser = JsoupBrowser()
    val doc = browser.parseString(html)

    val items = doc >> elementList(".category-list-wrapper li")
    val subs = doc >> elementList(".subcategory-name").map(_ >> attr("href")("a"))
    subs.filter(_.startsWith("http")).map(p => {
      val full = p.split("/")(4)
      val code = Try(full.split("-").last.toInt).toOption // some links are broken
      (full, code)
    }).filterNot(_._2.isEmpty).map(p => RequestItem(storeCode, p._1, p._2.get))
  }

  def parseProducts(in: (JsValue,  RequestItem,  Cart)) =  (in._1, in._2, in._3, (in._1 \ "products").as[List[JsValue]])

  def parseNextPages(in: (JsValue, RequestItem, Cart, List[JsValue])) =  {
    val currentPage = (in._1 \ "current_page").as[Int]
    val pages = (in._1 \ "pages").as[Int]
    val nexts = if (currentPage == 1 && pages > 1 ) (2 to pages).map(p => RequestItem(in._2.storeCode, in._2.taxonName, in._2.storeCode, p)) else List.empty
    (in._2, in._3,  in._4, nexts)
  }

  def nextCart(in: (RequestItem, Cart, List[JsValue], Seq[RequestItem])) = {
    in._2.reqs.remove(0)
    in._2.res  ++= in._3
    in._2.reqs ++= in._4
    (in._1, in._2)
  }

  val f = for {
    f1 <- Task {
      mode match {
        case Mode.REWRITE => jDir.listFiles.filter(_.isDirectory)
        case _ => jDir.listFiles.filter(_.isDirectory).map(p => (p, new File(p.getPath + "/products.txt"))).filterNot(_._2.exists()).map(_._1)
      }
    }
    f2 <- {
      val x = f1.map(dir => {
        val js = Utils.readFile(new File(dir + "/info.txt"))
        val slug = (js \ "slug").as[String]
        val storeId = (js \ "id").as[Int]
        val url = s"https://www.happyfresh.id/$slug/"
        Seq (
          Task.fromFuture(ws.url(url)
            .withRequestTimeout(8 seconds)
            .get()
            .map(p => (storeId, p.body)).map(getTaxons)),
          Task.sleep(1 second)
        )
      }).flatten
      Task.sequence(x)
    }
    f3 <- {
      f2.map(p => {

        val req = p
        info(s"${req.storeCode} ${req.taxonName} page-${req.page}")
        ws.url(buildUrl(req.storeCode, req.taxonCode, req.page))
          .addHttpHeaders("X-API-Key" -> "HdI3wa6E3L6ECd1XYZZjJ92d4wUGOD4X6CrtO6MM")
          .withRequestTimeout(5 seconds)
          .get()
          .map(p => (Json.parse(p.body), req, cart)).map(parseProducts).map(parseNextPages).map(nextCart).map(p => {
          if (p._2.reqs.isEmpty){
            save(p._2.dir.getPath + "/products.txt", p._2.res)
            self ! "start"
          } else {
            self ! DelayedHit(p._2)
          }
        })
          .recover{
            case e: Exception =>
              self ! DelayedHit(cart)
          }

      })
    }
  } yield f2
}
