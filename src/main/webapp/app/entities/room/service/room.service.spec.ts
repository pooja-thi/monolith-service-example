import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRoom, Room } from '../room.model';

import { RoomService } from './room.service';

describe('Room Service', () => {
  let service: RoomService;
  let httpMock: HttpTestingController;
  let elemDefault: IRoom;
  let expectedResult: IRoom | IRoom[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RoomService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      mediaURL: 'AAAAAAA',
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

    it('should create a Room', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Room()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Room', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          mediaURL: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Room', () => {
      const patchObject = Object.assign({}, new Room());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Room', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          mediaURL: 'BBBBBB',
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

    it('should delete a Room', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRoomToCollectionIfMissing', () => {
      it('should add a Room to an empty array', () => {
        const room: IRoom = { id: 123 };
        expectedResult = service.addRoomToCollectionIfMissing([], room);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(room);
      });

      it('should not add a Room to an array that contains it', () => {
        const room: IRoom = { id: 123 };
        const roomCollection: IRoom[] = [
          {
            ...room,
          },
          { id: 456 },
        ];
        expectedResult = service.addRoomToCollectionIfMissing(roomCollection, room);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Room to an array that doesn't contain it", () => {
        const room: IRoom = { id: 123 };
        const roomCollection: IRoom[] = [{ id: 456 }];
        expectedResult = service.addRoomToCollectionIfMissing(roomCollection, room);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(room);
      });

      it('should add only unique Room to an array', () => {
        const roomArray: IRoom[] = [{ id: 123 }, { id: 456 }, { id: 2801 }];
        const roomCollection: IRoom[] = [{ id: 123 }];
        expectedResult = service.addRoomToCollectionIfMissing(roomCollection, ...roomArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const room: IRoom = { id: 123 };
        const room2: IRoom = { id: 456 };
        expectedResult = service.addRoomToCollectionIfMissing([], room, room2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(room);
        expect(expectedResult).toContain(room2);
      });

      it('should accept null and undefined values', () => {
        const room: IRoom = { id: 123 };
        expectedResult = service.addRoomToCollectionIfMissing([], null, room, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(room);
      });

      it('should return initial array if no Room is added', () => {
        const roomCollection: IRoom[] = [{ id: 123 }];
        expectedResult = service.addRoomToCollectionIfMissing(roomCollection, undefined, null);
        expect(expectedResult).toEqual(roomCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
