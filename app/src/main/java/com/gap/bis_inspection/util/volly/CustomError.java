package com.gap.bis_inspection.util.volly;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * Created by mahdi on 10/18/16.
 */

public class CustomError extends VolleyError {

    public ErrorResponseBean errorResponseBean;

    public CustomError(NetworkResponse response, ErrorResponseBean errorResponseBean) {
        super(response);
        this.errorResponseBean = errorResponseBean;
    }

}
