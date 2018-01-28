package org.linnando.did615

import angulate2.forms.FormsModule
import angulate2.http.HttpModule
import angulate2.platformBrowser.BrowserModule
import angulate2.router.RouterModule
import angulate2.std._
import org.linnando.did615.genetic.{GeneticAlgorithmComponent, GeneticAlgorithmService}
import org.linnando.did615.genprogramming.{AntWorldComponent, GeneticProgrammingComponent, GeneticProgrammingService}
import org.linnando.did615.backprop.{BackPropagationComponent, BackPropagationService}

import scala.scalajs.js

@NgModule(
  imports = @@[BrowserModule, FormsModule, HttpModule] :+
    RouterModule.forRoot(@@@(
      Route(path = "", redirectTo = "/genetic", pathMatch = "full"),
      Route(path = "genetic", component = %%[GeneticAlgorithmComponent]),
      Route(path = "genprogramming", component = %%[GeneticProgrammingComponent]),
      Route(path = "backprop", component = %%[BackPropagationComponent])
    ), js.Dynamic.literal(useHash = true)),
  declarations = @@[
    AntWorldComponent,
    AppComponent,
    BackPropagationComponent,
    GeneticAlgorithmComponent,
    GeneticProgrammingComponent
  ],
  providers = @@[BackPropagationService, GeneticAlgorithmService, GeneticProgrammingService],
  bootstrap = @@[AppComponent]
)
class AppModule {
}
