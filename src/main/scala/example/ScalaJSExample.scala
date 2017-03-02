package example

import japgolly.scalajs.react._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel



case class State(items: List[String], text: String, typeOfChange: Int)
case class CounterState(count: Int, changeType: Int)
object ScalaJSExample {

  @JSExportTopLevel("main")
  def main() {

    val state_0 = State(Nil, "", 0)
    val dispatcher1 = new Dispatcher[State](state_0)

    ReactDOM.render(TodoApp(dispatcher1), dom.document.getElementById("target"))


  }


  @JSExportTopLevel("counter")
  def counter() {

    val state_0 = CounterState(0, 0)
    val dispatcher = new Dispatcher[CounterState](state_0)

    ReactDOM.render(Counter(CounterProps(dispatcher)), dom.document.getElementById("target"))


  }


}





