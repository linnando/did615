package org.linnando.did615.genetic

case class GenerationLog(nextGeneration: Generation,
                         elite: Vector[Individual],
                         crossoverParticipants: Vector[Individual],
                         children: Vector[Individual],
                         childrenAfterMutation: Vector[Individual])
