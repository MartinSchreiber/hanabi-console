package core

class HanabiControl(playerNames : List<String> = listOf("Martin", "Hexe", "Dennis", "Björn"),
                    numbers : List<Int> = listOf(1, 1, 1, 2, 2, 3, 3, 4, 4, 5),
                    maxHints : Int = 8, maxStorms : Int = 3,
                    mode : MODE = MODE.BASIC) {

    val handCardNo = if (playerNames.size in 2..3) 5 else 4
    val colorNo = if (mode == MODE.BASIC) 5 else 6
    val maxNo = numbers.maxOf { it }

    val cardset = numbers.map { n -> COLOR.values().asList().subList(0, colorNo).map { c -> Card(n, c)}.shuffled()}
        .flatten().shuffled().toMutableList()
    val model: HanabiModel = HanabiModel(playerNames, cardset, maxHints, maxStorms, handCardNo, colorNo)
    val ai = ArtificialDummy(model, numbers, colorNo)


    fun nextMove() : Boolean {
        val nextPlayer = model.players[model.moves.size % model.players.size]
        val move = ai.getMove(nextPlayer, model)

        return if (!model.gameOver)
            makeMove(nextPlayer, move)
        else false
    }

    fun points() : Int {
        return model.colorStacks.values.flatten().size
    }

    private fun makeMove(player: HanabiModel.Player, move : Move) : Boolean{
        model.moves.add(move)
        val moveReturn = when(move.type) {
            MOVE.PLAY -> play(player, move.cardIndex!!)
            MOVE.DISCARD -> {discard(player, move.cardIndex!!)}
            MOVE.HINT -> giveHint(player, move.hint!!)
        }
        val stormReturn = if (model.storms >= model.maxStorms)
                                {model.gameOver = true; false} else true
        val drawStackReturn = if (model.drawStack.isEmpty() && model.finalMoves-- <= 0)
                                    {model.gameOver = true; false} else true
            return moveReturn && stormReturn && drawStackReturn
    }

    private fun play(player: HanabiModel.Player, cardIndex : Int) : Boolean {
        return if (cardIndex in 0 until player.cards.size){
            ai.removeCard(player, cardIndex)
            play(player.cards.removeAt(cardIndex)) && draw(player)
        }
        else false
    }
    private fun play(card : Card) : Boolean {
        val stack = model.colorStacks[card.color]!!
        return if (stack.isNotEmpty() && card.follows(stack.takeLast(1)[0]))
        { stack.add(card);true }
        else { model.discard(card); model.storms++; false }
    }

    private fun discard(player: HanabiModel.Player, cardIndex : Int) : Boolean {
        return if (cardIndex in 0 until player.cards.size) {
            ai.removeCard(player, cardIndex)
            model.discard(player.cards.removeAt(cardIndex))
            draw(player)
        } else false
    }
    private fun draw(player: HanabiModel.Player) : Boolean {
        val card = model.draw()
        return if (card != null) {
            ai.drawCard(player)
            player.cards.add(card)
            true
        } else false
    }
    
    private fun giveHint(player: HanabiModel.Player, hint : Hint) : Boolean {
        ai.giveHint(player, hint)
        return true
    }

    override fun toString(): String {
        return model.toString()
    }
}