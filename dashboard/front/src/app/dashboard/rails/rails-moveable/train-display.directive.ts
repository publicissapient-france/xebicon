import {Directive, Input, ElementRef, Renderer} from "@angular/core";
import {TrainDisplayService} from "./train-display.service";

@Directive({
  selector: '[train-display]',
})
export class TrainDisplayDirective {
  @Input('train-display') trainId: number = undefined;
  @Input('train-states') trainStates: Array<any> = undefined;
  @Input('train-style') trainStyleId: string = undefined;

  constructor(private element: ElementRef, private renderer: Renderer, private trainDisplayService: TrainDisplayService) {
  }

  ngOnChanges() {
    this.refreshPosition();
  }

  ngOnInit() {
    this.refreshStyle();
    this.refreshPosition();
  }

  refreshPosition() {
    var matchingState = this.trainStates && this.trainStates.filter(train => train.id === this.trainId)[0];
    if (!!matchingState) {
      var targetPosition = matchingState.position;
      var translateDef = this.trainDisplayService.buildTransformFromPosition(targetPosition);
      this.renderer.setElementAttribute(this.element.nativeElement, 'transform', translateDef);
    }
  }

  refreshStyle() {
    var currentStyle = this.trainDisplayService.findStyleForTrain(this.trainStyleId);
    this.renderer.setElementAttribute(this.element.nativeElement, 'fill', currentStyle.fill);
    this.renderer.setElementAttribute(this.element.nativeElement, 'stroke', currentStyle.strokeColor);
  }
}


