package com.example.mycourseprojectapplication.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.TransformableNode;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;

public class ARActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName(); // this is used for debugging
    private static final double MIN_OPENGL_VERSION = 3.0;
    private ModelRenderable product_renderable;
    private ArFragment arCoreFragment;                    // declare our variables
    private boolean mUserRequestedInstall = true;
    private int count = 0;
    private TransformableNode transformableNode;
    private Session mSession;
    private ImageView imageViewWhite,imageViewBlack,imageViewGold,imageViewBlue;
    private Button scaleUp,scaleDown,reset,help,rotate;
    private AnchorNode anchorNode;
    private Anchor anchor;
    private String productName = "";
    private LinearLayout linearLayoutColors;
    private UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkDevice((this))) {    // check if the device support OpenGL 3.0 ES or higher
            return;
        }
        setContentView(R.layout.activity_a_r);
        arCoreFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment); // get the ar fragment

        assert arCoreFragment != null; // check if fragment is not null

        imageViewBlack = findViewById(R.id.imageViewARblack);
        imageViewWhite = findViewById(R.id.imageViewARWhite);
        imageViewGold = findViewById(R.id.imageViewARGold);
        imageViewBlue = findViewById(R.id.imageViewARBlue);
        help = findViewById(R.id.buttonHelpAr);
        scaleUp = findViewById(R.id.buttonIncreaseScale);
        scaleDown = findViewById(R.id.buttonDecreaseScale);
        rotate = findViewById(R.id.buttonRotate);
        reset = findViewById(R.id.buttonResetSession);
        linearLayoutColors = findViewById(R.id.linearLayoutArColors);

        userSession = new UserSession(this);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARActivity.this,ArHelpTipsActivity.class);
                startActivity(intent);
            }
        });


        arCoreFragment.setOnTapArPlaneListener(  // on tab plan found
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {


                    if (product_renderable == null) { // if renderable object is null return
                        Toast.makeText(this, "Product Renderable is null!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(count >= 1) // if renderable is more than 1 return
                    {
                        Toast.makeText(this, "Model is already placed!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(plane == null) // if plan is null return
                    {
                        Toast.makeText(this, "Plane is not found to place the object!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // else
                    ShowModel(hitResult);

                    scaleUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scaleUpModelByButton(transformableNode);
                        }
                    });
                    scaleDown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            scaleDownModelByButton(transformableNode);
                        }
                    });
                    rotate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rotateModelByButton();
                        }
                    });


                });

            try {

                productName = getIntent().getStringExtra("product_name"); // get product name from previous activity
                setReferableSource(getResources().getIdentifier(productName.replaceAll(" ", "").toLowerCase(), // set the referable source for models with product name defined in raw folder
                        "raw", getPackageName()));
                if(productName.toLowerCase().equals("q85t 4k qled tv"))
                {
                    linearLayoutColors.setVisibility(View.GONE);
                }
                else
                {
                    linearLayoutColors.setVisibility(View.VISIBLE);
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }



        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPlane();
            }
        });
        imageViewWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(productName.toLowerCase().equals("iphone 12 pro"))
                {
                    changeBackColor(1f, 1f, 1f);
                    changeColorExterior(1f, 1f, 1f);
                }
                if(productName.toLowerCase().equals("apple watch series 6"))
                {
                    changeBackColor(1f,1f,1f);
                }

            }
        });
        imageViewBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(productName.toLowerCase().equals("iphone 12 pro")) {
                        changeBackColor(0.007807f, 0.113035f, 0.214017f);
                        changeColorExterior(0.007807f, 0.113035f, 0.214017f);
                    }
                    if(productName.toLowerCase().equals("apple watch series 6"))
                    {
                        changeBackColor(0.007807f, 0.113035f, 0.214017f);
                    }




            }
        });

        imageViewGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(productName.toLowerCase().equals("iphone 12 pro"))
                {
                    changeBackColor(0.863157f, 0.760525f, 0.47932f);
                    changeColorExterior(0.863157f, 0.760525f, 0.47932f);
                }
                if(productName.toLowerCase().equals("apple watch series 6"))
                {
                    changeBackColor(0.863157f, 0.760525f, 0.47932f);
                }
            }
        });

        imageViewBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(productName.toLowerCase().equals("iphone 12 pro"))
                {
                    changeBackColor(0.095f, 0.095f, 0.095f);
                    changeColorExterior(0.095f, 0.095f, 0.095f);
                }
                if(productName.toLowerCase().equals("apple watch series 6"))
                {
                    changeBackColor(0.095f, 0.095f, 0.095f);
                }

            }
        });


    }


    private void resetPlane() {
        Intent intent = new Intent(this, ARActivity.class);
        intent.putExtra("product_name",productName);
        startActivity(intent);
        finish();

    }


    private void ShowModel(HitResult hitResult)
    {
        anchor = hitResult.createAnchor(); // create anchor points
        anchorNode = new AnchorNode(anchor); // create anchor node
        anchorNode.setParent(arCoreFragment.getArSceneView().getScene()); // set the parent as the fragment ar scene
        transformableNode = new TransformableNode(arCoreFragment.getTransformationSystem()); // create transformable node using the ar core fragment transformation system
        transformableNode.setParent(anchorNode); // set the parent of transformable node is the anchor node
        transformableNode.setRenderable(product_renderable); // pass the product renderable as the source for the node
        transformableNode.select(); // select node using gestures
        count++;
        if(!userSession.isUserPRFS_AR()) {

           return;
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    MaterialTapTargetSequence tapTargetSequence = new MaterialTapTargetSequence()
                            .addPrompt(new MaterialTapTargetPrompt.Builder(ARActivity.this)
                                    .setTarget(scaleUp)
                                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                    .setPrimaryText("Scale up")
                                    .setPrimaryTextColour(ContextCompat.getColor(ARActivity.this, R.color.textColorproduct))
                                    .setSecondaryText("Press the button to scale up the model!")
                                    .setSecondaryTextColour(ContextCompat.getColor(ARActivity.this, R.color.textColorproduct))
                                    .setIcon(R.drawable.ic_outline_add_24)
                                    .setFocalColour(ContextCompat.getColor(ARActivity.this, R.color.fullblack))
                                    .setIconDrawableColourFilter(ContextCompat.getColor(ARActivity.this, R.color.colorPrimary))
                                    .setBackgroundColour(ContextCompat.getColor(ARActivity.this, R.color.colorPrimary))
                                    .setAutoDismiss(false)
                                    .setAutoFinish(false)
                                    .setCaptureTouchEventOutsidePrompt(true)
                                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                        @Override
                                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                                prompt.dismiss();
                                            }
                                        }
                                    })
                                    .create())
                            .addPrompt(new MaterialTapTargetPrompt.Builder(ARActivity.this)
                                    .setTarget(scaleDown)
                                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                    .setPrimaryText("Scale down")
                                    .setPrimaryTextColour(ContextCompat.getColor(ARActivity.this, R.color.textColorproduct))
                                    .setSecondaryText("Press the button to scale down the model!")
                                    .setSecondaryTextColour(ContextCompat.getColor(ARActivity.this, R.color.textColorproduct))
                                    .setIcon(R.drawable.ic_baseline_remove_24)
                                    .setAutoDismiss(false)
                                    .setAutoFinish(false)
                                    .setCaptureTouchEventOutsidePrompt(true)
                                    .setFocalColour(ContextCompat.getColor(ARActivity.this, R.color.fullblack))
                                    .setIconDrawableColourFilter(ContextCompat.getColor(ARActivity.this, R.color.colorPrimary))
                                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                        @Override
                                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                                prompt.dismiss();
                                            }
                                        }
                                    })
                                    .setBackgroundColour(ContextCompat.getColor(ARActivity.this, R.color.colorPrimary)).create())
                            .addPrompt(new MaterialTapTargetPrompt.Builder(ARActivity.this)
                                    .setTarget(rotate)
                                    .setPrimaryText("Rotate")
                                    .setSecondaryText("Press the button to rotate the model")
                                    .setFocalPadding(R.dimen.dp_40)
                                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                    .setIcon(R.drawable.ic_baseline_rotate_90_degrees_ccw_24)
                                    .setPrimaryTextColour(ContextCompat.getColor(ARActivity.this, R.color.textColorproduct))
                                    .setSecondaryTextColour(ContextCompat.getColor(ARActivity.this, R.color.textColorproduct))
                                    .setFocalColour(ContextCompat.getColor(ARActivity.this, R.color.fullblack))
                                    .setAutoDismiss(false)
                                    .setAutoFinish(false)
                                    .setCaptureTouchEventOutsidePrompt(true)
                                    .setIconDrawableColourFilter(ContextCompat.getColor(ARActivity.this, R.color.colorPrimary))
                                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                        @Override
                                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                                prompt.dismiss();
                                            }
                                        }
                                    })
                                    .setBackgroundColour(ContextCompat.getColor(ARActivity.this, R.color.colorPrimary)).create())
                            .setSequenceCompleteListener(new MaterialTapTargetSequence.SequenceCompleteListener() {
                                @Override
                                public void onSequenceComplete() {

                                    Toast.makeText(ARActivity.this, "All set, You are ready to go!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    tapTargetSequence.show();
                }


            }, 1000);
        }
    }
    private void scaleUpModelByButton(TransformableNode transformableNode)
    {
        BaseTransformableNode node = arCoreFragment.getTransformationSystem().getSelectedNode();
        float currentScaleX = transformableNode.getWorldScale().x;
        float currentScaleY = transformableNode.getWorldScale().y;
        float currentScaleZ = transformableNode.getWorldScale().z;
        Vector3 position = transformableNode.getLocalPosition();
        Quaternion rotation = transformableNode.getLocalRotation();
        Node newNode =  arCoreFragment.getTransformationSystem().getSelectedNode();
        if(node != null) {
            anchorNode.removeChild(node);
            count = 0;
            newNode.setLocalScale(new Vector3(currentScaleX + 0.25f, currentScaleY + 0.25f, currentScaleZ + 0.25f)); // from 1,1,1 to 2,2,2 in 4 increment
            Log.d(TAG,new Vector3(currentScaleX + 0.25f, currentScaleY + 0.25f, currentScaleZ + 0.25f)+"");
            newNode.setLocalPosition(position);
            newNode.setLocalRotation(rotation);
            anchorNode.addChild(newNode);
            transformableNode = (TransformableNode) newNode;
            count++;
        }
        else
        {
            Toast.makeText(this, "You have to select the model to scale!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void scaleDownModelByButton(TransformableNode transformableNode)
    {
        BaseTransformableNode node = arCoreFragment.getTransformationSystem().getSelectedNode();
        float currentScaleX = transformableNode.getWorldScale().x;
        float currentScaleY = transformableNode.getWorldScale().y;
        float currentScaleZ = transformableNode.getWorldScale().z;
        Vector3 position = transformableNode.getLocalPosition();
        Quaternion rotation = transformableNode.getLocalRotation();
        Node newNode =  arCoreFragment.getTransformationSystem().getSelectedNode();
        if(node != null) {
            anchorNode.removeChild(node);
            count = 0;
            newNode.setLocalScale(new Vector3(currentScaleX - 0.25f, currentScaleY - 0.25f, currentScaleZ - 0.25f)); // from 1,1,1 to 0.8 only
            newNode.setLocalPosition(position);
            newNode.setLocalRotation(rotation);
            anchorNode.addChild(newNode);
            Log.d(TAG,new Vector3(currentScaleX - 0.25f, currentScaleY - 0.25f, currentScaleZ - 0.25f)+"");
            transformableNode = (TransformableNode) newNode;
            count++;
        }
        else
        {
            Toast.makeText(this, "You have to select the model to scale!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private void rotateModelByButton()
    {

        BaseTransformableNode node = arCoreFragment.getTransformationSystem().getSelectedNode();
        float currentRotationX = transformableNode.getWorldRotation().x;
        float currentRotationY= transformableNode.getWorldRotation().y;
        float currentRotationZ = transformableNode.getWorldRotation().z;
        float currentRotationW = transformableNode.getWorldRotation().w;
        Vector3 position = transformableNode.getLocalPosition();
        Vector3 scale = transformableNode.getLocalScale();
        Node newNode =  arCoreFragment.getTransformationSystem().getSelectedNode();
        if(node != null) {
            anchorNode.removeChild(node);
            count = 0;
            newNode.setLocalRotation(new Quaternion(currentRotationX , currentRotationY , currentRotationZ , currentRotationW));
            newNode.setLocalPosition(position);
            newNode.setLocalScale(scale);
            Log.i(TAG,new Quaternion(currentRotationX , currentRotationY , currentRotationZ , currentRotationW)+"");
            anchorNode.addChild(newNode);
            transformableNode = (TransformableNode) newNode;
            count++;
        }
        else
        {
            Toast.makeText(this, "Please select the model before rotating!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void changeColorExterior(float red,float green, float blue) {

            BaseTransformableNode node = arCoreFragment.getTransformationSystem().getSelectedNode();
            Color color = new Color();
            if (node != null) {

                if(productName.toLowerCase().equals("iphone 12 pro")) {
                    color.set(red, green, blue);
                    node.getRenderable().getMaterial(2).setFloat3("baseColorTint", color);
                    node.getRenderable().getMaterial(6).setFloat3("baseColorTint", color);
                    node.getRenderable().getMaterial(7).setFloat3("baseColorTint", color);
                }
            }
            else
            {
                Toast.makeText(this, "Please select the model before attempting to change the color!", Toast.LENGTH_SHORT).show();
                return;
            }


    }
    private void changeBackColor(float red,float green, float blue) {

        BaseTransformableNode node = arCoreFragment.getTransformationSystem().getSelectedNode();


        if (node != null) {

            if(productName.toLowerCase().equals("iphone 12 pro")) {
                Color color = new Color();
                color.set(red, green, blue);
                node.getRenderable().getMaterial(12).setFloat3("baseColorTint", color);
                node.getRenderable().getMaterial(4).setFloat3("baseColorTint", color);
                node.getRenderable().getMaterial(9).setFloat3("baseColorTint", color);
                node.getRenderable().getMaterial(3).setFloat3("baseColorTint", color);
            }
            if(productName.toLowerCase().equals("apple watch series 6"))
            {
                Color color = new Color();
                color.set(red, green, blue);
                node.getRenderable().getMaterial(1).setFloat3("baseColorTint", color);
                node.getRenderable().getMaterial(0).setFloat3("baseColorTint", color);
            }

        }
        else
        {
            Toast.makeText(this, "Please select the model before attempting to change the color!", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private boolean checkARCoreAvailability(final Activity activity) throws UnavailableArcoreNotInstalledException, UnavailableSdkTooOldException, UnavailableApkTooOldException { // method to check ARCore installed or not in the phone

        if(!checkPermissions(this)) // check for camera permission first
        {
            return false;
        }
            try {
            if (mSession == null) { // if session is null
                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    case INSTALLED:
                        // Success, create the AR session.
                        mSession = new Session(this);
                        return true;

                    case INSTALL_REQUESTED:
                        // Ensures next invocation of requestInstall() will either return
                        // INSTALLED or throw an exception.

                        mUserRequestedInstall = false;
                        return false;
                }
            }
        } catch (UnavailableUserDeclinedInstallationException | UnavailableDeviceNotCompatibleException e) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "This Application requires the latest version of ARCore" + e, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }



    public static boolean checkDevice(final Activity activity) { // method to check the device openGL version

        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Requires OpenGL ES 3.0 or higher");
            activity.finish();
            return false;
        }
        return true;
    }


    private void setReferableSource(int source) // method to set referable source
    {
        ModelRenderable.builder()
                .setSource(this, source)      // build the model
                .build()
                .thenAccept((renderable) ->
                {
                    product_renderable = renderable; // set product renderable as the renderable object
                })

                .exceptionally(  // if not able to render throw an exception and return nothing
                        throwable -> {
                            Log.e(TAG, "Unable to load renderable");
                            return null;
                        });
    }

    private boolean checkPermissions(final Activity activity) // method to check the camera permission
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }

    private void requestPermissions(final Activity activity) // method to request the camera permission
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // on permission request result
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000) // check if request code is the same
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) // if permission is granted
            {
                arCoreFragment.setOnTapArPlaneListener(
                        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                            if (product_renderable == null) {
                                return;
                            }
                            if(count >= 1)
                            {
                                return;
                            }
                            if(plane == null)                                // do the same thing as above
                            {
                                return;
                            }

                            Anchor anchor = hitResult.createAnchor();
                            AnchorNode anchorNode = new AnchorNode(anchor);
                            anchorNode.setParent(arCoreFragment.getArSceneView().getScene());
                            transformableNode = new TransformableNode(arCoreFragment.getTransformationSystem());
                            transformableNode.setParent(anchorNode);
                            transformableNode.setRenderable(product_renderable);
                            transformableNode.select();
                            count++;
                        });
            }
            else
            {
                requestPermissions(this); // otherwise request permission
            }
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(!checkARCoreAvailability(this))
            {
                return;
            }

        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSession.close();
    }
}

