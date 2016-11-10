import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'toJS'})
export class ImmutableToJS implements PipeTransform {
  transform(value: any): any {
    return value && value.toJS();
  }
}
