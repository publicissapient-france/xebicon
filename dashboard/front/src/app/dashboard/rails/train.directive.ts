import {Directive, Input, Renderer, ElementRef} from "@angular/core";


@Directive({
  selector: '[train]'
})
export class TrainDirective {

  @Input('train')
  trainId: number;

  @Input('position')
  position: string;

  constructor(public render: Renderer, public elementRef: ElementRef) {
  }

  show() {
    this.render.setElementStyle(this.elementRef.nativeElement, 'display', 'inline');
  }

  hide() {
    this.render.setElementStyle(this.elementRef.nativeElement, 'display', 'none');
  }

}
