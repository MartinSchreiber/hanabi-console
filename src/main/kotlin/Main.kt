import core.*
import ui.HanabiConsole
import kotlin.random.Random

fun main() {
    Main.main()
}

class Main {

    companion object {
        val hanabiControl = HanabiControl()
        val hanabiConsole = HanabiConsole(hanabiControl)

        fun main() {
            for (key in hanabiControl.model.colorStacks.keys) {
                for (n in 1..5) {
                    hanabiControl.model.colorStacks[key]?.add(Card(n, key))
                }
            }
            repeat(12) {
                hanabiControl.model.moves.add(randomMove())
            }
            hanabiControl.model.hints = 4
            hanabiControl.model.storms = 2
            hanabiControl.model.discardStack.addAll(hanabiControl.model.drawStack.drop(14))

            hanabiConsole.display()
        }

        private fun randomMove(): Move {
            val retVal: Move = if(Random.nextBoolean()) {
                if(Random.nextBoolean()) {
                    Move(MOVE.PLAY, randomPlayer(), randomCard())
                } else {
                    Move(MOVE.DISCARD, randomPlayer(), randomCard())
                }
            } else {
                val hint: Hint = if(Random.nextBoolean()) {
                    Hint(randomPlayer(), randomColor())
                } else {
                    Hint(randomPlayer(), null, randomNumber())
                }
                Move(MOVE.HINT, randomPlayer(), null, hint)
            }
            return retVal
        }

        private fun randomPlayer(): Int {
            return Random.nextInt(hanabiControl.model.players.size)
        }

        private fun randomColor(): COLOR {
            return COLOR.values().random()
        }

        private fun randomNumber(): Int {
            return Random.nextInt(1, hanabiControl.maxNo + 1)
        }
        private fun randomCard(): Int {
            return Random.nextInt(hanabiControl.handCardNo)
        }
    }
}
