import {Component, AfterContentInit} from "@angular/core";
import {ProgressEmulation} from "./progress-emulation.service";

@Component({
  selector: 'blog-publish',
  template: `
    <div class="github-publication">
      <publish-message title="Publication des articles" color="#6a205f" icon="../../../../assets/img/xebia-blog.png"></publish-message>
      <progress-bar title="http://blog.xebia.fr" [done]="progress" color="#6a205f"></progress-bar>
      <div class="images" [ngClass]="{'show': show}">
        <img src="../../../../assets/img/blog-1.png" alt=""/>
        <img src="../../../../assets/img/blog-2.png" alt=""/>
      </div>
    </div>
  `,
  styles: [require('./publish.component.scss')]
})
export class BlogPublishComponent implements AfterContentInit {
  private progress: number = 0;
  private show: boolean = false;

  constructor(private progressEmulation: ProgressEmulation) {
  }

  ngAfterContentInit(): void {
    this.start();
  }

  private start(): void {
    this.progressEmulation.emulate(4, 50)
      .subscribe(data => {
        this.progress = data;
      }, () => {
      }, () => {
        this.show = true;
      });
  }
}
