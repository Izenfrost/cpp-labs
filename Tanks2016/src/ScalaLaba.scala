/*
package jasphalt

import java.io.{PrintWriter, FileInputStream, ObjectInputStream, File}
import java.util

import scala.annotation.tailrec

import scala.collection.JavaConversions._
import scala.io.Source

object ScalaThings {


  def getListOfFiles(dir: String): List[File] = {

    val d = new File(dir)

    if (d.exists && d.isDirectory) d.listFiles.filter(_.isFile).toList
    else List[File]()
  }


  def mergeSort(xs: List[(File, Long)]): List[(File, Long)] = {

    val m = xs.length / 2

    if (m == 0) xs
    else {

      @tailrec
      def merge(xs: List[(File, Long)], ys: List[(File, Long)], accu: List[(File, Long)]): List[(File, Long)] =

        (xs, ys) match {

          case (Nil, Nil) => accu.reverse
          case (Nil, y :: ys1) => merge(xs, ys1, y :: accu)
          case (x :: xs1, Nil) => merge(xs1, ys, x :: accu)
          case (x :: xs1, y :: ys1) =>

            if (x._2 < y._2) merge(xs1, ys, x :: accu)
            else merge(xs, ys1, y :: accu)

        }

      val (left, right) = xs splitAt m

      merge(mergeSort(left), mergeSort(right), List())

    }

  }

  def sortReplays(): Long = {

    val pairs = getListOfFiles("replays/").map(x => (x, x.length()))

    val previousTime = System.nanoTime()

    mergeSort(pairs)

    val resultTime = System.nanoTime() - previousTime

    println("Scala time is: " + resultTime)

    resultTime

  }

  /////////////////////////////////////////////////////////////////////////////////////

  def getListOfReplays(): List[util.LinkedList[GameState]] = {

    val listOfFiles = getListOfFiles("replays/")

    val readers = for {

      file <- listOfFiles

    } yield new ObjectInputStream(new FileInputStream("replays/" + file.getName))

    readers.map(x => x.readObject().asInstanceOf[util.LinkedList[GameState]])

  }

  def updateStatistics(): Unit = {

    val replays = getListOfReplays()

    val centerOfRoad = (Constants.LEFT_LANE + Constants.RIGTH_LANE) / 2.0

    val lane = "You " + (meanPlayerX(replays) match {

      case oncoming if oncoming < centerOfRoad => "like breaking"
      case passing if passing > centerOfRoad => "are keeping"
      case _ => "are"

    }) + " the law"

    val carModel = "The most popular car is: " + (mostPopularCarModel(replays) match {

      case 0 => "orange"
      case 1 => "mini-truck"
      case 2 => "taxi"
    })

    val carDirection = "The busiest traffic is: " + (mostPopularDirection(replays) match {

      case Constants.ONCOMING_CAR => "oncoming"
      case Constants.PASSING_CAR => "passing"

    })

    val score = "Best score: " + bestScore(replays)

    val difficulty = "You like " + (mostPopularDifficulty(replays) match {

      case Constants.HARD_LEVEL => "hardcore stuff"
      case Constants.NORMAL_LEVEL => "piece a cake stuff"

    })

    val gameLength = f"Longest game: ${longestGame(replays)}%3.2f"

    val statistics = List(lane, carModel, carDirection, score, difficulty).mkString("\n")

    val writer = new PrintWriter(new File("resources/statistics.txt"))

    writer.write(statistics)

    writer.close()

  }

  def longestGame(replays: List[util.LinkedList[GameState]]): Double = {

    replays.maxBy(_.length).length / 60.0

  }

  def mostPopularDirection(replays: List[util.LinkedList[GameState]]): Int = {

    val spriteDirections = getter(replays, (state: GameState) => state.getSpriteDirection)

    getMostCommon(spriteDirections)

  }

  def mostPopularDifficulty(replays: List[util.LinkedList[GameState]]): Int = {

    val gameDifficulties = getter(replays, (state: GameState) => state.getDifficulty)

    getMostCommon(gameDifficulties)

  }

  def bestScore(replays: List[util.LinkedList[GameState]]): Long = {

    val scores = getter(replays, (state: GameState) => state.getScore)

    scores.max

  }

  def mostPopularCarModel(replays: List[util.LinkedList[GameState]]): Int = {

    val carTypes = getter(replays, (state: GameState) => state.getSpriteType)

    getMostCommon(carTypes)

  }

  def meanPlayerX(replays: List[util.LinkedList[GameState]]): Double = {

    val playerX = getter(replays, (state: GameState) => state.getPlayerX)

    average(playerX)

  }

  def getter[T](replays: List[util.LinkedList[GameState]], getter: (GameState) => T) = {

    for (replay <- replays; state <- replay) yield getter(state)

  }

  def getMostCommon[T](list: List[T])(implicit num: Numeric[T]) = {

    list.groupBy(x => x).maxBy(_._2.size)._1

  }


  def average[T](it: Iterable[T])(implicit num: Numeric[T]) = {

    num.toDouble(it.sum) / it.size

  }

  /////////////////////////////////////////////////////////////////////////////////////

  def compress[T](values: List[T]): List[T] = compressTail(Nil, values)

  @scala.annotation.tailrec
  def compressTail[T](seen: List[T], remaining: List[T]): List[T] =
    remaining match {

      case Nil => seen
      case x :: y :: xs if (x == y) => compressTail(seen, y :: xs)
      case x :: xs => compressTail(seen ::: List(x), xs)

    }

  def findCommonPrefixR(l1: List[Int], l2: List[Int]): List[Int] =
    l1 match {
      case Nil => Nil
      case x :: xs => if (l2 != Nil && l2.head == x) x :: findCommonPrefixR(xs, l2.tail) else Nil
    }


  def sequencer(): Unit = {

    val replays = getListOfReplays()

    val rawListOfSequences = for (replay <- replays) yield (for (state <- replay) yield state.getSpriteType).toList

    val listOfSequences = rawListOfSequences.map((x) => compress(x))

    val listOfSubseq = for (seq <- listOfSequences; subseq <- listOfSequences.filter(_ != seq)) yield findCommonPrefixR(seq, subseq)

    val mostCommonSubseq = listOfSubseq.filter(_.size > 1).groupBy(identity).mapValues(_.size).maxBy(_._2)

    println(listOfSubseq)

    println("kek")

    println(mostCommonSubseq)

    val listOfTokens = mostCommonSubseq._1.collect {

      case 0 => "orange car"
      case 1 => "truck"
      case 2 => "taxi"

    }

    val sequence = s"Most common spawn sequence: \n${listOfTokens.mkString("->")}\n"

    val percentage = f"Percentage: ${(mostCommonSubseq._2.toDouble / listOfSubseq.size) * 100.0}%3.2f%%"

    println(sequence + percentage)

    val writer = new PrintWriter(new File("resources/sequence.txt"))

    writer.write(sequence + percentage)

    writer.close()

  }

}
*/
