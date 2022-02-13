import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RoomService } from '../service/room.service';

import { RoomComponent } from './room.component';

describe('Room Management Component', () => {
  let comp: RoomComponent;
  let fixture: ComponentFixture<RoomComponent>;
  let service: RoomService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RoomComponent],
    })
      .overrideTemplate(RoomComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RoomComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RoomService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.rooms?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
