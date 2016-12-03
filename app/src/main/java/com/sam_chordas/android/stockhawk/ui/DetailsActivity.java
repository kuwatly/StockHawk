package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.animation.easing.BounceEase;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by iyadkuwatly on 11/27/16.
 */

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_ID = 1;
    private String mSymbol;
    private Cursor mCursor;
    private LineChartView mChart;
    private LineSet mLineSet;
    private int mMinValue;
    private int mMaxValue;
    private TextView mTextNotEnoughPoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_line_graph);
        mTextNotEnoughPoints = (TextView) findViewById(R.id.text_not_enough_points);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSymbol = extras.getString(getString(R.string.stock_symbol));
        }
        mChart = (LineChartView) this.findViewById(R.id.linechart);
        mLineSet = new LineSet();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    void buildChart() {
        mLineSet.setColor(getResources().getColor(R.color.graph_line_color));

        // Chart Data
        mChart.addData(mLineSet);

        // Chart Formatting
        mChart.setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(mMinValue, mMaxValue)
                .setXLabels(AxisRenderer.LabelPosition.NONE)
                .setLabelsColor(getResources().getColor(R.color.graph_label_color))
                .setXAxis(false)
                .setYAxis(false);

        // Chart Animation
        Animation anim = new Animation().setEasing(new BounceEase());

        mChart.show(anim);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{mSymbol},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if (null!=mCursor && mCursor.getCount() > 2) {
            mMinValue = Integer.MAX_VALUE;
            mMaxValue = Integer.MIN_VALUE;
            float price;
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                price = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
                mLineSet.addPoint("",
                        price);
                if (price > mMaxValue)
                    mMaxValue = (int) Math.ceil(price);
                if (price < mMinValue)
                    mMinValue = (int) Math.floor(price);
            }
            buildChart();
        } else {
            Toast.makeText(this, getString(R.string.not_enough_points),
                    Toast.LENGTH_SHORT).show();
            mTextNotEnoughPoints.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}


