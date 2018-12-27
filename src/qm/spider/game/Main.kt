package qm.spider.game

fun main(args: Array<String>) {
    val game = Spider.dealNewGame()
    println(game)
    println(game.stack.size)

    val done = false
    var lastMove: Move? =null
    while (!done) {

        val possibleMoves = game.getPossibleMoves()
        println(possibleMoves)
        println(possibleMoves.size)

        val input = readLine()
        if (input != "" && (input?.get(0) == 'D' || input?.get(0) == 'd'))
            game.deal()
        else if (input != "" && (input?.get(0) == 'U' || input?.get(0) == 'u'))
            if (lastMove != null) {
                println("Undoing $lastMove")
                game.undoMove(lastMove)
            }
            else
                println("nothing to undo")
        else {
            val moveIndex = if (input == "") 0 else input!!.toInt()
            lastMove = possibleMoves[moveIndex]
            game.executeMove(lastMove)
        }
        println(game)
        println(game.stack.size)
        println()
    }
}