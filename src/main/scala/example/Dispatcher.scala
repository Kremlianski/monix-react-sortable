package example

import monix.execution.Ack
import monix.execution.Ack.Continue
import monix.reactive.{Observable, Observer}
import monix.reactive.subjects.BehaviorSubject
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future


class Dispatcher [State](val initialState: State){

  private val dispatcher: BehaviorSubject[State => State] =  BehaviorSubject.apply(identity)

  val stream: Observable[State] = dispatcher.scan(initialState)((s, x) => x(s))


  def dispatch(f:State => State): Unit = dispatcher.onNext(f)

  def observer(f:State => Unit)= new Observer[State] {
    def onNext(s: State): Future[Ack] = {

      f(s)

      Continue
    }

    def onError(ex: Throwable): Unit =
      ex.printStackTrace()
    def onComplete(): Unit =
      println("Completed")
  }

  def subscribe(modeState:State=>Unit, filter:State=>Boolean): Cancelable =
    stream.filter(filter).subscribe(observer(modeState))

  def subscribe(modeState:State=>Unit): Cancelable =
    stream.subscribe(observer(modeState))


  def subscribeOpt(modeState:State=>Unit, filter:State=>Boolean): Option[Cancelable] =
    Option(stream.filter(filter).subscribe(observer(modeState)))

  def subscribeOpt(modeState:State=>Unit): Option[Cancelable] =
    Option(stream.subscribe(observer(modeState)))
}



