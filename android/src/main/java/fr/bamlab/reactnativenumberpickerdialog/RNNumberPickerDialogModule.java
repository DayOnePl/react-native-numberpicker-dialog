package fr.bamlab.reactnativenumberpickerdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;

class RNNumberPickerDialogModule extends ReactContextBaseJavaModule {
    private Context context;
    private AlertDialog.Builder dialogBuilder;

    public RNNumberPickerDialogModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "RNNumberPickerDialog";
    }

    @ReactMethod
    public void show(final ReadableMap options, final Callback onSuccess, final Callback onFailure) {
        ReadableArray values = options.getArray("values");
        if (values.size() == 0) {
            onFailure.invoke("values array must not be empty");
            return;
        }

        final NumberPicker picker = new NumberPicker(getCurrentActivity());
        picker.setMinValue(0);
        picker.setMaxValue(values.size() - 1);
        String selected = options.hasKey("selected") ?
                options.getString("selected") :
                null;

        String[] displayedValues = new String[values.size()];
        for (int i = 0; i < values.size(); ++i) {
            String value = values.getString(i);
            displayedValues[i] = value;
            if (selected != null && selected.equals(value)) {
                picker.setValue(i);
            }
        }
        picker.setDisplayedValues(displayedValues);
        picker.setWrapSelectorWheel(false);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getCurrentActivity());

        if (options.hasKey("title")) {
            dialogBuilder.setTitle(options.getString("title"));
        }

        if (options.hasKey("message")) {
            dialogBuilder.setMessage(options.getString("message"));
        }

        dialogBuilder.setView(picker)
                .setPositiveButton(options.getString("positiveButtonLabel"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onSuccess.invoke(picker.getValue());
                    }
                })
                .setNegativeButton(options.getString("negativeButtonLabel"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onSuccess.invoke(-1);
                    }
                })
                .create()
                .show();
    }
}
