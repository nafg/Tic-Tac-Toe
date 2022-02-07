import org.scalajs.dom.document
import org.scalajs.dom.Element
import scala.util.control.Breaks
import org.scalajs.dom.Event

import zio.*

object Continues extends Breaks

case class Game(
    active: Boolean,
    currentPlayer: String,
    cells: List[String]
)

object Main extends App {
  val statusDisplay = document.querySelector(".game--status")

  val initialState = Game(true, "X", List("", "", "", "", "", "", "", "", ""))

  def winningMessage(game: Game) = s"Player ${game.currentPlayer} has won!"
  def drawMessage = s"Game ended in a draw!"
  def currentPlayerTurn(game: Game) = s"It's ${game.currentPlayer}'s turn"

  val winningConditions = List(
    List(0, 1, 2),
    List(3, 4, 5),
    List(6, 7, 8),
    List(0, 3, 6),
    List(1, 4, 7),
    List(2, 5, 8),
    List(0, 4, 8),
    List(2, 4, 6)
  )

  def handleCellPlayed(
      game: Game
  )(clickedCell: Element, clickedCellIndex: Int) = {
    val game2 = game.copy(cells =
      game.cells.updated(clickedCellIndex, game.currentPlayer)
    )
    clickedCell.innerHTML = currentPlayerTurn(game2)
    game2
  }

  def handlePlayerChange(game: Game) = {
    val game2 =
      game.copy(currentPlayer = if (game.currentPlayer == "X") "O" else "X")
    statusDisplay.innerHTML = currentPlayerTurn(game2)
    game2
  }

  def isWin(game: Game)(winCondition: List[Int]) =
    winCondition.map(game.cells) match {
      case List("X", "X", "X") | List("O", "O", "O") => true
      case _                                         => false
    }

  def handleResultValidation(game: Game): Game = {
    val roundWon = winningConditions.exists(isWin(game))
    lazy val roundDraw = !game.cells.contains("")

    if (roundWon) {
      statusDisplay.innerHTML = winningMessage(game)
      game.copy(active = false)
    } else if (roundDraw) {
      statusDisplay.innerHTML = drawMessage
      game.copy(active = false)
    } else {
      handlePlayerChange(game)
    }
  }

  def handleCellClick(game: Game)(clickedCellEvent: Event): UIO[Game] = UIO {
    val clickedCell = clickedCellEvent.target.asInstanceOf[Element]
    val clickedCellIndex = clickedCell.getAttribute("data-cell-index").toInt

    if (game.cells(clickedCellIndex).isEmpty && game.active) {
      val game2 = handleCellPlayed(game)(clickedCell, clickedCellIndex)
      handleResultValidation(game2)
    } else game
  }

  def handleRestartGame = UIO {
    val game = initialState
    statusDisplay.innerHTML = currentPlayerTurn(game)
    document.querySelectorAll(".cell").foreach(cell => cell.innerHTML = "")
    game
  }

  extension (element: Element)
    def addClickListener(f: Event => UIO[Unit]): UIO[Unit] =
      UIO {
        element.addEventListener(
          "click",
          event => Runtime.global.unsafeRunAsync_(f(event))
        )
      }

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    var _game = initialState

    statusDisplay.innerHTML = currentPlayerTurn(_game)

    def updateGame(game: Game) = UIO { _game = game }

    ZIO
      .foreach(document.querySelectorAll(".cell"))(
        _.addClickListener(handleCellClick(_game)(_).flatMap(updateGame))
      )
      .flatMap { _ =>
        document
          .querySelector(".game--restart")
          .addClickListener(_ => handleRestartGame.flatMap(updateGame))
      }
      .as(ExitCode.success)
}
