import { IRoom } from 'app/entities/room/room.model';
import { IProject } from 'app/entities/project/project.model';

export interface IFloor {
  id?: number;
  level?: number;
  rooms?: IRoom[] | null;
  project?: IProject | null;
}

export class Floor implements IFloor {
  constructor(public id?: number, public level?: number, public rooms?: IRoom[] | null, public project?: IProject | null) {}
}

export function getFloorIdentifier(floor: IFloor): number | undefined {
  return floor.id;
}
