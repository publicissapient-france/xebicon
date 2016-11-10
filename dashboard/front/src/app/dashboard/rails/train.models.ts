export class Train {
  constructor(private _id:string, public position:string, public color:string) {
  }

  get id():string {
    return this._id;
  }
}
