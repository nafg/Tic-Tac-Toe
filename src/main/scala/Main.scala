import org.scalajs.dom.document
import org.scalajs.dom.Element
import scala.util.control.Breaks
import org.scalajs.dom.Event

object Continues extends Breaks

case class Game(
    active: Boolean,
    currentPlayer: String,
    cells: List[String]
)

object Main extends App {
  val statusDisplay = document.querySelector(".game--status")

  val initialState = Game(true, "X", List("", "", "", "", "", "", "", "", ""))
  var game = initialState

  def winningMessage = s"Player ${game.currentPlayer} has won!"
  def drawMessage = s"Game ended in a draw!"
  def currentPlayerTurn = s"It's ${game.currentPlayer}'s turn"

  statusDisplay.innerHTML = currentPlayerTurn

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

  def handleCellPlayed(clickedCell: Element, clickedCellIndex: Int) = {
    game = game.copy(cells =
      game.cells.updated(clickedCellIndex, game.currentPlayer)
    )
    clickedCell.innerHTML = game.currentPlayer
  }

  def handlePlayerChange() = {
    game =
      game.copy(currentPlayer = if (game.currentPlayer == "X") "O" else "X")
    statusDisplay.innerHTML = currentPlayerTurn
  }

  def isWin(winCondition: List[Int]) =
    winCondition.map(game.cells) match {
      case List("X", "X", "X") | List("O", "O", "O") => true
      case _                                         => false
    }

  def handleResultValidation(): Unit = {
    val roundWon = winningConditions.exists(isWin)
    lazy val roundDraw = !game.cells.contains("")

    if (roundWon) {
      statusDisplay.innerHTML = winningMessage
      game = game.copy(active = false)
    } else if (roundDraw) {
      statusDisplay.innerHTML = drawMessage
      game = game.copy(active = false)
    } else {
      handlePlayerChange()
    }
  }

  def handleCellClick(clickedCellEvent: Event): Unit = {
    val clickedCell = clickedCellEvent.target.asInstanceOf[Element]
    val clickedCellIndex = clickedCell.getAttribute("data-cell-index").toInt

    if (game.cells(clickedCellIndex).isEmpty && game.active) {
      handleCellPlayed(clickedCell, clickedCellIndex)
      handleResultValidation()
    }
  }

  def handleRestartGame() = {
    game = initialState
    statusDisplay.innerHTML = currentPlayerTurn
    document.querySelectorAll(".cell").foreach(cell => cell.innerHTML = "")
  }

  document
    .querySelectorAll(".cell")
    .foreach(cell => cell.addEventListener("click", handleCellClick _))
  document
    .querySelector(".game--restart")
    .addEventListener("click", _ => handleRestartGame())
}
