import {Component, OnInit} from "@angular/core";
import {select} from "ng2-redux";
import {Observable} from "rxjs";

@Component({
  selector: 'welcome',
  template: `
    <xebicon-header></xebicon-header>
    <div>
      <img *ngIf="!videoId" [src]="imageUrl" alt="Slide"/>
      
      <youtube-player *ngIf="videoId" [videoId]="videoId"
        [width]="1450" [height]="820"
        (ready)="savePlayer($event)" (change)="onStateChange($event)">
      </youtube-player>
    </div>
  `,
  styles: [require('./welcome.component.scss')]
})
export class WelcomeComponent implements OnInit {
  static localImgPath: string = '../../../assets/img/';

  @select(['slide']) keynoteSlide$: Observable<string>;

  private imageUrl: string = '';
  private videoId: string = '';
  private player;
  private ytEvent;

  ngOnInit(): void {
    this.keynoteSlide$
      .filter(slide => slide != undefined)
      .map((slide: any) => slide.toJS())
      .subscribe(slide => {
        this.imageUrl = this.resolveImageUrl(slide.imageUrl);
        this.videoId = slide.videoId;
      });
  }

  private resolveImageUrl(url: string): string {
    if (!url) {
      return '';
    }
    return url.indexOf('http') === 0 ? url : WelcomeComponent.localImgPath + url
  }

  onStateChange(event) {
    this.ytEvent = event.data;
  }

  savePlayer(player) {
    this.player = player;
    this.player.playVideo();
  }
}
