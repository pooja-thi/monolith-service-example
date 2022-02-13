import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'project',
        data: { pageTitle: 'poojaThittamaranahalliBackendtasksApp.project.home.title' },
        loadChildren: () => import('./project/project.module').then(m => m.ProjectModule),
      },
      {
        path: 'floor',
        data: { pageTitle: 'poojaThittamaranahalliBackendtasksApp.floor.home.title' },
        loadChildren: () => import('./floor/floor.module').then(m => m.FloorModule),
      },
      {
        path: 'room',
        data: { pageTitle: 'poojaThittamaranahalliBackendtasksApp.room.home.title' },
        loadChildren: () => import('./room/room.module').then(m => m.RoomModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
