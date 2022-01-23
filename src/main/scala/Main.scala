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

  val winningMessage = () => s"Player ${currentPlayer} has won!"
  val drawMessage = () => s"Game ended in a draw!"
  val currentPlayerTurn = () => s"It's ${currentPlayer}'s turn"

  statusDisplay.innerHTML = currentPlayerTurn()

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
    statusDisplay.innerHTML = currentPlayerTurn()
  }

  def handleResultValidation(): Unit = {
    var roundWon = false;
    Breaks.breakable {
      for (i <- 0 to 7) {
        Continues.breakable {
          val winCondition = winningConditions(i)
          var a = gameState(winCondition(0))
          var b = gameState(winCondition(1))
          var c = gameState(winCondition(2))
          if (a == "" || b == "" || c == "") {
            Continues.break
          }
          if (a == b && b == c) {
            roundWon = true;
            Breaks.break
          }
        }
      }
    }

    if (roundWon) {
      statusDisplay.innerHTML = winningMessage()
      gameActive = false
      return;
    }

    var roundDraw = !gameState.contains("")
    if (roundDraw) {
      statusDisplay.innerHTML = drawMessage()
      gameActive = false
      return
    }

    handlePlayerChange()
  }


  def handleCellClick(clickedCellEvent: Event): Unit = {
      val clickedCell = clickedCellEvent.target.asInstanceOf[Element]
      val clickedCellIndex = clickedCell.getAttribute("data-cell-index").toInt

      if (gameState(clickedCellIndex) != "" || !gameActive) {
          return;
      }

      handleCellPlayed(clickedCell, clickedCellIndex);
      handleResultValidation();
  }

  def handleRestartGame() = {
      gameActive = true;
      currentPlayer = "X";
      gameState = Array("", "", "", "", "", "", "", "", "")
      statusDisplay.innerHTML = currentPlayerTurn();
      document.querySelectorAll(".cell").foreach(cell => cell.innerHTML = "");
  }

  document.querySelectorAll(".cell").foreach(cell => cell.addEventListener("click", handleCellClick _));
  document.querySelector(".game--restart").addEventListener("click", _ => handleRestartGame())
}
