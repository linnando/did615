package org.linnando.did615

import angulate2.forms.FormsModule
import angulate2.platformBrowser.BrowserModule
import angulate2.router.RouterModule
import angulate2.std._
import org.linnando.did615.genetic.{GeneticAlgorithmComponent, GeneticAlgorithmService}

import scala.scalajs.js

@NgModule(
  imports = @@[BrowserModule, FormsModule] :+
    RouterModule.forRoot(@@@(
      Route(path = "", redirectTo = "/genetic", pathMatch = "full"),
      Route(path = "genetic", component = %%[GeneticAlgorithmComponent])
    ), js.Dynamic.literal(useHash = true)),
  declarations = @@[
    AppComponent,
    GeneticAlgorithmComponent
  ],
  providers = @@[GeneticAlgorithmService],
  bootstrap = @@[AppComponent]
)
class AppModule {
}
