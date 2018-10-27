package bd.edu.daffodilvarsity.classorganizer.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class TextInputAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    public TextInputAutoCompleteTextView(Context context) {
        super(context);
    }

    public TextInputAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextInputAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public CharSequence getHint() {
        // Certain test frameworks expect the actionable element to expose its hint as a label. When
        // TextInputLayout is providing our hint, retrieve it from the parent layout.
        TextInputLayout layout = getTextInputLayout();
        if ((layout != null) && layout.getHint() != null) {
            return layout.getHint();
        }
        return super.getHint();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        final InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null && outAttrs.hintText == null) {
            // If we don't have a hint and our parent is a TextInputLayout, use its hint for the
            // EditorInfo. This allows us to display a hint in 'extract mode'.
            outAttrs.hintText = getHintFromLayout();
        }
        return ic;
    }

    @Nullable
    private TextInputLayout getTextInputLayout() {
        ViewParent parent = getParent();
        while (parent instanceof View) {
            if (parent instanceof TextInputLayout) {
                return (TextInputLayout) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    @Nullable
    private CharSequence getHintFromLayout() {
        TextInputLayout layout = getTextInputLayout();
        return (layout != null) ? layout.getHint() : null;
    }

}
