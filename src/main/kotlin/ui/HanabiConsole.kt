package ui

import core.*
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi

//TODO: moves left/last round...

//TODO: OffTopic: no collision detection at bottom of screen manjaro
class HanabiConsole(private val control: HanabiControl) {

    private val colors = mapOf(
        COLOR.YELLOW to Ansi.Color.YELLOW,
        COLOR.GREEN to Ansi.Color.GREEN,
        COLOR.BLUE to Ansi.Color.BLUE,
        COLOR.RED to Ansi.Color.RED,
        COLOR.WHITE to Ansi.Color.WHITE,
        COLOR.RAINBOW to Ansi.Color.MAGENTA //TODO: Fix somehow
    )

//    private val drawStackWidth = control.model.drawStack.size * cardGap.length
//    private val discardStackWidth = control.model.discardStack.size * cardGap.length
//    private val tokenWidth = (control.model.maxStorms + control.model.maxHints) * tokenBack.length
//    private val contentWidth = max(max(drawStackWidth, discardStackWidth), tokenWidth)
    private val contentWidth = 120

    private var currentWidth = 0
    private var currentLine = ""

    fun display() {
        print(clearConsole)

        printCentered("", lineChar, cornerChar)
        printCentered("Hanabi for Console!")

        printCentered("Moves", lineChar)
        printMoves()

        printCentered("Hint- and Storm-Tokens", lineChar)
        printTokens()

        printCentered("Draw-Stack", lineChar)
        printStack(control.model.drawStack)

        printCentered("Color-Stacks", lineChar)
        printInfo()
        printColorStacks()


        printCentered("Discard-Stack", lineChar)
        printStack(control.model.discardStack)

        printCentered("Players", lineChar)
        printPlayers()
        printCentered("", lineChar, cornerChar)
    }

    private fun printInfo() =
        printCentered("Zug: ${control.model.moves.size+1}$infoGap${control.points()} Punkte${infoGap}Runde: " +
                ((control.model.moves.size / control.model.players.size)+1), "_")

    private fun printPlayers() = control.model.players.forEach { printPlayer(it) }

    private fun printPlayer(player: HanabiModel.Player) {
        printCentered(player.name, thinLineChar)

        player.cards.forEach { addCard(it) }
        printCentered()
        printCentered("", thinLineChar)

        for(i in 0 until player.cards.size) {
            printCardInfo(control.ai.cardInfos[player]!![i])
        }
    }

    private fun printMoves() { control.model.moves.forEachIndexed {i, m -> printMove(i+1, m) } }

    private fun printMove(number: Int, move: Move) {
        val player = control.model.players[move.playerIndex]
        val maxMoveWidth = control.model.moves.size.toString().length
        val maxPlayerWidth = control.model.players.maxOf { it.name.length }
        val nameFormat = "%${maxPlayerWidth}s "

        addToLine(String.format("%${maxMoveWidth}d:  ", number))
        addToLine(String.format(nameFormat, player.name))

        when(move.type) {
            MOVE.PLAY -> {
                addToLine("played ")
                addCard(player.cards[move.cardIndex!!])
            }
            MOVE.DISCARD -> {
                addToLine("discarded ")
                addCard(player.cards[move.cardIndex!!])
            }
            MOVE.HINT -> {
                val player2 = control.model.players[move.hint!!.targetPlayerIndex]

                addToLine("gave ")
                addToLine(String.format(nameFormat, player2.name))
                addToLine("the hint ")

                if (move.hint.number != null) {
                    player2.cards.forEach { c ->
                        if(c.number == move.hint.number)
                            { addCard(move.hint.number, Ansi.Color.WHITE) }
                        else
                            { addCard() }
                    }
                }
                else if (move.hint.color != null) {
                    player2.cards.forEach { c ->
                        if(c.color == move.hint.color)
                            { addCard(null, colors[move.hint.color]) }
                        else
                            { addCard() }
                    }
                }
            }
        }

        printIndented()
    }

    private fun printCardInfo(cardInfo: ArtificialDummy.CardInfo) {
        cardInfo.numberInfo.forEachIndexed { i, nI ->
            if (nI) { addCard((i+1).toString(), Ansi.Color.WHITE) }
               else { addCard(invalidCardVal, Ansi.Color.BLACK) }
        }

        addToLine(cardInfoGap)
        addCard(cardInfo.number, colors[cardInfo.color])
        addToLine(cardInfoGap)

        cardInfo.colorInfo.forEachIndexed { i, cI ->
            if (cI) { addCard(unknownCardVal, colors[COLOR.values()[i]]!!) }
            else { addCard(invalidCardVal, Ansi.Color.BLACK) }
        }
        printCentered()
    }

    private fun printStack(list: List<Card>) {
        list.forEach { addCard(it) }
        printCentered()
    }

    private fun printColorStacks() {
        for(i in 0 until control.maxNo) {
            control.model.colorStacks.forEach { stack ->
                if (stack.value.size > i) { addCard(stack.value[i]) }
                                     else { addToLine(cardGap) }
                addToLine(stackGap)
            }
            dropLastFromLine(stackGap.length)
            printCentered()
        }
    }

    private fun printTokens() {
        for (t in 1..control.model.maxHints) {
            if(t <= control.model.hints) { addToLine(ansi().fgBrightGreen().a(hintToken).reset().toString()
                                                , tokenBack.length) }
                                    else { addToLine(ansi().fgBrightBlack().a(tokenBack).reset().toString()
                                                , tokenBack.length) }
        }
        addToLine(tokenGap)
        for (t in 1..control.model.maxStorms) {
            if(t <= control.model.storms) { addToLine(ansi().fgBrightYellow().a(stormToken).reset().toString()
                                                , tokenBack.length) }
                                     else { addToLine(ansi().fgBrightBlack().a(tokenBack).reset().toString()
                                                , tokenBack.length) }
        }
        printCentered()
    }

    private fun addCard(card: Card) {
        addCard(card.number, colors[card.color])
    }

    private fun addCard(number: Int? = null, color: Ansi.Color? = null) {
        if(number != null && color != null) { addCard(number.toString(), color) }
                   else if (number != null) { addCard(number.toString(), Ansi.Color.BLACK) }
                    else if (color != null) { addCard(unknownCardVal, color) }
                                       else { addCard(unknownCardVal, Ansi.Color.BLACK) }
    }

    private fun addCard(value: String, color: Ansi.Color) {
        addToLine(ansi().fgBright(color)
            .a("$preCard$value$postCard")
            .reset().toString(), cardGap.length)
    }

    private fun addToLine(string: String, length: Int? = null) {
        currentLine += string
        currentWidth += length ?: string.length
    }

    private fun dropLastFromLine(length: Int) {
        currentLine = currentLine.dropLast(length)
        currentWidth -= length
    }

    private fun printCurrentLine() {
        println(currentLine)
        currentLine = ""; currentWidth = 0
    }

    private fun printIndented(line: String? = null, indent: Int = 10, filler: String = space, side: String = sideChar) {
        if (line != null) addToLine(line)

        currentLine = filler.repeat(indent) +
                        currentLine +
                            filler.repeat(currentSpaces() - indent)
        currentLine = "$side$currentLine$side"

        printCurrentLine()
    }

    private fun printCentered(line: String? = null, filler: String = space, side: String = sideChar) {
        if (line != null) addToLine(line)

        val padding = filler.repeat(currentSpaces()/2)

        currentLine = "$padding$currentLine$padding"
        currentWidth += padding.length*2
        currentLine += filler.repeat(currentSpaces())
        currentLine = "$side$currentLine$side"

        printCurrentLine()
    }

    private fun currentSpaces() = contentWidth - currentWidth

    companion object {
        private const val clearConsole = "\u001b[H\u001b[2J"

        private const val preCard = "["
        private const val postCard = "]"

        private const val unknownCardVal = "?"
        private const val invalidCardVal = "/"

        private const val hintToken = "(?)"
        private const val stormToken = "(Z)"
        private const val tokenBack = "(O)"

        private const val sideChar = "|"
        private const val lineChar = "="
        private const val thinLineChar = "-"
        private const val cornerChar = "#"

        private const val space = " "
        private const val cardGap = "   "
        private const val stackGap = "    "
        private const val tokenGap = "    "
        private const val cardInfoGap = " | "
        private const val infoGap = "____________"
    }
}