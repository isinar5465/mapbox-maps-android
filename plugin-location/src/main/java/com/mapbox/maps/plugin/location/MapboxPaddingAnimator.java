package com.mapbox.maps.plugin.location;

import android.animation.TypeEvaluator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.mapbox.maps.plugin.location.listeneres.CancelableCallback;

/**
 * Mapbox animator for padding property.
 */
public class MapboxPaddingAnimator extends MapboxAnimator<double[]> {

    MapboxPaddingAnimator(@NonNull @Size(min = 2) double[][] values,
                          @NonNull AnimationsValueChangeListener<double[]> updateListener,
                          @Nullable CancelableCallback cancelableCallback) {
        super(values, updateListener, Integer.MAX_VALUE);
        addListener(new MapboxAnimatorListener(cancelableCallback));
    }

    @NonNull
    @Override
    TypeEvaluator<double[]> provideEvaluator() {
        return new PaddingEvaluator();
    }
}