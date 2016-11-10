import {Component} from "@angular/core";

@Component({
  selector: 'publication',
  template: `
    <github-publish (finished)="blog = true" [ngClass]="{hide: blog}"></github-publish>
    <blog-publish *ngIf="blog"></blog-publish>
  `,
  styles: [require('./publication.component.scss')]
})
export class PublicationComponent {
  private blog: boolean = false;
}
