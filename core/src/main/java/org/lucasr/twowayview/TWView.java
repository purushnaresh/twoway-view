/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lucasr.twowayview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;

import org.lucasr.twowayview.TWLayoutManager;
import org.lucasr.twowayview.TWLayoutManager.Orientation;
import org.w3c.dom.Attr;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TWView extends RecyclerView {
    private static final String LOGTAG = "TWView";

    private static final Class<?>[] sConstructorSignature = new Class[] {
            Context.class, AttributeSet.class};

    final Object[] sConstructorArgs = new Object[2];

    public TWView(Context context) {
        this(context, null);
    }

    public TWView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TWView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.TWView, defStyle, 0);

        final String name = a.getString(R.styleable.TWView_layoutManager);
        if (!TextUtils.isEmpty(name)) {
            loadLayoutManagerFromName(context, attrs, name);
        }

        a.recycle();
    }

    private void loadLayoutManagerFromName(Context context, AttributeSet attrs, String name) {
        try {
            if (name.startsWith(".")) {
                final String packageName = context.getPackageName();
                name = packageName + name;
            }

            Class<? extends TWLayoutManager> clazz =
                    context.getClassLoader().loadClass(name).asSubclass(TWLayoutManager.class);

            Constructor<? extends TWLayoutManager> constructor =
                    clazz.getConstructor(sConstructorSignature);

            sConstructorArgs[0] = context;
            sConstructorArgs[1] = attrs;

            setLayoutManager(constructor.newInstance(sConstructorArgs));
        } catch (Exception e) {
            throw new IllegalStateException("Could not load TWLayoutManager from class: " + name, e);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof  TWLayoutManager)) {
            throw new IllegalArgumentException("TWView can only use TWLayoutManager subclasses " +
                                               "as its layout manager");
        }

        super.setLayoutManager(layout);
    }

    public Orientation getOrientation() {
        TWLayoutManager layout = (TWLayoutManager) getLayoutManager();
        return layout.getOrientation();
    }

    public void setOrientation(Orientation orientation) {
        TWLayoutManager layout = (TWLayoutManager) getLayoutManager();
        layout.setOrientation(orientation);
    }

    public int getFirstVisiblePosition() {
        TWLayoutManager layout = (TWLayoutManager) getLayoutManager();
        return layout.getFirstVisiblePosition();
    }
}
