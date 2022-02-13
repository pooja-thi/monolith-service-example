import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFloor, getFloorIdentifier } from '../floor.model';

export type EntityResponseType = HttpResponse<IFloor>;
export type EntityArrayResponseType = HttpResponse<IFloor[]>;

@Injectable({ providedIn: 'root' })
export class FloorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/floors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(floor: IFloor): Observable<EntityResponseType> {
    return this.http.post<IFloor>(this.resourceUrl, floor, { observe: 'response' });
  }

  update(floor: IFloor): Observable<EntityResponseType> {
    return this.http.put<IFloor>(`${this.resourceUrl}/${getFloorIdentifier(floor) as number}`, floor, { observe: 'response' });
  }

  partialUpdate(floor: IFloor): Observable<EntityResponseType> {
    return this.http.patch<IFloor>(`${this.resourceUrl}/${getFloorIdentifier(floor) as number}`, floor, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFloor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFloor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFloorToCollectionIfMissing(floorCollection: IFloor[], ...floorsToCheck: (IFloor | null | undefined)[]): IFloor[] {
    const floors: IFloor[] = floorsToCheck.filter(isPresent);
    if (floors.length > 0) {
      const floorCollectionIdentifiers = floorCollection.map(floorItem => getFloorIdentifier(floorItem)!);
      const floorsToAdd = floors.filter(floorItem => {
        const floorIdentifier = getFloorIdentifier(floorItem);
        if (floorIdentifier == null || floorCollectionIdentifiers.includes(floorIdentifier)) {
          return false;
        }
        floorCollectionIdentifiers.push(floorIdentifier);
        return true;
      });
      return [...floorsToAdd, ...floorCollection];
    }
    return floorCollection;
  }
}
