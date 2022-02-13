import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFloor } from '../floor.model';
import { FloorService } from '../service/floor.service';

@Component({
  templateUrl: './floor-delete-dialog.component.html',
})
export class FloorDeleteDialogComponent {
  floor?: IFloor;

  constructor(protected floorService: FloorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.floorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
