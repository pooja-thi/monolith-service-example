import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FloorComponent } from './list/floor.component';
import { FloorDetailComponent } from './detail/floor-detail.component';
import { FloorUpdateComponent } from './update/floor-update.component';
import { FloorDeleteDialogComponent } from './delete/floor-delete-dialog.component';
import { FloorRoutingModule } from './route/floor-routing.module';

@NgModule({
  imports: [SharedModule, FloorRoutingModule],
  declarations: [FloorComponent, FloorDetailComponent, FloorUpdateComponent, FloorDeleteDialogComponent],
  entryComponents: [FloorDeleteDialogComponent],
})
export class FloorModule {}
