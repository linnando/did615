package org.linnando.did615

import angulate2.forms.FormsModule
import angulate2.platformBrowser.BrowserModule
import angulate2.router.RouterModule
import angulate2.std._
import org.linnando.did615.genetic.{GeneticAlgorithmComponent, GeneticAlgorithmService}
import org.linnando.did615.genprogramming.{AntWorldComponent, GeneticProgrammingComponent, GeneticProgrammingService}

import scala.scalajs.js

@NgModule(
  imports = @@[BrowserModule, FormsModule] :+
    RouterModule.forRoot(@@@(
      Route(path = "", redirectTo = "/genetic", pathMatch = "full"),
      Route(path = "genetic", component = %%[GeneticAlgorithmComponent]),
      Route(path = "genprogramming", component = %%[GeneticProgrammingComponent])
    ), js.Dynamic.literal(useHash = true)),
  declarations = @@[
    AntWorldComponent,
    AppComponent,
    GeneticAlgorithmComponent,
    GeneticProgrammingComponent
  ],
  providers = @@[GeneticAlgorithmService, GeneticProgrammingService],
  bootstrap = @@[AppComponent]
)
class AppModule {
}
