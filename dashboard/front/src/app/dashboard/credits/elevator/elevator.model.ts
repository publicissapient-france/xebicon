export class Team {
  public name: string;
  public logos: Array<string>;
  public persons: Array<string>;
}

export class Credit {
  public teams: Array<Team>;
}