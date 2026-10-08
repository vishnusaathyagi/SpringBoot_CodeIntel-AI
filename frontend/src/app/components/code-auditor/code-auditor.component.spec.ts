import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CodeAuditorComponent } from './code-auditor.component';

describe('CodeAuditorComponent', () => {
  let component: CodeAuditorComponent;
  let fixture: ComponentFixture<CodeAuditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CodeAuditorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CodeAuditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
