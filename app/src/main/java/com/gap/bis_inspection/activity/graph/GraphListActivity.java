package com.gap.bis_inspection.activity.graph;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;

public class GraphListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_list);
        AppController application = (AppController) getApplication();

        findViewById(R.id.backIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_LINE")) {
            findViewById(R.id.card_line).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_line).setVisibility(View.GONE);
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_DRIVER")) {
            findViewById(R.id.card_driver).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_driver).setVisibility(View.GONE);
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_TARIFF")) {
            findViewById(R.id.card_tariff).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_tariff).setVisibility(View.GONE);
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_CAR")) {
            findViewById(R.id.card_car).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_car).setVisibility(View.GONE);
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_CAR_ORG")) {
            findViewById(R.id.card_public).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_public).setVisibility(View.GONE);
        }

        if (application.getPermissionMap().containsKey("ROLE_APP_GET_MNG_FLEET_CAR_PRIVATE")) {
            findViewById(R.id.card_private).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_private).setVisibility(View.GONE);
        }


        findViewById(R.id.card_tariff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent slideActivity = new Intent(GraphListActivity.this, GraphActivity.class);
                startActivity(slideActivity);
            }
        });
    }
}