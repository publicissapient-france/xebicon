import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {VoteComponent} from "./votes.component";
import {VotesFrequencyComponent} from "./vote-frequency/votes-frequency.component";
import {VotesFrequencyItemComponent} from "./vote-frequency/votes-frequency-item.component";
import {UtilsModule} from "../../utils/utils.module";
import {SharedModule} from "../shared/shared.module";
import {VoteTimerComponent} from "./vote-timer/vote-timer.component";
import {VotesDistributionComponent} from "./votes-distribution/votes-distribution.component";
import {VoteCounterComponent} from "./vote-counter/vote-counter.component";

@NgModule({
  declarations: [
    VoteComponent,
    VotesDistributionComponent,
    VotesFrequencyComponent,
    VotesFrequencyItemComponent,
    VoteTimerComponent,
    VoteCounterComponent
  ],
  imports: [
    CommonModule,
    UtilsModule,
    SharedModule
  ],
  exports: [
    VoteComponent,
    VotesDistributionComponent,
    VotesFrequencyComponent,
    VotesFrequencyItemComponent,
    VoteTimerComponent,
    VoteCounterComponent
  ],
  providers: []
})
export class VotesModule {
}
