package com.gap.bis_inspection.activity.report;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;
import com.gap.bis_inspection.util.CustomInfoWindowGoogleMap;
import com.gap.bis_inspection.util.InfoWindowData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ShowMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ArrayList<ComplaintReport> complaintReports;
    private ArrayList<String> dateReports = null;
    private LatLng zaragoza;
    private int count = 0;
    private String reportStrSummary;
    private String strDate = "";
    private String deliver = "";
    private ComplaintReport complaintReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        complaintReports = getIntent().getParcelableArrayListExtra("complaintReports");
        dateReports = (ArrayList<String>) getIntent().getSerializableExtra("dateReports");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (complaintReports != null) {
            for (int i = 0; i < complaintReports.size(); i++) {
                complaintReport = complaintReports.get(i);
                strDate = dateReports.get(i);

                boolean b = complaintReport.getDeliverIs();
                if (b) {
                    deliver = "ارسال شده";
                } else {
                    deliver = "ارسال نشده";
                }

                if (complaintReport.getReportStr() != null) {
                    if (complaintReport.getReportStr().length() > 30) {
                        reportStrSummary = complaintReport.getReportStr().substring(0, 30);
                    } else {
                        reportStrSummary = complaintReport.getReportStr();
                    }

                }

                if (complaintReport.getXLatitude() != null && complaintReport.getYLongitude() != null) {
                    double latitude = Double.parseDouble(complaintReport.getXLatitude());
                    double longitude = Double.parseDouble(complaintReport.getYLongitude());
                    LatLng barcelona = new LatLng(latitude, longitude);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(barcelona)
                            .title(reportStrSummary)
                            .icon(BitmapDescriptorFactory.fromBitmap(createStoreMarker(count + 1)));

                    InfoWindowData info = new InfoWindowData();
                    if (b) {
                        info.setImage("deliver");
                    } else if (!b) {
                        info.setImage("not_deliver");
                    }

                    info.setDate(CommonUtil.farsiNumberReplacement(strDate));

                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
                    googleMap.setInfoWindowAdapter(customInfoWindow);

                    Marker m = googleMap.addMarker(markerOptions);
                    m.setTag(info);
                    m.showInfoWindow();

                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 15));

                   /* googleMap.addMarker(new MarkerOptions().position(barcelona).title(reportStrSummary).snippet(strDate + "\n" + "وضعیت : " + deliver).icon(BitmapDescriptorFactory.fromBitmap(createStoreMarker(count + 1))));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 15));
                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            LinearLayout info = new LinearLayout(getApplicationContext());
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(getApplicationContext());
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(getApplicationContext());
                            snippet.setTextColor(Color.GRAY);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });*/
                    count += 1;
                }
            }
        }


        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (zaragoza != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 15));
        }

    }

    public Bitmap createStoreMarker(int index) {
        View markerLayout = getLayoutInflater().inflate(R.layout.store_marker_layout, null);

        ImageView markerImage = (ImageView) markerLayout.findViewById(R.id.marker_image);
        TextView markerRating = (TextView) markerLayout.findViewById(R.id.marker_text);

        boolean b = complaintReport.getDeliverIs();
        if (b) {
            markerImage.setImageResource(R.drawable.success_location);
        } else {
            markerImage.setImageResource(R.drawable.no_success);
        }

        markerRating.setText(String.valueOf(index));

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }
}
