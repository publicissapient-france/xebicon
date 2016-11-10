import {Injectable} from "@angular/core";
import {Observable} from "rxjs";

@Injectable()
export class ProgressEmulation {
  constructor() {
  }

  emulate(step: number = 8, delay: number = 100): Observable<number> {

    return Observable.create(observer => {
      let i: number = 0;

      const next = () => {
        i += Math.round(Math.random() * step);
        observer.next(i);

        if (i < 100) {
          setTimeout(next, delay);
        } else {
          setTimeout(() => observer.complete(), 200);
        }
      };

      setTimeout(next, 1000);

      return () => console.log('disposed')
    });

  }
}
