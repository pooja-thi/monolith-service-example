import { IFloor } from 'app/entities/floor/floor.model';

export interface IRoom {
  id?: number;
  mediaURL?: string | null;
  floor?: IFloor | null;
}

export class Room implements IRoom {
  constructor(public id?: number, public mediaURL?: string | null, public floor?: IFloor | null) {}
}

export function getRoomIdentifier(room: IRoom): number | undefined {
  return room.id;
}
