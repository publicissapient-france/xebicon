import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'toTime'})
export class ToTime implements PipeTransform {
  transform(value: any): any {
    return this.format(value);
  }

  format(timer) {
    var sec_num = parseInt(timer, 10);
    var hours = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);
    return minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
  }
}
