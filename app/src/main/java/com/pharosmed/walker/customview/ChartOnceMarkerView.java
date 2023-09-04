package com.pharosmed.walker.customview;

import android.app.Activity;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.pharosmed.walker.R;
import com.pharosmed.walker.beans.TrainDataEntity;

import java.text.MessageFormat;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class ChartOnceMarkerView extends MarkerView {

    private Activity mContext;
    private TextView tv_weight;

    public ChartOnceMarkerView(Activity context, int layoutResource) {
        super(context, layoutResource);
        mContext = context;
        tv_weight = findViewById(R.id.tv_weight);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        TrainDataEntity bean = (TrainDataEntity) e.getData();
        tv_weight.setText(MessageFormat.format("{0} kg", bean.getRealLoad()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
