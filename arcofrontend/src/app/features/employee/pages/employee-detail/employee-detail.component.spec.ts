import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, expect, it, vi } from 'vitest';
import { EmployeeDetailComponent } from './employee-detail.component';
import { EmployeeService } from '../../services/employee.service';
import { EmploymentPeriodService } from '../../services/employment-period.service';
import { LocationService } from '../../services/location.service';
import { UserService } from '../../../identity-access/services/user.service';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { EmployeeDetails, EmploymentPeriodDetails } from '../../models/employee.model';

describe('EmployeeDetailComponent', () => {
  const sampleEmployee: EmployeeDetails = {
    employeeId: 'emp-1',
    firstName: 'Roberto',
    lastName: 'Díaz',
    email: 'roberto@arcoymetal.com.sv',
    phoneNumber: '76667777',
    gender: 'MALE',
    birthDate: '1988-11-05',
    addressLine: 'Pasaje Central #8',
    districtId: 'AH0101',
    dui: '077777771',
    nit: null,
    isss: null,
    nup: null,
    pensionFundInstitution: 'AFP_CONFIA',
    emergencyContactName: 'María Díaz',
    emergencyContactPhone: '71112222',
    employmentStatus: 'ACTIVE',
    currentJobTitle: 'Soldador Senior',
    createdAt: '2026-11-24T12:00:00Z',
  };

  const activeHistory: EmploymentPeriodDetails[] = [
    {
      employmentPeriodId: 'ep-1',
      employeeId: 'emp-1',
      startDate: '2026-01-15',
      endDate: null,
      status: 'ACTIVE',
      terminationReason: null,
      changes: [
        {
          employmentChangeId: 'chg-1',
          jobTitle: 'Soldador de Estructuras',
          contractType: 'FULL_TIME',
          baseSalary: 650.5,
          effectiveFrom: '2026-01-15',
          createdAt: '2026-01-15T12:00:00Z',
        },
        {
          employmentChangeId: 'chg-latest',
          jobTitle: 'Soldador Senior',
          contractType: 'FULL_TIME',
          baseSalary: 780.0,
          effectiveFrom: '2026-05-01',
          createdAt: '2026-05-01T12:00:00Z',
        },
      ],
    },
  ];

  const mockHierarchy = [
    {
      id: 'AH',
      name: 'Ahuachapán',
      municipalities: [
        {
          id: 'AH01',
          name: 'Ahuachapán Norte',
          districts: [{ id: 'AH0101', name: 'Atiquizaya' }],
        },
      ],
    },
  ];

  it('pre-selects location selects, displays active employment, registers change and terminates period', () => {
    const getEmployeeHistory = vi.fn().mockReturnValue(of(activeHistory));
    const registerChange = vi.fn().mockReturnValue(of({ employmentChangeId: 'chg-2' }));
    const removeChange = vi.fn().mockReturnValue(of(void 0));
    const terminatePeriod = vi.fn().mockReturnValue(of({ employmentPeriodId: 'ep-1' }));

    TestBed.configureTestingModule({
      imports: [EmployeeDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => 'emp-1' } } },
        },
        {
          provide: EmployeeService,
          useValue: {
            getEmployeeById: () => of(sampleEmployee),
            updateEmployee: () => of({ employeeId: 'emp-1' }),
          },
        },
        {
          provide: EmploymentPeriodService,
          useValue: {
            getEmployeeHistory,
            createEmploymentPeriod: vi.fn(),
            registerChange,
            removeChange,
            terminatePeriod,
          },
        },
        {
          provide: AuthService,
          useValue: {
            isAdmin: () => true,
          },
        },
        {
          provide: UserService,
          useValue: {
            getUserByEmployeeId: () =>
              of({
                id: 'usr-1',
                employeeId: 'emp-1',
                email: 'roberto.user@arcoymetal.com.sv',
                roles: ['MANAGER'],
                status: 'ACTIVE',
                createdAt: '2026-11-24T12:00:00Z',
              }),
          },
        },
        {
          provide: LocationService,
          useValue: {
            getHierarchy: () => of(mockHierarchy),
            resolveDistrict: () => ({
              department: mockHierarchy[0],
              municipality: mockHierarchy[0].municipalities[0],
              district: mockHierarchy[0].municipalities[0].districts[0],
            }),
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(EmployeeDetailComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;

    // 1. Active employment explicitly picks the change with greatest effectiveFrom (even if not at index 0)
    expect(component.activePeriod()?.employmentPeriodId).toBe('ep-1');
    expect(component.currentEmployment()?.jobTitle).toBe('Soldador Senior');
    expect(component.currentEmployment()?.baseSalary).toBe(780);

    // 1b. Bug 1: linkedUser hides "+ Usuario" and renders the linked account email badge
    expect(fixture.nativeElement.textContent).toContain('Cuenta: roberto.user@arcoymetal.com.sv');
    expect(fixture.nativeElement.textContent).not.toContain('+ Usuario');

    // 1c. Bug 3 & A2: only latest non-initial change can be removed, and salary delta is computed
    expect(component.canRemoveChange(activeHistory[0], activeHistory[0].changes[0])).toBe(false);
    expect(component.canRemoveChange(activeHistory[0], activeHistory[0].changes[1])).toBe(true);
    const delta = component.getSalaryDelta(activeHistory[0], activeHistory[0].changes[1]);
    expect(delta?.diff).toBe(129.5);
    component.onRemoveChange('ep-1', 'chg-latest');
    expect(removeChange).toHaveBeenCalledWith('ep-1', 'chg-latest');

    // 2. Edit form pre-selects department, municipality, and district in the DOM
    component.toggleEditMode();
    fixture.detectChanges();
    expect((fixture.nativeElement.querySelector('#edit-dept') as HTMLSelectElement).value).toBe('AH');
    expect((fixture.nativeElement.querySelector('#edit-mun') as HTMLSelectElement).value).toBe('AH01');
    expect((fixture.nativeElement.querySelector('#edit-dist') as HTMLSelectElement).value).toBe('AH0101');

    // 3. Bug 2: Register employment change renders all 5 contract type radio labels in DOM
    component.togglePanel('change');
    fixture.detectChanges();
    const changePanelText = fixture.nativeElement.textContent;
    expect(changePanelText).toContain('Tiempo Completo (Planilla)');
    expect(changePanelText).toContain('Medio Tiempo');
    expect(changePanelText).toContain('Servicios Profesionales / Contratista');
    expect(changePanelText).toContain('Pasantía');
    expect(changePanelText).toContain('Temporal / Por Obra');

    component.registerChangeForm.setValue({
      effectiveFrom: '2026-06-01',
      jobTitle: 'Maestro Soldador',
      contractType: 'FULL_TIME',
      baseSalary: '800.00',
    });
    component.onRegisterChange();

    expect(registerChange).toHaveBeenCalledWith('ep-1', {
      effectiveFrom: '2026-06-01',
      jobTitle: 'Maestro Soldador',
      contractType: 'FULL_TIME',
      baseSalary: 800,
    });

    // 4. Terminate active period
    component.togglePanel('terminate');
    component.terminateForm.setValue({
      endDate: '2026-12-31',
      terminationReason: 'MUTUAL_AGREEMENT',
    });
    component.onTerminatePeriod();

    expect(terminatePeriod).toHaveBeenCalledWith('ep-1', {
      endDate: '2026-12-31',
      terminationReason: 'MUTUAL_AGREEMENT',
    });
  });

  it('handles 409 conflict when creating an overlapping period', () => {
    const createEmploymentPeriod = vi.fn().mockReturnValue(
      throwError(() => ({
        status: 409,
        error: {
          timestamp: '2026-11-24T12:00:00Z',
          status: 409,
          message: 'Start date 2026-01-15 overlaps with a previous employment period',
          errors: null,
        },
      }))
    );

    TestBed.configureTestingModule({
      imports: [EmployeeDetailComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => 'emp-1' } } },
        },
        {
          provide: EmployeeService,
          useValue: {
            getEmployeeById: () => of(sampleEmployee),
            updateEmployee: () => of({ employeeId: 'emp-1' }),
          },
        },
        {
          provide: EmploymentPeriodService,
          useValue: {
            getEmployeeHistory: () => of([]),
            createEmploymentPeriod,
            registerChange: vi.fn(),
            removeChange: vi.fn(),
            terminatePeriod: vi.fn(),
          },
        },
        {
          provide: AuthService,
          useValue: {
            isAdmin: () => false,
          },
        },
        {
          provide: UserService,
          useValue: {
            getUserByEmployeeId: () => throwError(() => ({ status: 404 })),
          },
        },
        {
          provide: LocationService,
          useValue: {
            getHierarchy: () => of(mockHierarchy),
            resolveDistrict: () => null,
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(EmployeeDetailComponent);
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.togglePanel('period');
    component.employmentPeriodForm.setValue({
      startDate: '2026-01-15',
      jobTitle: 'Soldador',
      contractType: 'FULL_TIME',
      baseSalary: '600.00',
    });
    component.onCreateEmploymentPeriod();

    expect(component.actionError()).toBe(
      'Start date 2026-01-15 overlaps with a previous employment period'
    );
  });
});
