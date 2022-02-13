import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFloor, Floor } from '../floor.model';

import { FloorService } from './floor.service';

describe('Floor Service', () => {
  let service: FloorService;
  let httpMock: HttpTestingController;
  let elemDefault: IFloor;
  let expectedResult: IFloor | IFloor[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FloorService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      level: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Floor', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Floor()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Floor', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          level: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Floor', () => {
      const patchObject = Object.assign({}, new Floor());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Floor', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          level: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Floor', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFloorToCollectionIfMissing', () => {
      it('should add a Floor to an empty array', () => {
        const floor: IFloor = { id: 123 };
        expectedResult = service.addFloorToCollectionIfMissing([], floor);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(floor);
      });

      it('should not add a Floor to an array that contains it', () => {
        const floor: IFloor = { id: 123 };
        const floorCollection: IFloor[] = [
          {
            ...floor,
          },
          { id: 456 },
        ];
        expectedResult = service.addFloorToCollectionIfMissing(floorCollection, floor);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Floor to an array that doesn't contain it", () => {
        const floor: IFloor = { id: 123 };
        const floorCollection: IFloor[] = [{ id: 456 }];
        expectedResult = service.addFloorToCollectionIfMissing(floorCollection, floor);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(floor);
      });

      it('should add only unique Floor to an array', () => {
        const floorArray: IFloor[] = [{ id: 123 }, { id: 456 }, { id: 58159 }];
        const floorCollection: IFloor[] = [{ id: 123 }];
        expectedResult = service.addFloorToCollectionIfMissing(floorCollection, ...floorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const floor: IFloor = { id: 123 };
        const floor2: IFloor = { id: 456 };
        expectedResult = service.addFloorToCollectionIfMissing([], floor, floor2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(floor);
        expect(expectedResult).toContain(floor2);
      });

      it('should accept null and undefined values', () => {
        const floor: IFloor = { id: 123 };
        expectedResult = service.addFloorToCollectionIfMissing([], null, floor, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(floor);
      });

      it('should return initial array if no Floor is added', () => {
        const floorCollection: IFloor[] = [{ id: 123 }];
        expectedResult = service.addFloorToCollectionIfMissing(floorCollection, undefined, null);
        expect(expectedResult).toEqual(floorCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
