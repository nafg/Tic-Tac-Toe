import org.scalajs.dom.document
import org.scalajs.dom.Element
import scala.util.control.Breaks
import org.scalajs.dom.Event

object Continues extends Breaks

object Main extends App {
  val statusDisplay = document.querySelector(".game--status")

  var gameActive = true
  var currentPlayer = "X"
  var gameState = Array("", "", "", "", "", "", "", "", "")

  def winningMessage = s"Player ${currentPlayer} has won!"
  def drawMessage = s"Game ended in a draw!"
  def currentPlayerTurn = s"It's ${currentPlayer}'s turn"

  statusDisplay.innerHTML = currentPlayerTurn

  val winningConditions = Array(
    Array(0, 1, 2),
    Array(3, 4, 5),
    Array(6, 7, 8),
    Array(0, 3, 6),
    Array(1, 4, 7),
    Array(2, 5, 8),
    Array(0, 4, 8),
    Array(2, 4, 6)
  )

  def handleCellPlayed(clickedCell: Element, clickedCellIndex: Int) = {
    gameState(clickedCellIndex) = currentPlayer
    clickedCell.innerHTML = currentPlayer
  }

  def handlePlayerChange() = {
    currentPlayer = if (currentPlayer == "X") "O" else "X"
    statusDisplay.innerHTML = currentPlayerTurn
  }

  def isWin(winCondition: Array[Int]) =
    winCondition.map(x => gameState(x)) match {
      case Array("X", "X", "X") | Array("O", "O", "O") => true
      case _                                           => false
    }

  def handleResultValidation(): Unit = {
    val roundWon = winningConditions.exists(isWin)
    lazy val roundDraw = !gameState.contains("")

    if (roundWon) {
      statusDisplay.innerHTML = winningMessage
      gameActive = false
    } else if (roundDraw) {
      statusDisplay.innerHTML = drawMessage
      gameActive = false
    } else {
      handlePlayerChange()
    }
  }

  def handleCellClick(clickedCellEvent: Event): Unit = {
    val clickedCell = clickedCellEvent.target.asInstanceOf[Element]
    val clickedCellIndex = clickedCell.getAttribute("data-cell-index").toInt

    if (gameState(clickedCellIndex).isEmpty && gameActive) {
      handleCellPlayed(clickedCell, clickedCellIndex)
      handleResultValidation()
    }
  }

  def handleRestartGame() = {
    gameActive = true;
    currentPlayer = "X";
    gameState = Array("", "", "", "", "", "", "", "", "")
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
