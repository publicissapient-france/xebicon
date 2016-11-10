import {Injectable} from "@angular/core";

@Injectable()
export class TrainDisplayService {
  static availableStyles:any = {
    yellow:{fill:'url(#mx-gradient-ffcd28-1-ffa500-1-s-0)', strokeColor:'#d79b00'},
    purple:{fill:'url(#mx-gradient-e1d5e7-1-8c6c9c-1-s-0)', strokeColor:'#9673a6'},
  };


  static availableLanes: Array<any> = [
    {name:'mid', availableCoordinates:[
      {stepId:2, x:168,y:42},
      {stepId:8, x:812,y:42}
    ]},
    {name:'top', availableCoordinates:[
      {stepId:1,x:0,y:0},
      //{stepId:2,x:80,y:21,r:20},
      //{stepId:4,x:248,y:2,r:-20},
      {stepId:3,x:328,y:-19},
      {stepId:4,x:408,y:-19},
      {stepId:5,x:488,y:-19},
      {stepId:6,x:568,y:-19},
      {stepId:7,x:648,y:-19},
      {stepId:9,x:972,y:0},
    ]},
    {name:'bottom', availableCoordinates:[
      {stepId:1,x:0,y:80},
      //{stepId:2,x:80,y:63,r:-20},
      //{stepId:4,x:248,y:83,r:20},
      {stepId:3,x:328,y:100},
      {stepId:4,x:408,y:100},
      {stepId:5,x:488,y:100},
      {stepId:6,x:568,y:100},
      {stepId:7,x:648,y:100},
      {stepId:9,x:972,y:80}
    ]}
  ];

  constructor(){

  }

  findStyleForTrain(trainStyleId:string){
    return TrainDisplayService.availableStyles[trainStyleId];
  }

  buildTransformFromPosition(position:string){
    var splitPosition = position.split('_');
    if(splitPosition.length!=4){
      console.error('invalid position message');
    }
    var laneId = splitPosition[1];
    var stepId = splitPosition[3];
    var matchingCoordinates = TrainDisplayService.availableLanes[laneId].availableCoordinates.filter((coord)=>{
      return coord.stepId=== +stepId;
    })[0];
    return `translate(${matchingCoordinates.x}, ${matchingCoordinates.y})`;
  }
}
