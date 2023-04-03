package qm.spider.solver2

import qm.spider.game.Spider

fun main() {
    val solver = RecursiveSolver(Spider.dealNewGame())
    val solution = solver.findSolution()
    if (solution.isEmpty()) println("no solution found in ${solver.movesTested} moves")
    else {
        println("solution found in ${solver.movesTested} moves")
        println(solution)
    }
}