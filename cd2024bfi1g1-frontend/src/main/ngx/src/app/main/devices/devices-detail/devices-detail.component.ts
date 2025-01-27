import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { Expression, FilterExpressionUtils, OTableComponent, OTranslateService } from 'ontimize-web-ngx';
import { DataAdapterUtils, LineChartConfiguration, OChartComponent } from 'ontimize-web-ngx-charts';

@Component({
  selector: 'app-devices-detail',
  templateUrl: './devices-detail.component.html',
  styleUrls: ['./devices-detail.component.css']
})
export class DevicesDetailComponent implements AfterViewInit {

  //Grafica temperatura
  @ViewChild('measurementsTemperatureTable', { static: false }) measurementsTemperatureTable: OTableComponent;
  //Grafica humedad
  @ViewChild('measurementsHumidityTable', { static: false }) measurementsHumidityTable: OTableComponent;

  @ViewChild('lineChartBasic', { static: false }) lineChart: OChartComponent;

  dataArrayTemp: any = [];
  dataArrayHum: any = [];
  chartData = [];

  chartParametersTemp: LineChartConfiguration;
  chartParametersHum: LineChartConfiguration;

  colorScheme = {
    domain: ['#1464A5', '#eeeeee', '#c5c5c5']
  };

  tempField: string = "ME_TEMP";
  humidityField: string = "ME_HUMIDITY";

  formatDate(d: number): string {
    const date = new Date(d);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    //d.toLocaleString('es-ES', {month:'numeric',day:'numeric',hour:'2-digit',minute:'2-digit'}).replace(',','');

    return `${day}/${month} ${hours}:${minutes}`;
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

  ngAfterViewInit(): void {
    const chart = this.lineChart.getChartByType();
    console.log({ chart });
  }

  createFilter(values: Array<{ attr, value }>): Expression {

    let filters: Array<Expression> = [];
    values.forEach(fil => {
      if (fil.value) {
        if (fil.attr === 'DEV_ID') {
          filters.push(FilterExpressionUtils.buildExpressionEquals('DEV_ID', fil.value));
        }
        if (fil.attr === 'ME_DATE') {
          filters.push(FilterExpressionUtils.buildExpressionMoreEqual('ME_DATE', fil.value.startDate._d.getTime()));
        }
        if (fil.attr === 'ME_DATE') {
          filters.push(FilterExpressionUtils.buildExpressionLessEqual('ME_DATE', fil.value.endDate._d.getTime()));
        }
      }
    });

    if (filters.length > 0) {
      return filters.reduce((exp1, exp2) => FilterExpressionUtils.buildComplexExpression(exp1, exp2, FilterExpressionUtils.OP_AND));
    } else {
      return null;
    }
  }

  //LLenar tablas
  fillChart(ev: any) {
    console.log({ ev });
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
