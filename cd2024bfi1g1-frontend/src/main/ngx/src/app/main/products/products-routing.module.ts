import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';


import { ProductsNewComponent } from './products-new/products-new.component';
import { ProductsHomeComponent } from './products-home/products-home.component';

const routes: Routes = [
  { path: '', component: ProductsHomeComponent },
  { path: 'new', component: ProductsNewComponent },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProductsRoutingModule { }
