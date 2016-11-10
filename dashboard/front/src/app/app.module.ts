import {NgModule, ApplicationRef} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";
import {removeNgStyles, createNewHosts} from "@angularclass/hmr";
import {ENV_PROVIDERS} from "./environment";
import {ROUTES} from "./app.routes";
import {App} from "./app.component";
import {APP_RESOLVER_PROVIDERS} from "./app.resolver";
import {NgRedux} from "ng2-redux";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {KitchenSinkComponent} from "./kitchensink/kitchen-sink.component";
import {ObstacleComponent} from "./dashboard/obstacle/obstacle.component";
import {WelcomeComponent} from "./dashboard/welcome/welcome.component";
import {ConfigurationService} from "./simulation/configuration.service";
import {SocketService} from "./communication/socket.service";
import {NoContent} from "./no-content/no-content";
import {VotesModule} from "./dashboard/votes/votes.module";
import {HighAvailabilityModule} from "./dashboard/high-availability/high-availability.module";
import {UtilsModule} from "./utils/utils.module";
import {RailsModule} from "./dashboard/rails/rails.module";
import {HotDeploymentModule} from "./dashboard/hot-deployment/hot-deployment.module";
import {SharedModule} from "./dashboard/shared/shared.module";
import {TrainDepartureModule} from "./dashboard/train-departure/train-departure.module";
import {TrainDescriptionModule} from "./dashboard/train-description/train-description.module";
import {TwitterModule} from "./dashboard/twitter/twitter.module";
import {SimulationModule} from "./simulation/simulation.module";
import {PublicationModule} from "./dashboard/publication/publication.module";
import {YoutubePlayerModule} from 'ng2-youtube-player';
import {CreditsModule} from './dashboard/credits/credits.module';
import {TrainArrivedModule} from "./dashboard/train-arrived/train-arrived.module";
/*
 * Platform and Environment providers/directives/pipes
 */
// App is our top level component

// Application wide providers
const APP_PROVIDERS = [
  ...APP_RESOLVER_PROVIDERS,
  NgRedux,
  SocketService,
  ConfigurationService,
];


// class MyExceptionHandler implements ExceptionHandler {
//   _logger;
//   _rethrowException;
//   call(error, stackTrace = null, reason = null) {
//     // do something with the exception
//     console.error('>>>>>>>>>>>>>>>>', error);
//   }
// }


/**
 * `AppModule` is the main entry point into Angular2's bootstraping process
 */
@NgModule({
  bootstrap: [App],
  declarations: [
    App,
    DashboardComponent,
    KitchenSinkComponent,
    ObstacleComponent,
    WelcomeComponent,
    NoContent
  ],
  imports: [ // import Angular's modules
    BrowserModule,
    FormsModule,
    HttpModule,
    RouterModule.forRoot(ROUTES, {useHash: true}),
    YoutubePlayerModule,
    SimulationModule,
    TwitterModule,
    VotesModule,
    HighAvailabilityModule,
    UtilsModule,
    RailsModule,
    HotDeploymentModule,
    SharedModule,
    TrainDepartureModule,
    TrainDescriptionModule,
    PublicationModule,
    CreditsModule,
    TrainArrivedModule
  ],
  exports: [],
  providers: [ // expose our Services and Providers into Angular's dependency injection
    ENV_PROVIDERS,
    APP_PROVIDERS
    //{provide: ExceptionHandler, useClass: MyExceptionHandler}
  ]
})
export class AppModule {
  constructor(public appRef: ApplicationRef) {
  }

  hmrOnInit(store) {
    if (!store || !store.state) return;
    console.log('HMR store', store);
    //this.appState.state = store.state;
    delete store.state;
  }

  hmrOnDestroy(store) {
    var cmpLocation = this.appRef.components.map(cmp => cmp.location.nativeElement);
    // recreate elements
    //var state = this.appState.state;
    //store.state = state;  // TODO Redux store
    store.disposeOldHosts = createNewHosts(cmpLocation)
    // remove styles
    removeNgStyles();
  }

  hmrAfterDestroy(store) {
    // display new elements
    store.disposeOldHosts();
    delete store.disposeOldHosts;
  }
}
