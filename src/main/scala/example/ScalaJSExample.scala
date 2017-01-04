package example

import japgolly.scalajs.react._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExportTopLevel



case class State(items: List[String], text: String, typeOfChange: Int)
object ScalaJSExample {

  @JSExportTopLevel("main")
  def main() {

    val state_0 = State(Nil, "", 0)
    val dispatcher1 = new Dispatcher[State](state_0)

    ReactDOM.render(TodoApp(AppProps(dispatcher1)), dom.document.getElementById("target"))


  }
}





