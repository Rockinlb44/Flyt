package com.example.devin.flyt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.Button;

import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private ViewSwitcher switcher;
    private static final int REFRESH_SCREEN = 1;
    private EditText mEdit;
    private Button home_btn;
    private Button query_btn;
    private Button mAnimateButton;
    //Time in seconds
    private int mDuration = 8;
    int scale = 1;
    private double[][] map_data = {
            {41.88553,-87.63124,Color.YELLOW},
            {41.978627,-87.850517,Color.RED},
            {41.970726,-87.664189,Color.GREEN},
            {41.95131,-87.13256,Color.BLUE},
            {41.908173,-86.602900,Color.GREEN},
            {41.966475,-86.361554,Color.RED},
            {41.903982,-85.978590,Color.BLUE},
            {41.832366,-86.250568,Color.YELLOW},
            {41.635125,-85.011233,Color.RED},
            {41.670757,-83.551345,Color.YELLOW},
            {41.618935,-83.036151,Color.BLUE},
            {41.476890,-82.677660,Color.GREEN},
            {41.492188,-81.689049,Color.YELLOW},
            {41.411774,-81.862984,Color.GREEN},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapView);
        //Latitude and Longitude of Cleveland, OH
        ArcGISMap map = new ArcGISMap(Basemap.Type.IMAGERY_WITH_LABELS, map_data[0][0], map_data[0][1], 16);
        //ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 41.5, -81.8, 16);
        mMapView.setMap(map);
        for(int i=0; i< map_data.length; i++) {
            plot_points(mMapView, map_data[i][0], map_data[i][1], ((int) map_data[i][2]));
        }

        //Envelope mInitExtent = new Envelope(-9140300.201915182, 5067320.34009667,-9050028.071510509, 5112723.934898005, SpatialReference.create(102100));
        //Envelope mInitExtent = new Envelope(longToMercX(map_data[0][0]), latToMercY(map_data[0][1]),longToMercX(map_data[1][0]), latToMercY(map_data[1][1]), SpatialReference.create(102100));
        int length = map_data.length - 1;
        Point begin_point = new Point(longToMercX(map_data[0][1]), latToMercY(map_data[0][0]),SpatialReference.create(102100));
        Point end_point = new Point(longToMercX(map_data[length][1]), latToMercY(map_data[length][0]),SpatialReference.create(102100));
        Envelope mInitExtent = new Envelope(begin_point,end_point);
        //Envelope mInitExtent = new Envelope(longToMercX(map_data[length][1]),latToMercY(map_data[0][0]),longToMercX(map_data[0][1]),latToMercY(map_data[length][0]),SpatialReference.create(102100));
        Viewpoint vp = new Viewpoint(mInitExtent);
        //mMapView.setViewpoint(vp);

        switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        mEdit = (EditText)findViewById(R.id.flightNumber);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(6); //Filter to 10 characters
        mEdit .setFilters(filters);

        home_btn = (Button)findViewById(R.id.return_to_main);
        query_btn = (Button)findViewById(R.id.query_btn);
        home_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switcher.showNext();  // Switches to the next view
            }
        });
        query_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(MainActivity.this, mEdit.getText().toString(), Toast.LENGTH_LONG).show();
                switcher.showNext();  // Switches to the next view
            }
        });

        mAnimateButton = (Button)findViewById(R.id.animebutton);
        mAnimateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                for(int i=0; i< map_data.length; i++) {
                    animate_trail(map_data[i][0],map_data[i][1]);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this,mMapView){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                //Toast.makeText(getApplicationContext(),"T",Toast.LENGTH_SHORT).show();
                //askQuestion();
                DialogFragment newFragment = new questionDialogFragment();
                newFragment.show(getSupportFragmentManager(), "question");
                //Toast.makeText(MainActivity.this, mEdit.getText().toString(), Toast.LENGTH_LONG).show();
                return super.onSingleTapConfirmed(e);
            }
        });

        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.CYAN, 5);
        GraphicsOverlay overlay = new GraphicsOverlay();
        overlay.getGraphics().add(new Graphic(createPolyline(map_data), lineSymbol));
        mMapView.getGraphicsOverlays().add(overlay);
        //mMapView.setViewpointGeometryAsync(mInitExtent);
    }

    public void askQuestion() {
        DialogFragment newFragment = new questionDialogFragment();
        newFragment.show(getSupportFragmentManager(), "question");
    }

    @Override
    protected void onPause(){
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    double rMajor = 6378137; //Equatorial Radius WGS84
    double shift = Math.PI*rMajor;
    //Look into why we need a factor of ten
    //Ref on Merator Auxiliary Sphere to Latitude and Longitude http://dotnetfollower.com/wordpress/2011/07/javascript-how-to-convert-mercator-sphere-coordinates-to-latitude-and-longitude/
    public double mercXtoLongitude(double mercX){
        return mercX*180/shift;
    }

    public double mercYToLatitude(double mercY){
        double latitude = 0;
        //latitude = mercY*180/shift;
        latitude = (360.0/Math.PI) * (Math.atan(Math.exp(mercY*Math.PI/shift))-(Math.PI/4.0));
        return latitude;
    }

    //Converting between Mercator Auxiliary Sphere coordinates and Longitude and Latitude
    public double longToMercX(double longitude){
        return longitude*shift/180.0;
    }

    public double latToMercY(double latitude){
        return (shift/(Math.PI))*Math.log(Math.tan((0.5*latitude*Math.PI/180.0)+(Math.PI/4)));
    }

    public void plot_points(MapView mMapView, double lat, double lot, int col){
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(graphicsOverlay);
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, col,12);
        //MercX, MercY
        Point graphicPoint = new Point(longToMercX(lot), latToMercY(lat), SpatialReference.create(102100));
        Graphic graphic = new Graphic(graphicPoint,symbol);
        graphicsOverlay.getGraphics().add(graphic);
    }

    public void animate_trail(double lat, double lot){
        Point myPoint = new Point(longToMercX(lot), latToMercY(lat),SpatialReference.create(102100));
        Viewpoint viewpoint = new Viewpoint(myPoint,scale);
        mMapView.setViewpointAsync(viewpoint,mDuration);
        mAnimateButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
    }

    private Polyline createPolyline(double[][] map_data) {
        //[DocRef: Name=Create Polyline, Category=Fundamentals, Topic=Geometries]
        // create a Polyline from a PointCollection
        PointCollection border = new PointCollection(SpatialReference.create(102100));
        int length = map_data.length - 1;
        border.add(longToMercX(map_data[0][1]),latToMercY(map_data[0][0]));
        border.add(longToMercX(map_data[length][1]),latToMercY(map_data[length][0]));
        /*
        for(int i=0; i< map_data.length; i++) {
            border.add(longToMercX(map_data[i][1]),latToMercY(map_data[i][0]));
        }
        */
        Polyline polyline = new Polyline(border);
        //[DocRef: END]
        return polyline;
    }

}
