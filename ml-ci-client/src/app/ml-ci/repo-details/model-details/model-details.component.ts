import { ApproachService } from '../../../shared/services/approach.service';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Approach } from './../../../shared/models/approach';
import { TrackedRepository } from './../../../shared/models/tracked-repository';

@Component({
  selector: 'app-model-details',
  templateUrl: './model-details.component.html',
  styleUrls: ['./model-details.component.scss']
})
export class ModelDetailsComponent implements OnInit {

  @Input() approach: Approach;
  @Input() trackedRepo: TrackedRepository;

  @Output() download = new EventEmitter<void>();

  public downloading = false;

  public statusIcon = {
    PENDENT: 'far fa-circle',
    NONE: 'far fa-circle',
    ERROR: 'fas fa-times',
    TRAINED: 'far fa-check-circle',
    TRAINING: 'far fa-clock fa-spin'
  };

  constructor(private approachService: ApproachService) { }

  ngOnInit() {
  }

  public downloadModel() {
    this.downloading = true;
    this.download.emit();

    this.approachService.downloadEvaluation(this.approach, this.trackedRepo.id).subscribe(
      (res: any) => {
        this.downloadFile(res, 'octet/stream');
      }
    );
  }

  /**
   * Method is use to download file.
   * @param data - Array Buffer data
   * @param type - type of the document.
   */
  private downloadFile(data: any, type: string) {
    const a = document.createElement('a');
    document.body.appendChild(a);
    a.style.display = 'none';

    const blob = new Blob([data], { type: type});
    const url = window.URL.createObjectURL(blob);

    a.href = url;
    a.download = `${this.approach.name}_${this.approach.id}_${this.trackedRepo.id}.csv`;
    a.click();

    window.URL.revokeObjectURL(url);
    this.downloading = false;
  }
}
