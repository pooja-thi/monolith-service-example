import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FloorDetailComponent } from './floor-detail.component';

describe('Floor Management Detail Component', () => {
  let comp: FloorDetailComponent;
  let fixture: ComponentFixture<FloorDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FloorDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ floor: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FloorDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FloorDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load floor on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.floor).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
