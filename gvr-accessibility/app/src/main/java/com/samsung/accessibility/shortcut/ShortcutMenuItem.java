/*
 * Copyright 2015 Samsung Electronics Co., LTD
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.samsung.accessibility.shortcut;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderData.GVRRenderingOrder;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.accessibility.GVRAccessibilitySpeech;
import org.gearvrf.accessibility.GVRAccessibilitySpeechListener;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.samsung.accessibility.R;
import com.samsung.accessibility.focus.FocusableSceneObject;
import com.samsung.accessibility.focus.OnClickListener;
import com.samsung.accessibility.focus.OnFocusListener;
import com.samsung.accessibility.gaze.GazeCursorSceneObject;
import com.samsung.accessibility.main.Main;
import com.samsung.accessibility.scene.AccessibilityScene;
import com.samsung.accessibility.util.AccessibilityTexture;

public class ShortcutMenuItem extends FocusableSceneObject {
    private static final String TAG = ShortcutMenuItem.class.getSimpleName();
    private GVRContext gvrContext;
    private static final int IN_FOCUS_COLOR = 8570046;
    private static final int LOST_FOCUS_COLOR = 6186095;
    private static final int CLICKED_COLOR = 12631476;
    private boolean clicked;
    private GVRSceneObject icon;
    private GVRTexture iconTexture;
    private TypeItem typeItem;
    private AccessibilityTexture textures;
    private GVRAccessibilitySpeech speech;

    public ShortcutMenuItem(GVRContext gvrContext) {
        super(gvrContext);
        this.gvrContext = gvrContext;
        createRenderData();
        attachEyePointeeHolder();
        getRenderData().getMaterial().setColor(LOST_FOCUS_COLOR);
        focusAndUnFocus();
        clickEvent();
    }

    private void createRenderData() {
        GVRMesh mesh = gvrContext.loadMesh(new GVRAndroidResource(gvrContext, R.raw.circle_menu));
        GVRTexture texture = gvrContext.loadTexture(new GVRAndroidResource(gvrContext, R.drawable.circle_normal));
        textures = AccessibilityTexture.getInstance(gvrContext);
        GVRMaterial material = new GVRMaterial(gvrContext);
        GVRRenderData renderData = new GVRRenderData(gvrContext);
        renderData.setMaterial(material);
        renderData.setMesh(mesh);
        attachRenderData(renderData);
        getRenderData().getMaterial().setMainTexture(texture);
    }

    private void focusAndUnFocus() {
        setOnFocusListener(new OnFocusListener() {

            @Override
            public void lostFocus(FocusableSceneObject object) {
                if (clicked)
                    object.getRenderData().getMaterial().setColor(CLICKED_COLOR);
                else
                    object.getRenderData().getMaterial().setColor(LOST_FOCUS_COLOR);

            }

            @Override
            public void inFocus(FocusableSceneObject object) {
                if (clicked)
                    object.getRenderData().getMaterial().setColor(CLICKED_COLOR);
            }

            @Override
            public void gainedFocus(FocusableSceneObject object) {
                if (typeItem != TypeItem.EMPTY)
                    object.getRenderData().getMaterial().setColor(IN_FOCUS_COLOR);
            }
        });
    }

    public void createIcon(GVRTexture iconMenu, TypeItem typeItem) {
        if (icon != null)
            removeIcon();
        iconTexture = iconMenu;
        icon = new GVRSceneObject(gvrContext, gvrContext.createQuad(.60f, .20f), iconMenu);
        icon.getTransform().setPosition(-0f, 0.02f, -0.7f);
        icon.getTransform().rotateByAxis(-90, 1, 0, 0);
        icon.getTransform().rotateByAxisWithPivot(245, 0, 1, 0, 0, 0, 0);
        icon.getRenderData().setRenderingOrder(GVRRenderingOrder.OVERLAY);
        addChildObject(icon);
        getRenderData().getMaterial().setMainTexture(textures.getSpaceTexture());
        this.typeItem = typeItem;
    }
    
    public GVRTexture getIconTexture() {
        return iconTexture;
    }

    private void clickEvent() {
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick() {
                final GVRSceneObject wholeSceneObjects[] = gvrContext.getMainScene().getWholeSceneObjects();
                switch (typeItem) {
                case TALK_BACK:

                    talkBack();
                    break;

                case BACK:

                    back(wholeSceneObjects);
                    break;

                case ZOOM:
                    zoom();
                    break;

                case INVERTED_COLORS:
                    clickEffectMenu();
                    invertedColors();

                    break;

                case SPEECH:
                    speech();
                    break;

                case ACCESSIBILITY:
                    accessibility(wholeSceneObjects);
                    break;
                default:
                    break;

                }

            }
        });
    }

    private void speech() {
        speech = new GVRAccessibilitySpeech(gvrContext);
        speech.start(new GVRAccessibilitySpeechListener() {

            @Override
            public void onRmsChanged(float arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onBeginningOfSpeech() {
                // TODO Auto-generated method stub
            }
        });
    }

    private void talkBack() {
        AudioManager audioManager = (AudioManager) gvrContext.getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (icon.getRenderData().getMaterial().getMainTexture().equals(textures.getTalkBackLess())) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

        } else {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }
    }

    private void back(final GVRSceneObject[] wholeSceneObjects) {
        final AccessibilityScene accessibilityScene = Main.accessibilityScene;
        gvrContext.setMainScene(accessibilityScene.getMainApplicationScene());
        createIcon(textures.getAccessibilityIcon(), TypeItem.ACCESSIBILITY);
        accessibilityScene.removeSceneObject(accessibilityScene.getShortcutMenu());
        accessibilityScene.getMainApplicationScene().addSceneObject(accessibilityScene.getShortcutMenu());
        GVRSceneObject gazeCursorSceneObject = GazeCursorSceneObject.getInstance(gvrContext);
        Main.accessibilityScene.getMainCameraRig().removeChildObject(gazeCursorSceneObject);
        gvrContext.getMainScene().getMainCameraRig().addChildObject(gazeCursorSceneObject);
    }

    private void accessibility(final GVRSceneObject[] wholeSceneObjects) {
        gvrContext.getMainScene().getMainCameraRig()
                .removeChildObject(GazeCursorSceneObject.getInstance(gvrContext));
        Main.accessibilityScene.getMainCameraRig()
                .addChildObject(GazeCursorSceneObject.getInstance(gvrContext));
        createIcon(textures.getBackIcon(), TypeItem.BACK);
        Main.accessibilityScene.show();
    }

    private void invertedColors() {
        if (Main.manager.getInvertedColors().isInverted()) {
            Main.manager.getInvertedColors().turnOff(Main.accessibilityScene.getMainApplicationScene(),
                    Main.accessibilityScene);
        } else {
            Main.manager.getInvertedColors().turnOn(Main.accessibilityScene.getMainApplicationScene(),
                    Main.accessibilityScene);
        }
    }

    private void zoom() {
        try {
            if (icon.getRenderData().getMaterial().getMainTexture().equals(textures.getZoomIn())) {
                Main.manager.getZoom().zoomIn(Main.accessibilityScene.getMainApplicationScene(),
                        Main.accessibilityScene);
            } else {
                Main.manager.getZoom().zoomOut(Main.accessibilityScene.getMainApplicationScene(),
                        Main.accessibilityScene);
            }
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "Feature not supported", e);
        }
    }

    private void clickEffectMenu() {
        if (!isClicked()) {
            setClicked(true);
            getRenderData().getMaterial().setColor(CLICKED_COLOR);
        } else {
            resetClick();
        }
    }

    public void resetClick() {
        setClicked(false);
        getRenderData().getMaterial().setColor(LOST_FOCUS_COLOR);
    }

    public void removeIcon() {
        typeItem = TypeItem.EMPTY;
        icon.getRenderData().getMaterial().setMainTexture(AccessibilityTexture.getInstance(gvrContext).getEmptyIcon());
    }

    public GVRSceneObject getIcon() {
        return icon;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean isClicked() {
        return this.clicked;
    }

    public TypeItem getTypeItem() {
        return typeItem;
    }

    public void setTypeItem(TypeItem typeItem) {
        this.typeItem = typeItem;
    }

    public enum TypeItem {
        SPEECH, INVERTED_COLORS, TALK_BACK, ZOOM, EMPTY, BACK, ACCESSIBILITY
    }

}
