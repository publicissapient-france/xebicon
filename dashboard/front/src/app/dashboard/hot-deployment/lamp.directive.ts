import {Directive, Input, Renderer, ElementRef} from "@angular/core";
import {HotDeploymentService} from "./hot-deployment.service";

@Directive({
  selector: 'polygon',
  providers:[HotDeploymentService]
})
export class LampDirective {

  @Input()
  public id:number;

  constructor(public elementRef:ElementRef, public renderer:Renderer) {
  }

  changeColor(color:number){
    this.renderer.setElementAttribute(this.elementRef.nativeElement, 'fill', 'rgba(255, 255, 0,'+color+')');
  }
}
