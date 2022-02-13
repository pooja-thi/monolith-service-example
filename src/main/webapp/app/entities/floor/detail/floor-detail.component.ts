import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFloor } from '../floor.model';

@Component({
  selector: 'jhi-floor-detail',
  templateUrl: './floor-detail.component.html',
})
export class FloorDetailComponent implements OnInit {
  floor: IFloor | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ floor }) => {
      this.floor = floor;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
