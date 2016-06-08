import java.io.{File, FileInputStream, ObjectInputStream, PrintWriter}
import java.util

import scala.annotation.tailrec
import scala.collection.JavaConversions._

object ScalaSort {


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

    val pairs = getListOfFiles("./saves/").map(x => (x, x.length()))

    val previousTime = System.nanoTime()

    mergeSort(pairs)

    val resultTime = System.nanoTime() - previousTime

    println("Scala time is: " + resultTime)

    resultTime

  }
}
