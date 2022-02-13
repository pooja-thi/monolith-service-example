import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { FloorService } from '../service/floor.service';

import { FloorComponent } from './floor.component';

describe('Floor Management Component', () => {
  let comp: FloorComponent;
  let fixture: ComponentFixture<FloorComponent>;
  let service: FloorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [FloorComponent],
    })
      .overrideTemplate(FloorComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FloorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FloorService);

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
    expect(comp.floors?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
