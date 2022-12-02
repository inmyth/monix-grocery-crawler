package me.mbcu.crawler.utils

import java.io.{File, FileInputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import play.api.libs.json.{JsValue, Json}

object Utils{
  def readFile(file: File): JsValue = {
    val stream = new FileInputStream(file)
    try {  Json.parse(stream) } finally { stream.close() }
  }

  def writeFile(name: String, content: String): Unit = Files.write(Paths.get(name), content.getBytes(StandardCharsets.UTF_8))

}
