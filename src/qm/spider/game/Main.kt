package qm.spider.game

fun main(args: Array<String>) {
    val game = Spider.dealNewGame()
    println(game)
    println(game.stack.size)

    val done = false
    while (!done) {

        val possibleMoves = game.getPossibleMoves()
        println(possibleMoves)
        println(possibleMoves.size)

        val input = readLine()
        if (input?.get(0) == 'D')
            game.deal()
        else {
            val moveIndex = input!!.toInt()
            game.executeMove(possibleMoves[moveIndex])
        }
        println(game)
        println()
    }
}