import { Component, ViewChild } from '@angular/core';
import { Expression, FilterExpressionUtils, ODateRangeInputComponent, OFilterBuilderComponent, OTableComponent, OTranslateService } from 'ontimize-web-ngx';
import { DataAdapterUtils, LineChartConfiguration, OChartComponent } from 'ontimize-web-ngx-charts';

@Component({
  selector: 'app-devices-detail',
  templateUrl: './devices-detail.component.html',
  styleUrls: ['./devices-detail.component.css']
})
export class DevicesDetailComponent {

  //Grafica temperatura
  @ViewChild('measurementsTemperatureTable', { static: false }) measurementsTemperatureTable: OTableComponent;
  //Grafica humedad
  @ViewChild('measurementsHumidityTable', { static: false }) measurementsHumidityTable: OTableComponent;

  @ViewChild('lineChartBasic', { static: false }) lineChart: OChartComponent;

  @ViewChild('dates', { static: false }) dateFilter: ODateRangeInputComponent;


  dataArrayTemp: any = [];
  dataArrayHum: any = [];
  chartData = [];

  chartParametersTemp: LineChartConfiguration;
  chartParametersHum: LineChartConfiguration;

  colorSchemeTemp = {
    domain: ['#1464A5']
  };

  colorSchemeHum = {
    domain: ['#31a514']
  };

  tempField: string = "";
  humidityField: string = "";

  formatDate(d: number): string {
    const date = new Date(d);
    return date.toLocaleString('es-ES', { month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' }).replace(',', '');
  }

  constructor(
    private translator: OTranslateService
  ) {
    //Temperatura
    this.chartParametersTemp = new LineChartConfiguration();
    this.chartParametersTemp.isArea = [true];
    this.chartParametersTemp.interactive = true;
    this.chartParametersTemp.useInteractiveGuideline = false;
    this.chartParametersTemp.xAxis = "ME_DATE";
    this.chartParametersTemp.xDataType = this.formatDate;
    this.chartParametersTemp['showTooltip'] = true;

    //Humedad
    this.chartParametersHum = new LineChartConfiguration();
    this.chartParametersHum.isArea = [true];
    this.chartParametersHum.interactive = false;
    this.chartParametersHum.useInteractiveGuideline = false;
    this.chartParametersHum.xAxis = "ME_DATE";
    this.chartParametersHum.xDataType = this.formatDate;
    this.chartParametersHum['showTooltip'] = true;
  }

  createFilter(values: Array<{ attr, value }>): Expression {
    console.log('Filtros generados:', this.dateFilter);

    let filters: Array<Expression> = [];
    values.forEach(fil => {

      if (fil.value) {
        if (fil.attr === 'DEV_ID') {
          filters.push(FilterExpressionUtils.buildExpressionEquals('DEV_ID', fil.value));
        }
        
        if (this.dateFilter.getValue() != undefined) {
          console.log('Agregando filtro de rango de fechas:', fil.value);
          let endDate = new Date(this.dateFilter.getValue().endDate);
          endDate.setHours(23);
          endDate.setMinutes(59);
          endDate.setSeconds(59);
          endDate.setMilliseconds(999);
          filters.push(FilterExpressionUtils.buildExpressionMoreEqual('ME_DATE', this.dateFilter.getValue().startDate));
          filters.push(FilterExpressionUtils.buildExpressionLessEqual('ME_DATE', endDate.getTime()));
        }
      }
    });

    if (filters.length > 0) {
      return filters.reduce((exp1, exp2) => FilterExpressionUtils.buildComplexExpression(exp1, exp2, FilterExpressionUtils.OP_AND));
    } else {
      console.log("No hay filtro");
      return null;
    }
  }

  /*
  Para modificar como se muestra la fecha del tooltip cuando ponemos el raton encima de uno de los puntos, habria que 
  modificar la linea 101 donde se carga la fecha en la variable "data" para modificar el formato de fecha que se le pasa:
    "ME_DATE": (new Date(row.ME_DATE)).toISOString(),
  El problema es que rompe por completo la representacion de los datos, es decir, no se representan los saltos temporales.
  Por lo tanto de momento no se modifica hasta que el cliente decida lo contrario. Una vez arreglado eliminar este comentario 
  */

  //LLenar tablas
  fillChart(ev: any) {
    this.tempField = this.translator.get('TEMPERATURE');
    this.humidityField = this.translator.get('HUMIDITY');
    this.chartParametersTemp.yAxis = [this.tempField];
    this.chartParametersHum.yAxis = [this.humidityField];
    const data = ev.map((row) => {
      return {
        [this.tempField]: row.ME_TEMP,
        "ME_DATE": row.ME_DATE,
        [this.humidityField]: row.ME_HUMIDITY
      }
    });

    this.dataArrayTemp = DataAdapterUtils.createDataAdapter(
      this.chartParametersTemp
    ).adaptResult(data);

    this.dataArrayHum = DataAdapterUtils.createDataAdapter(
      this.chartParametersHum
    ).adaptResult(data);
  }

}
