import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FloorComponent } from '../list/floor.component';
import { FloorDetailComponent } from '../detail/floor-detail.component';
import { FloorUpdateComponent } from '../update/floor-update.component';
import { FloorRoutingResolveService } from './floor-routing-resolve.service';

const floorRoute: Routes = [
  {
    path: '',
    component: FloorComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FloorDetailComponent,
    resolve: {
      floor: FloorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FloorUpdateComponent,
    resolve: {
      floor: FloorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FloorUpdateComponent,
    resolve: {
      floor: FloorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(floorRoute)],
  exports: [RouterModule],
})
export class FloorRoutingModule {}
