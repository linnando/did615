package org.linnando.did615.genprogramming

case class ExecutionState(world: AntWorld,
                          visited: Set[(Int, Int)],
                          position: (Int, Int),
                          orientation: (Int, Int),
                          memory: Byte,
                          remainingSteps: Int) {

  def sense: Byte = world.neighbour(position, orientation) match {
    case Some(p) => if (visited(p)) 0 else world(p)
    case None => -1
  }

  def move: ExecutionState = {
    val nextPosition = world.neighbour(position, orientation) match {
      case Some(p) => p
      case None => position
    }
    copy(visited = visited + nextPosition,
      position = nextPosition,
      remainingSteps = remainingSteps - 1)
  }

  def turnLeft: ExecutionState =
    copy(orientation = world.turnLeft(orientation),
      remainingSteps = remainingSteps - 1)

  def turnRight: ExecutionState =
    copy(orientation = world.turnRight(orientation),
      remainingSteps = remainingSteps - 1)

}
