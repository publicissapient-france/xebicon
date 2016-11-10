import {Injectable} from "@angular/core";

@Injectable()
export class HotDeploymentService {

  constructor(){
  }

  // convert 0..255 R,G,B values to a hexidecimal color string
  RGBToHex (r,g,b):any{
    var bin = r << 16 | g << 8 | b;
    return '#'+(function(h){
      return new Array(7-h.length).join("0")+h
    })(bin.toString(16).toUpperCase())
  }

}
