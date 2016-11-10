import {Component, Input} from "@angular/core";
import {SocketService} from "../communication/socket.service";
import {SimulationMessage} from "./simulation.model";

@Component({
  selector: 'simulation-item',
  template: `
    <form>
      <div class="message" [ngStyle]="{'border-color': simulation.color}">
        <strong class="message-type">{{simulation.name}}</strong>
        <button class="toggle" (click)="this.simulation.opened = !this.simulation.opened"> > </button>
        <textarea *ngIf="simulation.opened" [(ngModel)]="simulation.json" rows="6" cols="60" [ngModelOptions]="{standalone: true}"></textarea>
        <button class="btn" *ngIf="!simulation.isCommand" (click)="socketService.pushToServer(simulation.json)">To rabbit</button>
        <button class="btn blue" *ngIf="!simulation.isCommand" (click)="socketService.pushToServer(simulation.json, true)">To dashboard</button>
        <button class="btn red" *ngIf="simulation.isCommand" (click)="socketService.commandToServer(simulation.json)">Command</button>
      </div>
    </form>
  `,
  styles: [require('./simulation-item.component.scss')]
})
export class SimulationItemComponent {

  @Input()
  simulation:SimulationMessage;

  constructor(public socketService:SocketService) {
  }
}
