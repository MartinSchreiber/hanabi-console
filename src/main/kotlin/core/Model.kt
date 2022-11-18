package core

enum class COLOR {YELLOW, GREEN, BLUE, RED, WHITE, RAINBOW}
enum class MOVE{PLAY, DISCARD, HINT}
enum class MODE{BASIC, COLORRUSH1, COLORRUSH2}

data class Move(val type : MOVE, val playerIndex : Int, val cardIndex : Int?=null, val hint : Hint?=null)
data class Hint(val targetPlayerIndex : Int, val color : COLOR? = null, val number : Int? = null)

class Card(val number : Int, val color : COLOR) {
    fun follows(card : Card) : Boolean = color == card.color && number == card.number + 1
    override fun toString(): String = "[$color\t$number]"
}

class HanabiModel(playerNames : List<String>, cardset: MutableList<Card>,
                  val maxHints: Int, val maxStorms: Int,
                    val handCardNo : Int, val colorNo : Int) {

    val drawStack = cardset
    val discardStack = mutableListOf<Card>()
    val colorStacks = COLOR.values().asList().subList(0, colorNo).associateWith { mutableListOf<Card>() }

    val players = playerNames.map {Player(it)}
    var hints = maxHints;var storms = 0
    var moves = mutableListOf<Move>()
    var gameOver = false;var finalMoves = players.size
    val points = if (storms < maxStorms) colorStacks.values.sumOf { it.size } else 0

    fun draw() : Card? = if (drawStack.size>0) drawStack.removeAt(0) else {null}
    fun discard(card : Card) = discardStack.add(discardStack.size, card)

    inner class Player(val name : String) {
        val cards: MutableList<Card> = MutableList(handCardNo) {draw()!!}
        override fun toString(): String = name + "\n" + cards.joinToString("\n")
    }

    override fun toString(): String = players.joinToString("\n") { it.toString() } + "\n" +
            colorStacks.map{ it.key.name + "\n" + it.value.toString() }.joinToString("\n") + "\n"+
            drawStack.joinToString("\n") { it.toString() } + "\n" + drawStack.size + "\n" +
            discardStack.joinToString("\n") { it.toString() } + "\n" + discardStack.size +
            "\n$hints/$maxHints Hints| $storms/$maxStorms Storms" +
            "\n$colorNo colors and $handCardNo hand-cards"
}