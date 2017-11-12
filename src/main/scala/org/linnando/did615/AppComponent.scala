package org.linnando.did615

import angulate2.router.Router
import angulate2.std._

@Component(
  selector = "did615-app",
  templateUrl = "src/main/resources/app.component.html",
  styleUrls = @@@("src/main/resources/app.component.css")
)
class AppComponent(r: Router) {
  val router = r
}
