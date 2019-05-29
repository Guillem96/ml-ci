import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { TrackedRepositoryService } from 'src/app/shared/services/tracked-repository.service';
import { INT_TYPE } from '@angular/compiler/src/output/output_ast';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { MlCiLog } from 'src/app/shared/models/logs';

@Component({
  selector: 'app-repo-logs',
  templateUrl: './repo-logs.component.html',
  styleUrls: ['./repo-logs.component.scss']
})
export class RepoLogsComponent implements OnInit, OnChanges {

  @Input() trackedRepository: TrackedRepository;
  public logs: MlCiLog[] = [];

  constructor(private trackedRepositoryService: TrackedRepositoryService) { }

  ngOnInit() {
    this.fetchLogs();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['trackedRepository'].firstChange)
      this.fetchLogs();
  }

  private fetchLogs(): void {
    // Fetch logs from current buildNum
    this.trackedRepositoryService
    .getLogsByBuildNum(this.trackedRepository, this.trackedRepository.buildNum)
    .subscribe(res => {
      this.logs = res;
    });
  }

}
