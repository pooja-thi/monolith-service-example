import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRoom, Room } from '../room.model';
import { RoomService } from '../service/room.service';
import { IFloor } from 'app/entities/floor/floor.model';
import { FloorService } from 'app/entities/floor/service/floor.service';

@Component({
  selector: 'jhi-room-update',
  templateUrl: './room-update.component.html',
})
export class RoomUpdateComponent implements OnInit {
  isSaving = false;

  floorsSharedCollection: IFloor[] = [];

  editForm = this.fb.group({
    id: [],
    mediaURL: [],
    floor: [],
  });

  constructor(
    protected roomService: RoomService,
    protected floorService: FloorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ room }) => {
      this.updateForm(room);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const room = this.createFromForm();
    if (room.id !== undefined) {
      this.subscribeToSaveResponse(this.roomService.update(room));
    } else {
      this.subscribeToSaveResponse(this.roomService.create(room));
    }
  }

  trackFloorById(index: number, item: IFloor): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRoom>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(room: IRoom): void {
    this.editForm.patchValue({
      id: room.id,
      mediaURL: room.mediaURL,
      floor: room.floor,
    });

    this.floorsSharedCollection = this.floorService.addFloorToCollectionIfMissing(this.floorsSharedCollection, room.floor);
  }

  protected loadRelationshipsOptions(): void {
    this.floorService
      .query()
      .pipe(map((res: HttpResponse<IFloor[]>) => res.body ?? []))
      .pipe(map((floors: IFloor[]) => this.floorService.addFloorToCollectionIfMissing(floors, this.editForm.get('floor')!.value)))
      .subscribe((floors: IFloor[]) => (this.floorsSharedCollection = floors));
  }

  protected createFromForm(): IRoom {
    return {
      ...new Room(),
      id: this.editForm.get(['id'])!.value,
      mediaURL: this.editForm.get(['mediaURL'])!.value,
      floor: this.editForm.get(['floor'])!.value,
    };
  }
}
