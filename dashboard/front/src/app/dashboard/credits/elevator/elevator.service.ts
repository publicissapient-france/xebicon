import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Credit} from "./elevator.model";
import {Observable} from "rxjs";

@Injectable()
export class ElevatorService {

  private creditsFile: string = 'assets/credits.json';

  constructor(private http: Http) {
  }

  loadCredits(): Observable<Credit> {
    return this.http.get(this.creditsFile)
      .map(res => res.json());
  }
}
