import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OntimizeWebModule } from 'ontimize-web-ngx';

import { CompaniesHomeComponent } from './companies-home/companies-home.component';
import { CompaniesRoutingModule } from './companies-routing.module';
import { CompaniesNewComponent } from './companies-new/companies-new.component';
import { CompaniesDetailsComponent } from './companies-details/companies-details.component';
import { SharedModule } from "src/app/shared/shared.module";

@NgModule({
  declarations: [
    CompaniesHomeComponent,
    CompaniesNewComponent,
    CompaniesDetailsComponent
  ],
  imports: [
    CommonModule,
    OntimizeWebModule,
    CompaniesRoutingModule,
    SharedModule
  ]
})
export class CompaniesModule { }
