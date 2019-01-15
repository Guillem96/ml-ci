import { Component, OnInit, Input } from '@angular/core';
import { Model } from 'src/app/shared/models/model';

@Component({
  selector: 'app-model-details',
  templateUrl: './model-details.component.html',
  styleUrls: ['./model-details.component.scss']
})
export class ModelDetailsComponent implements OnInit {

  @Input() model: Model;
  
  public statusIcon = {
    pendent: 'far fa-circle',
    none: 'far fa-circle',
    error: 'fas fa-times',
    trained: 'far fa-check-circle',
    training: 'far fa-clock fa-spin'
  }

  constructor() { }

  ngOnInit() {
  }

}
