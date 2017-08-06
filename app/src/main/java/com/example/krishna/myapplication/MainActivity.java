package com.example.krishna.myapplication;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.widget.TextView;
import java.lang.Math;

import java.util.ArrayList;
import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static com.example.krishna.myapplication.R.id.graph;

public class MainActivity extends Activity implements SensorEventListener {

    SensorManager senSensorManager;
    Sensor senAccelerometer;
    private float last_x, last_y, last_z;
    public double Px,Py,Pz,pSgolay,sqrt_sg;
    private Button btnStart, btnStop, btnPlot;
    private boolean started = false;
    public ArrayList sensorData;
    private LinearLayout layout;
    private View mChart;
    private LineGraphSeries<DataPoint> seriesX, seriesY,seriesZ,seriesSgolay;
    double t= System.currentTimeMillis();
    public TextView tvX,tvY,tvZ;
    boolean enableRecord = false;
    ArrayList sensorDataX = new ArrayList();
    ArrayList sensorDataY = new ArrayList();
    ArrayList sensorDataZ = new ArrayList();
    ArrayList sensorDataSqrt= new ArrayList();
    ArrayList out=new ArrayList();
    ArrayList sgolay_h=new ArrayList();
    GraphView graph;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);


            tvX= (TextView)findViewById(R.id.x_axis);
            tvY= (TextView)findViewById(R.id.y_axis);
            tvZ= (TextView)findViewById(R.id.z_axis);



            graph = (GraphView)findViewById(R.id.graph);

            seriesX=new LineGraphSeries<DataPoint>();
            seriesX.setColor(Color.GREEN);

            seriesY=new LineGraphSeries<DataPoint>();
            seriesY.setColor(Color.RED);

            seriesZ=new LineGraphSeries<DataPoint>();
            seriesZ.setColor(Color.BLUE);


            seriesSgolay=new LineGraphSeries<DataPoint>();
            seriesSgolay.setColor(Color.CYAN);

    //        Viewport viewport=graph.getViewport();
            //      viewport.setYAxisBoundsManual(true);
            //viewport.setMinY(0);
            //viewport.setMaxY(t);
            //viewport.setScrollable(true);



            btnPlot = (Button) findViewById(R.id.btnStart);
            btnStop = (Button) findViewById(R.id.btnStop);
            btnStart = (Button) findViewById(R.id.btnClose);
    //        btnStart.setOnClickListener(this);
    //        btnStop.setOnClickListener(this);
    //        btnClose.setOnClickListener(this);
    //        btnStart.setEnabled(true);
    //        btnStop.setEnabled(false);
    //        if (sensorData == null || sensorData.size() == 0) {
    //            btnClose.setEnabled(false);
     //       }
        }

        /** Called when the activity is first created. */
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = sensorEvent.values[0];
                last_x=x;
                Px=(double)last_x;


                float y = sensorEvent.values[1];
                last_y=y;
                Py=(double)last_y;


                float z = sensorEvent.values[2];
                last_z=z;
                Pz=(double)last_z;
                double sq=((Px*Px)+(Py*Py)+(Pz*Pz));
                sqrt_sg=Math.sqrt(sq);

            }

            if(enableRecord){
                sensorDataX.add(Px);
                sensorDataY.add(Py);
                sensorDataZ.add(Pz);
                sensorDataSqrt.add(sqrt_sg);
            }

            tvX.setText(Float.toString(last_x));
            tvY.setText(Float.toString(last_y));
            tvZ.setText(Float.toString(last_z));



        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        protected void onPause() {
            super.onPause();
            senSensorManager.unregisterListener(this);
        }
        protected void onResume() {
            super.onResume();
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }


    public void plot(View view ){
        int count = sensorDataX.size();
        for(int i = 0; i<count;i++) {
           //double xdata = (double)sensorDataX.get(i);
            seriesX.appendData(new DataPoint(i,(double)sensorDataX.get(i)), true, count, false);
            seriesY.appendData(new DataPoint(i,(double)sensorDataY.get(i)),true,count,false);
            seriesZ.appendData(new DataPoint(i,(double)sensorDataZ.get(i)),true,count,false);
        }
        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);
    }

    public  void startRecord(View v){
        enableRecord = true;
        sensorDataX.clear();
        sensorDataY.clear();
        sensorDataZ.clear();
        sensorDataSqrt.clear();


    }



    public  void stopRecord(View v){
        enableRecord = false;

    }

    public ArrayList convDigi(ArrayList sensorDataSqrt,ArrayList sgolay_h){
        int sizeX=sensorDataSqrt.size();
        int sizeH=sgolay_h.size();
        int sizeZ=sizeX+sizeH-1;
        double temp;

        out.clear();
        for (int i=0;i<sizeZ;i++){
            temp=0;
            for(int j=0; j<sizeX;j++){
                if((i-j)>=0 & (i-j)<sizeH){
                    temp+=((double)sensorDataSqrt.get(j)*(double)sgolay_h.get(i-j));
                }
            }
            pSgolay=-(double)temp;
            out.add(pSgolay);

        }
        return out;

    }
    public void sgolayfilt(View v){
        double sgolaycoeff[] = {0.409069346213559,0.315163015352769,0.233655338968432,0.163791616353286,0.104817146800069,0.0559772296015181,
                0.0165171640503709,-0.0143177505606348,-0.0372822149387614,-0.0531309297912713,-0.0626185958254269,-0.0664999137484906,
                -0.0655295842677247,-0.0604623080903916,-0.0520527859237537,-0.0410557184750734,-0.0282258064516129,-0.0143177505606348,-8.62515094014414,
                0.0137139899948249,0.0263282732447818,0.0370018975332068,0.0449801621528376,0.0495083663964119,0.0498318095566672,0.0451957909263412,0.0348456097981715,0.0180265654648957,-0.00601604278074864,-0.0380369156460237,-0.0787907538381921
        };
        sgolay_h.clear();
        for(int k=0;k<sgolaycoeff.length;k++){
            sgolay_h.add(sgolaycoeff[k]);

        }



    }
    public void plotsgolay(View v) {
        convDigi(sensorDataSqrt,sgolay_h);
        int count = out.size();

        for (int i = 0; i < count; i++) {
            seriesSgolay.appendData(new DataPoint(i, (double) out.get(i)), true, count, false);

        }
        graph.addSeries(seriesSgolay);
    }














    /*public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnClose.setEnabled(false);
                //sensorData = new ArrayList();
                // save prev data if available
                started = true;
                Sensor accel = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                senSensorManager.registerListener(this, accel,SensorManager.SENSOR_DELAY_FASTEST);
                openChart();
                break;
            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnClose.setEnabled(true);
                started = false;
                senSensorManager.unregisterListener(this);
                layout.removeAllViews();
                openChart();

                // show data in chart
                break;
            case R.id.btnClose:

                break;
            default:
                break;
        }

    }*/

    /*private void openChart() {
        if (started==true) {

            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

            XYSeries xSeries = new XYSeries("X");
            XYSeries ySeries = new XYSeries("Y");
            XYSeries zSeries = new XYSeries("Z");

            long t = System.currentTimeMillis();
            if(started==true){
                xSeries.add( t, last_x);
                ySeries.add( t, last_y);
                zSeries.add( t, last_z);
            }

            dataset.addSeries(xSeries);
            dataset.addSeries(ySeries);
            dataset.addSeries(zSeries);

            XYSeriesRenderer xRenderer = new XYSeriesRenderer();
            xRenderer.setColor(Color.RED);
            xRenderer.setPointStyle(PointStyle.CIRCLE);
            xRenderer.setFillPoints(true);
            xRenderer.setLineWidth(1);
            xRenderer.setDisplayChartValues(false);

            XYSeriesRenderer yRenderer = new XYSeriesRenderer();
            yRenderer.setColor(Color.GREEN);
            yRenderer.setPointStyle(PointStyle.CIRCLE);
            yRenderer.setFillPoints(true);
            yRenderer.setLineWidth(1);
            yRenderer.setDisplayChartValues(false);

            XYSeriesRenderer zRenderer = new XYSeriesRenderer();
            zRenderer.setColor(Color.BLUE);
            zRenderer.setPointStyle(PointStyle.CIRCLE);
            zRenderer.setFillPoints(true);
            zRenderer.setLineWidth(1);
            zRenderer.setDisplayChartValues(false);

            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setXLabels(0);
            multiRenderer.setLabelsColor(Color.RED);
            multiRenderer.setChartTitle("t vs (x,y,z)");
            multiRenderer.setXTitle("Sensor Data");
            multiRenderer.setYTitle("Values of Acceleration");
            multiRenderer.setZoomButtonsVisible(true);
            for (int i = 0; i < sensorData.size(); i++) {

                multiRenderer.addXTextLabel(i + 1, ""
                        + ( t));
            }
            for (int i = 0; i < 12; i++) {
                multiRenderer.addYTextLabel(i + 1, ""+i);
            }

            multiRenderer.addSeriesRenderer(xRenderer);
            multiRenderer.addSeriesRenderer(yRenderer);
            multiRenderer.addSeriesRenderer(zRenderer);

            // Getting a reference to LinearLayout of the MainActivity Layout

            // Creating a Line Chart
            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                    multiRenderer);

            // Adding the Line Chart to the LinearLayout
            layout.addView(mChart);

        }
    }*/


    }



