export class HighAvailabilityState {
  nodes: Array<KubernetesNode>;
  shops: Array<Shop>;
}

export class KubernetesNode {
  name:string;
  state:string;
  apps:Array<KubernetesApp>;
  labels:Array<KubernetesLabel>;
}

export class KubernetesApp {
  name:string
}

export class KubernetesLabel {
  name:string
}



export class Shop {
  train:number;
  items:Array<SoldItem>;
  totalPrice:number;
}


export class SoldItem {
  type:string;
  quantity:number;
}

