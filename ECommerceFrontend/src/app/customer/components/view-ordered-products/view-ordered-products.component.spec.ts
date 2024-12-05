import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewOrderedProductsComponent } from './view-ordered-products.component';

describe('ViewOrderedProductsComponent', () => {
  let component: ViewOrderedProductsComponent;
  let fixture: ComponentFixture<ViewOrderedProductsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewOrderedProductsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewOrderedProductsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
