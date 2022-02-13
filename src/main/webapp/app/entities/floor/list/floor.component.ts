import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFloor } from '../floor.model';
import { FloorService } from '../service/floor.service';
import { FloorDeleteDialogComponent } from '../delete/floor-delete-dialog.component';

@Component({
  selector: 'jhi-floor',
  templateUrl: './floor.component.html',
})
export class FloorComponent implements OnInit {
  floors?: IFloor[];
  isLoading = false;

  constructor(protected floorService: FloorService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.floorService.query().subscribe({
      next: (res: HttpResponse<IFloor[]>) => {
        this.isLoading = false;
        this.floors = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFloor): number {
    return item.id!;
  }

  delete(floor: IFloor): void {
    const modalRef = this.modalService.open(FloorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.floor = floor;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
