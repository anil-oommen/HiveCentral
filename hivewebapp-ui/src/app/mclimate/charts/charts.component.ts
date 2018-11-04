import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { Observable , timer} from 'rxjs';
import {ChartingService} from '../_services/index';

@Component({
  selector: 'mcli-charts',
  templateUrl: './charts.component.html',
  styleUrls: ['./charts.component.css']
})
export class ChartsComponent implements OnInit {

  chartData = []; 

  tempHumidityData = {  
    "type":"TimeSeries",
    "intervalFrequency":"5mins",
    "data":[  
       { "temperature": 30,
          "humidity": 70,
         "dt":1485717216
       }
   ]
 };

 constructor(private chartService: ChartingService) { }
 //onstructor() { }

  ngOnInit() { 
    /* Timer for Regular Sync with Server */
    let timerChartReload = timer(1000,60000);
    timerChartReload.subscribe(
      t=>this.chartService.triggerChartDataFromCentral() //async data will not available immeditly
    );

    this.chartService.getHvDataRefreshObserve().subscribe(
      (dataRefreshed:string)=>{
        this.chartTypePendingChange = 0;
        this.tempHumidityData = this.chartService.getSensorChartData();
        this.redrawChartCanvas();
      })
    this.setChartType1Hour();
  }

  chartTypeCurrent =0;
  chartTypePendingChange = 0;

  setChartType1Hour(){
    this.chartTypeCurrent =1; this.chartTypePendingChange=1;
    this.chartService.paramFlashbackMinutes=60; 
    this.chartService.paramIntervalMinutes=3;
    this.chartService.triggerChartDataFromCentral(); //async data will not available immeditly
  }

  setChartType6Hours(){
    this.chartTypeCurrent =2; this.chartTypePendingChange=2;
    this.chartService.paramFlashbackMinutes=360; 
    this.chartService.paramIntervalMinutes=15;
    this.chartService.triggerChartDataFromCentral(); //async data will not available immeditly
  }
  
  setChartType24Hour(){
    this.chartTypeCurrent =3; this.chartTypePendingChange=3;
    this.chartService.paramFlashbackMinutes=60*24; 
    this.chartService.paramIntervalMinutes=30;
    this.chartService.triggerChartDataFromCentral(); //async data will not available immeditly
  }

  setChartType30Mins(){
    this.chartTypeCurrent =4; this.chartTypePendingChange=4;
    this.chartService.paramFlashbackMinutes=30; 
    this.chartService.paramIntervalMinutes=1;
    this.chartService.triggerChartDataFromCentral(); //async data will not available immeditly
  }


  redrawChartCanvas(){
    let temp_list = this.tempHumidityData['data'].map(res => res.temperature);
    let humd_list = this.tempHumidityData['data'].map(res => res.humidity);
    let tseries_list = this.tempHumidityData['data'].map(res => res.dt);

    let tseriesChartData = []
    tseries_list.forEach((res) => {
        let jsdate = new Date(res * 1000)
        //tseriesChartData.push(jsdate.toLocaleTimeString('en', { year: 'numeric', month: 'short', day: 'numeric' }))
        tseriesChartData.push(jsdate.toLocaleTimeString('en',{hour: '2-digit', minute:'2-digit'}))
    });

    this.chartData = new Chart('canvas', {
      type: 'line',
      data: {
        labels: tseriesChartData,
        datasets: [
          { 
            label: "Temperature",
            data: temp_list,
            borderColor: "#3cba9f",
            fill: true,
            yAxisID: "y-axis-1",
          },
          { 
            label: "Humidity",
            data: humd_list,
            borderColor: "#ffcc00",
            fill: false,
            yAxisID: "y-axis-2"
          },
        ]
      },
      options: {
        legend: {
          display: true
        },
        scales: {
          xAxes: [{
            display: true,
            text:'Chart.js Line Chart - Multi Axis'
          }],
          yAxes: [{
            type: "linear", 
            display: true,
            position: "left",
            id: "y-axis-1",
            stepSize:1,
            ticks: {
              suggestedMin: 18,
              suggestedMax: 40
            }
          },{
            type: "linear", 
            display: true,
            position: "right",
            id: "y-axis-2",
            stepSize:1,
            ticks: {
              suggestedMin: 50,
              suggestedMax: 100
            }
          }],
        }
      }
    });

  }

}
