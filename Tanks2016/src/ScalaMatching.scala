import java.util

import com.sun.media.jfxmedia.events.PlayerStateEvent.PlayerState


object ScalaMatching {
  def Pseudocode(playerState: List[GameState]): Unit ={
      for(player <- playerState) {
        val list = List(player.direction, player.shot)
        val prevDirection: Direction = player.direction
        for (current <- list){current match {
          case d: Direction => {
            if (d != prevDirection) d match {
              case Direction.D => println("Игрок движется вниз")
              case Direction.L => println("Игрок движется влево")
              case Direction.R => println("Игрок движется вправо")
              case Direction.U => println("Игрок движется вверх")
              case Direction.STOP => println("Игрок остановился")
            }
          }
          case s: Boolean => if(s) println("Игрок выстрелил")
        }
      }
    }
  }
}
