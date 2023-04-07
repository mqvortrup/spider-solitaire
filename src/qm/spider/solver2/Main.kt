package qm.spider.solver2

import qm.spider.game2.SpiderSolitaire

fun main() {
    val solver = RecursiveSolver(SpiderSolitaire.dealNewGame())
    val solution = solver.findSolution()
    if (solution.isEmpty()) println("no solution found in ${solver.movesTested} moves")
    else {
        println("solution found in ${solver.movesTested} moves")
        println(solution)
    }
}