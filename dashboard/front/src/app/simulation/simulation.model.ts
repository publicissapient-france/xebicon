export class SimulationMessage {
  public name: string;
  public color: string;
  public message: KeynoteMessage;
  public json: string;
  public opened: boolean = false;
  public isCommand: boolean = false;
}

export class KeynoteMessage {
  public type: string;
  public payload: any;

  constructor(type: string, payload: any) {
    this.type = type;
    this.payload = payload;
  }
}
