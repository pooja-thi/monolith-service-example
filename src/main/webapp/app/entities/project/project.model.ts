import { IFloor } from 'app/entities/floor/floor.model';

export interface IProject {
  id?: number;
  name?: string;
  floors?: IFloor[] | null;
}

export class Project implements IProject {
  constructor(public id?: number, public name?: string, public floors?: IFloor[] | null) {}
}

export function getProjectIdentifier(project: IProject): number | undefined {
  return project.id;
}
