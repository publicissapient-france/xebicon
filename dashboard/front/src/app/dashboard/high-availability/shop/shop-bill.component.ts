import {Component, Input, OnChanges} from '@angular/core';
import {Shop} from "../high-availability.model";

@Component({
  selector: 'shop-bill',
  templateUrl: 'shop-bill.component.html',
  styles: [require('./shop-bill.component.scss')]
})
export class ShopBillComponent implements OnChanges {
  @Input() data: Shop;

  ngOnChanges() {
    // this.items = this.data.items.map(item => Object.assign({}, item, {label: this.shopLabels[item.type] || item.type}));
    // this.trainLabel = this.trainLabels[this.data.train] || 'Train';
  }
}
