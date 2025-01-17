import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OntimizeWebModule } from 'ontimize-web-ngx';

import { ContainersRoutingModule } from './containers-routing.module';
import { ContainersHomeComponent } from './containers-home/containers-home.component';
import { ContainersNewComponent } from './containers-new/containers-new.component';
import { ContainersDetailsComponent } from './containers-details/containers-details.component';
import { LotsHomeComponent } from './lots/lots-home/lots-home.component';



@NgModule({
  declarations: [
    ContainersHomeComponent,
    ContainersNewComponent,
    ContainersDetailsComponent,
    LotsHomeComponent,
  ],
  imports: [
    CommonModule,
    OntimizeWebModule,
    ContainersRoutingModule
  ]
})
export class ContainersModule { }
