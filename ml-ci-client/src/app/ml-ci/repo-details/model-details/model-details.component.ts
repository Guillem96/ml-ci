import { Component, OnInit, Input } from '@angular/core';
import { Model } from 'src/app/shared/models/model';

@Component({
  selector: 'app-model-details',
  templateUrl: './model-details.component.html',
  styleUrls: ['./model-details.component.scss']
})
export class ModelDetailsComponent implements OnInit {

  @Input() model: Model;
  
  constructor() { }

  ngOnInit() {
  }

}
