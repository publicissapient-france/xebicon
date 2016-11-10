import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {SharedModule} from "../shared/shared.module";
import {TwitterComponent} from "./twitter.component";
import {TweetThreadComponent} from "./tweet-thread/tweet-thread.component";
import {TweetComponent} from "./tweet/tweet.component";
import {UtilsModule} from "../../utils/utils.module";

@NgModule({
  declarations: [
    TwitterComponent,
    TweetThreadComponent,
    TweetComponent
  ],
  imports: [
    CommonModule,
    UtilsModule,
    SharedModule],
  exports: [
    TwitterComponent,
    TweetThreadComponent,
    TweetComponent
  ],
  providers: []
})
export class TwitterModule {
}
