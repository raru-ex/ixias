/*
 * This file is part of the IxiaS services.
 *
 * For the full copyright and license information,
 * please view the LICENSE file that was distributed with this source code.
 */

package net.ixias
package core.port.adapter.persistence.lifted

import scalaz._
import scalaz.Scalaz._
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration.Duration
import scala.util.{ Success, Failure }
import scala.language.implicitConversions
import core.port.adapter.persistence.io.IOAction

final case class FutureOps[A](val self: Future[A]) extends AnyVal {
  def await(): IOAction#ValidationNel[Unit] = await(_ => Unit)
  def await[A1](implicit convert: A => A1): IOAction#ValidationNel[A1] = {
    Await.ready(self, Duration.Inf)
    self.value.get match {
      case Success(v) => convert(v).successNel
      case Failure(t) => (t.getMessage()).failureNel
    }
  }
}

trait ToFutureOps {
  implicit def ToFutureOps[A](a: Future[A]) = FutureOps(a)
}
