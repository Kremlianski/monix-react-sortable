package example


import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom._
import monix.execution.Scheduler.Implicits.global
import monix.execution.Cancelable

import scala.scalajs.js
import scala.scalajs.js._


import net.scalapro.sortable._


case class AppProps(d: Dispatcher[State])

object TodoApp {
  private val TodoList = ReactComponentB[(List[String], Dispatcher[State])]("TodoList")
    .render_P { prop =>
      def refFunc(el: Element) = Callback {
        val props = new SortableProps {
          override val handle = ".glyphicon-move"
          override val animation = 150
          override val onEnd: UndefOr[Function1[EventS, Unit]] = js.defined { (e: EventS) => {
            val els = el.getElementsByClassName("text")
            val size = els.length
            var buffer = Vector.empty[String]
            (0 until size).foreach(i => {
              buffer :+= els.item(i).textContent
            })
            prop._2.dispatch((s: State) => s.copy(items = buffer.toList).copy(typeOfChange = 2))
          }
          }
        }
        if (el != null)
          new Sortable(el, props)


      }

      def createItem(item: (String, Int)) = <.div(^.className := "list-group-item",
        <.span(^.className := "badge", item._2 + 1),
        <.span(^.className := "glyphicon glyphicon-move"),
        <.span(^.className := "text", item._1))

      <.div(^.id := "listWithHandle",
        ^.key := java.util.UUID.randomUUID.toString,
        ^.ref ==> refFunc, ^.className := "list-group",
        prop._1.zipWithIndex.map(createItem))

    }
    .build


  class Backend($: BackendScope[AppProps, State]) {
    var end: Option[Cancelable] = None

    def onChange(e: ReactEventI) = {
      val disp = $.props.runNow().d
      val newValue = e.target.value
      Callback(disp.dispatch(s => s.copy(text = newValue).copy(typeOfChange = 1)))
    }

    def handleSubmit(e: ReactEventI) =
      e.preventDefaultCB >>
        Callback($.props.runNow().d.dispatch((s: State) => s.copy(items = s.items :+ s.text)
          .copy(text = "").copy(typeOfChange = 1)))


    def render(state: State) = {
      val state = $.state.runNow()
      val props = $.props.runNow()
      <.div(
        <.h3("TODO"),
        TodoList((state.items, props.d)),
        <.form(^.onSubmit ==> handleSubmit,
          <.input(^.onChange ==> onChange, ^.value := state.text),
          <.button("Add #", state.items.length + 1)
        )
      )
    }
  }


  val TodoApp = ReactComponentB[AppProps]("TodoApp")
    .initialState_P((_.d.initialState))
    .renderBackend[Backend]
    .componentDidMount(scope =>

      Callback {
        val disp = scope.props.d
        scope.backend.end = Option(disp.stream

          .subscribe(disp.observer(x => scope.modState(_ => x).runNow())))
      }
    )

    .componentWillUnmount(scope =>
      Callback(scope.backend.end.map(_.cancel)))


    .build

  def apply(props: AppProps) = TodoApp(props)

}
