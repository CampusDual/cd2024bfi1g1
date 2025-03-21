import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Expression, FilterExpressionUtils, OTableComponent, OTextInputComponent, OTranslateService } from 'ontimize-web-ngx';
import { DataAdapterUtils, LineChartConfiguration, OChartComponent } from 'ontimize-web-ngx-charts';
import { DateRendererPipe } from 'src/app/shared/components/pipes/date-renderer.pipe';

@Component({
  selector: 'app-alerts-details',
  templateUrl: './alerts-details.component.html',
  styleUrls: ['./alerts-details.component.css']
})
export class AlertsDetailsComponent implements OnInit {

  @ViewChild('measurementsTemperatureTable', { static: false }) measurementsTemperatureTable: OTableComponent;
  @ViewChild('measurementsHumidityTable', { static: false }) measurementsHumidityTable: OTableComponent;
  @ViewChild('lineChartBasic', { static: false }) lineChart: OChartComponent;
  @ViewChild('alertDateEnd', { static: false }) alertDateEnd: OTextInputComponent;

  dataArrayTemp: any = [];
  dataArrayHum: any = [];
  chartData = [];

  chartParametersTemp: LineChartConfiguration;
  chartParametersHum: LineChartConfiguration;

  tempField: string = "";
  humidityField: string = "";

  // Paleta de colores para dispositivos
  deviceColors = [
    '#1464A5', '#FF5733', '#FFC300', '#28A745', '#6F42C1',
    '#E74C3C', '#8E44AD', '#F39C12', '#C0392B', '#3498DB'
  ];

  colorSchemeTemp = {
    domain: ['#1464A5']
  };

  colorSchemeHum = {
    domain: ['#31a514']
  };

  // Mapa de colores asignados a dispositivos
  deviceColorMap: { [key: string]: string } = {};

  formatDate(d: number): string {
    const date = new Date(d);
    return date.toLocaleString('es-ES', { month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' }).replace(',', '');
  }

  constructor(
    private dateRenderer: DateRendererPipe,
    private translator: OTranslateService,
    private router: Router
  ) {
    // Configuración del gráfico de temperatura
    this.chartParametersTemp = new LineChartConfiguration();
    this.chartParametersTemp.isArea = [true];
    this.chartParametersTemp.interactive = true;
    this.chartParametersTemp.useInteractiveGuideline = false;
    this.chartParametersTemp.xAxis = "ME_DATE";
    this.chartParametersTemp.xDataType = this.formatDate;
    this.chartParametersTemp['showTooltip'] = true;

    // Configuración del gráfico de humedad
    this.chartParametersHum = new LineChartConfiguration();
    this.chartParametersHum.isArea = [true];
    this.chartParametersHum.interactive = false;
    this.chartParametersHum.useInteractiveGuideline = false;
    this.chartParametersHum.xAxis = "ME_DATE";
    this.chartParametersHum.xDataType = this.formatDate;
    this.chartParametersHum['showTooltip'] = true;
  }

  public rowClass = (rowData: any, rowIndex: number): string | string[] => {
    const temp = rowData.ME_TEMP;
    const minTemp = rowData.MIN_TEMP;
    const maxTemp = rowData.MAX_TEMP;

    if (temp < minTemp || temp > maxTemp) {
      return "error-row";
    }
    return '';
  }

  fillChart(ev: any) {

    this.tempField = this.translator.get('TEMPERATURE');
    this.humidityField = this.translator.get('HUMIDITY');

    this.chartParametersTemp.yAxis = [this.tempField];
    this.chartParametersHum.yAxis = [this.humidityField];

    const devicesData = {};
    const uniqueDevices = new Set();

    ev.forEach(row => {
      const devId = row.DEV_NAME;
      uniqueDevices.add(row.DEV_NAME);

      if (!devicesData[devId]) {
        devicesData[devId] = [];
      }

      devicesData[devId].push({
        name: row.DEV_NAME,
        [this.tempField]: row.ME_TEMP,
        [this.humidityField]: row.ME_HUMIDITY,
        "ME_DATE": row.ME_DATE,
      });
    });

    let colorIndex = 0;
    uniqueDevices.forEach(devId => {
      const devIdStr = String(devId);
      if (!this.deviceColorMap[devIdStr]) {
        this.deviceColorMap[devIdStr] = this.deviceColors[colorIndex % this.deviceColors.length];
        colorIndex++;
      }
    });

    this.dataArrayTemp = Object.keys(devicesData).map(devId => ({
      name: devId,
      series: devicesData[devId].map(point => ({
        name: point.ME_DATE,
        value: point[this.tempField]
      }))
    }));

    this.dataArrayHum = Object.keys(devicesData).map(devId => ({
      name: devId,
      series: devicesData[devId].map(point => ({
        name: point.ME_DATE,
        value: point[this.humidityField]
      }))
    }));

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

  ngOnInit(): void { }

  getCellData(startDate, endDate): string {
    let totalSeconds: number;

    if (!startDate) {
      return 'Fecha de inicio no disponible';
    }

    if (!endDate) {
      const now = new Date();
      totalSeconds = Math.floor((now.getTime() - new Date(startDate).getTime()) / 1000);
    } else {
      totalSeconds = Math.floor((new Date(endDate).getTime() - new Date(startDate).getTime()) / 1000);
    }

    const days = Math.floor(totalSeconds / (24 * 3600));
    const hours = Math.floor((totalSeconds % (24 * 3600)) / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const secs = totalSeconds % 60;

    const formattedTime = `${String(hours).padStart(2, '0')}h ${String(minutes).padStart(2, '0')}min ${String(secs).padStart(2, '0')}s`;

    if (days > 0) {
      return `${days}d ${formattedTime}`;
    }
    return formattedTime;
  }

  public alertDateEndVisible: string = "N/A";

  fillData(e: any) {
    if (e.ALT_DATE_END !== undefined) {
      this.alertDateEndVisible = this.dateRenderer.transform(this.alertDateEnd.getValue(), 'dd/MM/yyyy HH:mm:ss');
    }
  }
}
