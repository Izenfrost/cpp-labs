import java.io._

import scala.io.Source

object ScalaStatistic {

  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) d.listFiles.filter(_.isFile).toList
    else List[File]()
  }

  def getNumberOfReplays(): Int = {
    val listOfFiles = getListOfFiles("./saves/")
    listOfFiles.size
  }

  def writeStat(
                 playerDeaths: Int,
                 enemiesKilled: Int,
                 playerBulletsFired: Int,
                 enemyBulletsFired: Int,
                 homeDeaths: Int,
                 wins: Int,
                 loses: Int,
                 bricksDestroyed: Int): Unit = {
    val numberOfFiles = getNumberOfReplays()
    val oldStat = readStat().map(_.toInt)
    println(oldStat)
    val newStat = List(playerDeaths, enemiesKilled, playerBulletsFired, enemyBulletsFired, homeDeaths, wins, loses, bricksDestroyed, numberOfFiles)
    println(newStat)
    val writer = new PrintWriter(new File("statistics.txt"))
    writer.write(addNewStat(oldStat, newStat).mkString("\r\n"))
    writer.close()
  }

  def readStat(): List[String] = {
    Source.fromFile("statistics.txt").getLines().toList
  }

  def addNewStat(oldStat: List[Int], newStat: List[Int]): List[Int] = {
    var i = 0
    (for (i<-oldStat.indices) yield oldStat(i) + newStat(i))(collection.breakOut)
  }
}