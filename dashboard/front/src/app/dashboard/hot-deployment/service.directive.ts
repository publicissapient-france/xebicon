import {Directive, Input, Renderer, ElementRef, ContentChild, AfterContentInit} from "@angular/core";

@Directive({
  selector: 'g'
})
export class ServiceDirective {

  private versionV1:string = '#FF0000';
  private versionV2:string = '#4CAF50';

  @Input()
  public id:number;

  @ContentChild('service')
  private service:ElementRef;

  @ContentChild('version')
  private version:ElementRef;

  private interval:any;

  // private version:string = 'V1';

  private tick:boolean;

  constructor(public renderer:Renderer) {
  }

  deployVersion() {
    this.renderer.setElementAttribute(this.service.nativeElement, 'fill', this.tick ? '#ffffff' : '#ee0300');
    this.tick = !this.tick;
  }

  switchColor(color:string) {
    this.renderer.setElementAttribute(this.service.nativeElement, 'fill', color);
  }

  handleStatus(status:string, version:string) {
    // this.version = version;
    this.renderer.setText(this.version.nativeElement, version);
    if (status === 'START' && this.interval === undefined) {
      this.interval = setInterval(() => this.deployVersion(), 500);
    }
    else if (status === 'STOP' && this.interval) {
      clearInterval(this.interval);
      this.interval = undefined;
    }
    this.switchColor(version === 'V1' ? this.versionV1 : this.versionV2);
  }
}
