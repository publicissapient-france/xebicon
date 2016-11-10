import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {ImmutableToJS} from "./immutable-to-js.pipe";
import {ToTime} from "./to-time.pipe";

@NgModule({
  declarations: [
    ImmutableToJS,
    ToTime
  ],
  imports: [CommonModule],
  exports: [
    ImmutableToJS,
    ToTime
  ],
  providers: []
})
export class UtilsModule {
}
