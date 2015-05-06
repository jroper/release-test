import sbtrelease._
import ReleaseKeys._

val myTask1 = taskKey[Unit]("my custom task 1")

val myTask2 = taskKey[Unit]("my custom task 2")

myTask1 := {
  throw sys.error("this should fail first")
}

myTask2 := {
  throw sys.error("this task should never be reached")
}

releaseSettings

releaseProcess := Seq[ReleaseStep](
  ReleaseStep(action = releaseTask(myTask1)),
  ReleaseStep(action = { state =>
    // Does exactly the same as the releaseTask convenience function
    Project.extract(state).runAggregated(myTask1, state)
  }),
  ReleaseStep(action = { state =>
    println("Reached step three...")
    Project.extract(state).runTask(myTask2, state)._1
  })
)
