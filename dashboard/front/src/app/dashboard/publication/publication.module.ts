import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {UtilsModule} from "../../utils/utils.module";
import {SharedModule} from "../shared/shared.module";
import {PublicationComponent} from "./publication.component";
import {PublishMessageComponent} from "./publish-message/publish-message.component";
import {GithubPublishComponent} from "./publish/github-publish.component";
import {ProgressEmulation} from "./publish/progress-emulation.service";
import {BlogPublishComponent} from "./publish/blog-publish.component";

@NgModule({
  declarations: [
    PublicationComponent,
    PublishMessageComponent,
    GithubPublishComponent,
    BlogPublishComponent
  ],
  imports: [
    CommonModule,
    UtilsModule,
    SharedModule
  ],
  exports: [
    PublicationComponent
  ],
  providers: [ProgressEmulation]
})
export class PublicationModule {
}
