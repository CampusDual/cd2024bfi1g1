import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContainersDetailsComponent } from './containers-details/containers-details.component';
import { ContainersHomeComponent } from './containers-home/containers-home.component';
import { ContainersNewComponent } from './containers-new/containers-new.component';
import { ContainersLotsNewComponent } from './containers-lots-new/containers-lots-new.component';

export const CONTAINERS_MODULE_DECLARATIONS = [
  ContainersDetailsComponent,
  ContainersHomeComponent,
  ContainersNewComponent,
  ContainersLotsNewComponent
]

const routes: Routes = [
  { path: '', component: ContainersHomeComponent },
  { path: 'new', component: ContainersNewComponent },
  { path: ':CNT_ID', component: ContainersDetailsComponent },
  { path: ':CNT_ID/new', component: ContainersLotsNewComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ContainersRoutingModule { }
