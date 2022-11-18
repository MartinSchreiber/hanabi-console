package core

interface Intelligence {
    fun getMove(player : HanabiModel.Player, hanabiModel : HanabiModel) : Move
    fun giveHint(player: HanabiModel.Player, hint : Hint)
    fun removeCard(player: HanabiModel.Player, cardIndex: Int)
    fun drawCard(player: HanabiModel.Player)
}

class ArtificialDummy(val hanabiModel : HanabiModel, val numbers : List<Int>, val colorNo : Int) : Intelligence {
    inner class CardInfo {
        var numberInfo = BooleanArray(maxNo) { true }
        var colorInfo = BooleanArray(colorNo) { true }
        var number: Int? = null; var color: COLOR? = null
    }

    private val maxNo = numbers.maxOf { it }
    private val numberCounts = numbers.distinct().associateWith{n1 -> numbers.count{n2 -> n1 == n2 }}

    private var cardsRemaining = Array(maxNo) {n -> Array(colorNo) {numberCounts[n] ?: 0} }

    var cardInfos = hanabiModel.players.associateWith { MutableList(it.cards.size) {CardInfo()} }

    override fun getMove(player: HanabiModel.Player, hanabiModel: HanabiModel): Move{
        TODO("Not yet implemented")
    }
    override fun giveHint(player: HanabiModel.Player, hint: Hint) {
        takeHintBase(player, hint)
    }
    override fun removeCard(player: HanabiModel.Player, cardIndex: Int) {
        cardInfos[player]!!.removeAt(cardIndex)
    }
    override fun drawCard(player: HanabiModel.Player) {
        cardInfos[player]!!.add(CardInfo())
    }

    private fun takeHintBase(player: HanabiModel.Player, hint: Hint) {
        val cardInfo = cardInfos[player]!!
        player.cards.forEachIndexed {cI, c ->
            if(hint.number != null) {
                cardInfo[cI].numberInfo = cardInfo[cI].numberInfo.mapIndexed {i, f ->
                    if(hint.number == c.number)
                        hint.number == i + 1
                    else
                            (hint.number != i + 1) && f
                }.toBooleanArray()
            }
            else if(hint.color != null && hint.color != COLOR.values().last()) {
                cardInfo[cI].colorInfo = cardInfo[cI].colorInfo.mapIndexed {i, f ->
                    if(hint.color == c.color || c.color == COLOR.values().last())
                        hint.color.ordinal == i
                    else
                            (hint.color.ordinal != i) && f
                }.toBooleanArray() //|| COLOR.values().last().ordinal == i
                TODO("set RAINBOW-INFO to false, when card with only R and one other INFO get a color-hint with a different color" +
                        "also set R-I to false, when card gets a hint with a different color")
            }
        }
    }

    private fun setCardInfoVals(cardInfo: CardInfo) {
        if (cardInfo.colorInfo.sumOf { if (it) 1 else 0 as Int} == 1) {
            cardInfo.colorInfo.forEachIndexed { i, c ->
                if (c) cardInfo.color = COLOR.values()[i]
            }
        }
        if (cardInfo.numberInfo.sumOf { if (it) 1 else 0 as Int} == 1) {
            cardInfo.numberInfo.forEachIndexed { i, n ->
                if (n) cardInfo.number = i + 1
            }
        }
    }
}