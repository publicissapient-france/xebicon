import {Component, AfterContentInit, Output, EventEmitter} from "@angular/core";
import {ProgressEmulation} from "./progress-emulation.service";

@Component({
  selector: 'github-publish',
  template: `
    <div class="github-publication">
      <publish-message title="Publication des sources" color="#f59300" icon="../../../../assets/svg/github.svg"></publish-message>
      <progress-bar title="https://github.com/xebia-france/xebicon" [done]="githubProgress" color="#f59300"></progress-bar>
      <div class="images" [ngClass]="{'show': show}">
        <img src="../../../../assets/img/github-xebicon-1.png" alt=""/>
        <img src="../../../../assets/img/github-xebicon-2.png" alt=""/>
      </div>
    </div>
  `,
  styles: [require('./publish.component.scss')]
})
export class GithubPublishComponent implements AfterContentInit {
  @Output() finished = new EventEmitter();

  private githubProgress: number = 0;
  private show: boolean = false;

  constructor(private progressEmulation: ProgressEmulation) {
  }

  ngAfterContentInit(): void {
    this.start();
  }

  private start(): void {
    this.progressEmulation.emulate(4, 50)
      .subscribe(data => {
        this.githubProgress = data;
      }, () => {
      }, () => {
        this.show = true;
        setTimeout(() => this.finished.emit(), 2000);
      });
  }
}
