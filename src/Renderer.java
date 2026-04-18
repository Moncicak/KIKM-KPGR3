import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import java.io.IOException;
import java.util.Locale;

import lwjglutils.OGLBuffers;
import lwjglutils.OGLModelOBJ;
import lwjglutils.OGLTexImageFloat;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Mat4ViewRH;
import transforms.Mat4RotXYZ;
import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Vec3D;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPolygonOffset;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BORDER_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexParameterfv;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_NONE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glIsProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Renderer extends AbstractRenderer {

    private static final double CAMERA_SPEED = 3.5;
    private static final double CAMERA_FAST_SPEED = 8.0;
    private static final double MOUSE_SENSITIVITY = 0.005;
    private static final int GRID_ROWS = 120;
    private static final int GRID_COLS = 120;

    private final boolean[] pressedKeys = new boolean[GLFW_KEY_LAST + 1];
    private final FrameTimer frameTimer = new FrameTimer();
    private final CameraState cameraState = new CameraState();

    private int paramProgram;
    private int meshProgram;
    private int lightProgram;
    private int paramShadowProgram;
    private int meshShadowProgram;
    private int paramDeferredProgram;
    private int meshDeferredProgram;
    private int aoProgram;
    private int blurProgram;
    private int compositeProgram;

    private OGLBuffers gridList;
    private OGLBuffers gridStrip;
    private OGLBuffers lightMarker;
    private OGLModelOBJ elephantModel;
    private OGLTexture2D surfaceTexture;
    private OGLTexture2D shadowDepthTexture;
    private int shadowFbo;
    private OGLRenderTarget deferredTarget;
    private OGLRenderTarget aoTarget;
    private OGLRenderTarget aoBlurTarget;
    private OGLBuffers screenQuad;

    private GridTopology gridTopology = GridTopology.TRIANGLE_LIST;
    private SurfaceType surfaceType = SurfaceType.CARTESIAN_WAVE;
    private DebugView debugView = DebugView.LIT;
    private RenderMode renderMode = RenderMode.FILL;
    private boolean useAmbient = true;
    private boolean useDiffuse = true;
    private boolean useSpecular = true;
    private boolean useTexture = true;
    private boolean useDeferred = false;
    private double spotlightYaw = Math.PI;
    private double spotlightPitch = -0.65;
    private double spotlightInnerDegrees = 18.0;
    private double spotlightOuterDegrees = 28.0;

    private static final int SHADOW_SIZE = 2048;

    private boolean mouseLookActive;
    private boolean firstMouseSample = true;
    private double lastMouseX;
    private double lastMouseY;

    @Override
    public void init() {
        super.init();

        frameTimer.reset();
        cameraState.reset();

        gridList = GridMeshFactory.createGrid(GRID_ROWS, GRID_COLS, GridTopology.TRIANGLE_LIST);
        gridStrip = GridMeshFactory.createGrid(GRID_ROWS, GRID_COLS, GridTopology.TRIANGLE_STRIP);
        lightMarker = createLightMarker();

        try {
            elephantModel = new OGLModelOBJ("/obj/ElephantBody.obj");
            surfaceTexture = new OGLTexture2D("textures/bricks.jpg");
            shadowDepthTexture = new OGLTexture2D(new OGLTexImageFloat(SHADOW_SIZE, SHADOW_SIZE, new OGLTexImageFloat.FormatDepth()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load project assets", e);
        }

        paramProgram = ShaderUtils.loadProgram("shaders/parametric");
        meshProgram = ShaderUtils.loadProgram("shaders/mesh");
        lightProgram = ShaderUtils.loadProgram("shaders/light");
        paramShadowProgram = ShaderUtils.loadProgram(new String[]{"shaders/parametric", "shaders/shadow"});
        meshShadowProgram = ShaderUtils.loadProgram(new String[]{"shaders/mesh", "shaders/shadow"});
        paramDeferredProgram = ShaderUtils.loadProgram(new String[]{"shaders/parametric_gbuffer", "shaders/gbuffer"});
        meshDeferredProgram = ShaderUtils.loadProgram(new String[]{"shaders/mesh_gbuffer", "shaders/gbuffer"});
        aoProgram = ShaderUtils.loadProgram(new String[]{"shaders/screen", "shaders/ao"});
        blurProgram = ShaderUtils.loadProgram(new String[]{"shaders/screen", "shaders/blur"});
        compositeProgram = ShaderUtils.loadProgram(new String[]{"shaders/screen", "shaders/composite"});

        screenQuad = createScreenQuad();

        setupShadowMap();
        setupDeferredTargets();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_PROGRAM_POINT_SIZE);
        glCullFace(GL_BACK);
        glClearColor(0.06f, 0.07f, 0.11f, 1.0f);
    }

    @Override
    public void display() {
        frameTimer.beginFrame();
        updateCamera();

        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glPolygonMode(GL_FRONT_AND_BACK, renderMode.getPolygonMode());
        glLineWidth(2.0f);
        glPointSize(8.0f);

        Mat4 view = cameraState.getViewMatrix();
        Mat4 projection = cameraState.getProjectionMatrix(width, height);
        double time = frameTimer.getElapsedSeconds();
        Vec3D cameraPosition = cameraState.getPosition();
        Vec3D lightPosition = computeLightPosition(time);
        Mat4 lightViewProjection = computeLightViewProjection(lightPosition);

        renderShadowPass(lightViewProjection, time);

        if (useDeferred && debugView == DebugView.LIT) {
            renderDeferredPipeline(view, projection, lightViewProjection, cameraPosition, lightPosition, time);
        } else {
            drawParametricSurface(view, projection, lightViewProjection, cameraPosition, lightPosition, time);
            drawElephant(view, projection, lightViewProjection, cameraPosition, lightPosition, time);
        }
        drawLightMarker(view, projection, lightPosition);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        drawHud(cameraPosition, lightPosition, time);
    }

    private void drawParametricSurface(Mat4 view, Mat4 projection, Mat4 lightViewProjection, Vec3D cameraPosition, Vec3D lightPosition, double time) {
        OGLBuffers mesh = gridTopology == GridTopology.TRIANGLE_LIST ? gridList : gridStrip;
        Mat4 model = new Mat4Scale(1.9, 1.9, 1.9).mul(new Mat4Transl(-2.4, 0.0, 0.0));
        Mat4 mvp = model.mul(view).mul(projection);

        glUseProgram(paramProgram);
        setCommonLitUniforms(paramProgram, model, mvp, lightViewProjection, cameraPosition, lightPosition, new Vec3D(1.0, 1.0, 1.0), surfaceTexture, debugView);
        glUniform1f(glGetUniformLocation(paramProgram, "uTime"), (float) time);
        glUniform1i(glGetUniformLocation(paramProgram, "uSurfaceType"), surfaceType.getIndex());
        mesh.draw(gridTopology.getGlTopology(), paramProgram);
        glUseProgram(0);
    }

    private void drawElephant(Mat4 view, Mat4 projection, Mat4 lightViewProjection, Vec3D cameraPosition, Vec3D lightPosition, double time) {
        if (elephantModel == null || elephantModel.getBuffers() == null) {
            return;
        }

        Mat4 model = new Mat4Scale(0.9, 0.9, 0.9)
                .mul(new Mat4RotXYZ(0.0, time * 0.45, 0.0))
                .mul(new Mat4Transl(3.0, -0.2, -0.8));
        Mat4 mvp = model.mul(view).mul(projection);

        glUseProgram(meshProgram);
        setCommonLitUniforms(meshProgram, model, mvp, lightViewProjection, cameraPosition, lightPosition, new Vec3D(0.85, 0.82, 0.78), surfaceTexture, debugView);
        elephantModel.getBuffers().draw(elephantModel.getTopology(), meshProgram);
        glUseProgram(0);
    }

    private void renderDeferredPipeline(Mat4 view, Mat4 projection, Mat4 lightViewProjection, Vec3D cameraPosition, Vec3D lightPosition, double time) {
        ensureDeferredTargets();
        drawParametricSurfaceDeferred(view, projection, time);
        drawElephantDeferred(view, projection, time);
        renderAoPass(view);
        renderBlurPass();
        renderDeferredComposite(view, projection, lightViewProjection, cameraPosition, lightPosition);
    }

    private void drawParametricSurfaceDeferred(Mat4 view, Mat4 projection, double time) {
        OGLBuffers mesh = gridTopology == GridTopology.TRIANGLE_LIST ? gridList : gridStrip;
        Mat4 model = new Mat4Scale(1.9, 1.9, 1.9).mul(new Mat4Transl(-2.4, 0.0, 0.0));
        Mat4 mvp = model.mul(view).mul(projection);

        glUseProgram(paramDeferredProgram);
        setCommonDeferredUniforms(paramDeferredProgram, model, mvp, view, new Vec3D(1.0, 1.0, 1.0), surfaceTexture);
        glUniform1f(glGetUniformLocation(paramDeferredProgram, "uTime"), (float) time);
        glUniform1i(glGetUniformLocation(paramDeferredProgram, "uSurfaceType"), surfaceType.getIndex());
        mesh.draw(gridTopology.getGlTopology(), paramDeferredProgram);
        glUseProgram(0);
    }

    private void drawElephantDeferred(Mat4 view, Mat4 projection, double time) {
        if (elephantModel == null || elephantModel.getBuffers() == null) {
            return;
        }

        Mat4 model = new Mat4Scale(0.9, 0.9, 0.9)
                .mul(new Mat4RotXYZ(0.0, time * 0.45, 0.0))
                .mul(new Mat4Transl(3.0, -0.2, -0.8));
        Mat4 mvp = model.mul(view).mul(projection);

        glUseProgram(meshDeferredProgram);
        setCommonDeferredUniforms(meshDeferredProgram, model, mvp, view, new Vec3D(0.85, 0.82, 0.78), surfaceTexture);
        elephantModel.getBuffers().draw(elephantModel.getTopology(), meshDeferredProgram);
        glUseProgram(0);
    }

    private void renderAoPass(Mat4 view) {
        aoTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(aoProgram);
        bindDeferredInputs(aoProgram, 0, 1);
        deferredTarget.bindColorTexture(aoProgram, "uPositionTex", 0, 0);
        deferredTarget.bindColorTexture(aoProgram, "uNormalTex", 1, 1);
        glUniformMatrix4fv(glGetUniformLocation(aoProgram, "uView"), false, ToFloatArray.convert(view));
        glUniform1i(glGetUniformLocation(aoProgram, "uKernelSize"), 3);
        glUniform1f(glGetUniformLocation(aoProgram, "uRadius"), 0.9f);
        screenQuad.draw(GL_TRIANGLE_STRIP, aoProgram);
        glUseProgram(0);
    }

    private void renderBlurPass() {
        aoBlurTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(blurProgram);
        aoTarget.bindColorTexture(blurProgram, "uAOTex", 0);
        glUniform2f(glGetUniformLocation(blurProgram, "uTexelSize"), 1.0f / aoTarget.getWidth(), 1.0f / aoTarget.getHeight());
        screenQuad.draw(GL_TRIANGLE_STRIP, blurProgram);
        glUseProgram(0);
    }

    private void renderDeferredComposite(Mat4 view, Mat4 projection, Mat4 lightViewProjection, Vec3D cameraPosition, Vec3D lightPosition) {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDrawBuffer(GL_BACK);
        glReadBuffer(GL_BACK);
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glUseProgram(compositeProgram);
        deferredTarget.bindColorTexture(compositeProgram, "uPositionTex", 0, 0);
        deferredTarget.bindColorTexture(compositeProgram, "uNormalTex", 1, 1);
        deferredTarget.bindColorTexture(compositeProgram, "uAlbedoTex", 2, 2);
        aoBlurTarget.bindColorTexture(compositeProgram, "uAOTexture", 3, 0);
        shadowDepthTexture.bind(compositeProgram, "uShadowMap", 4);
        glUniformMatrix4fv(glGetUniformLocation(compositeProgram, "uShadowMvp"), false, ToFloatArray.convert(lightViewProjection));
        glUniform3f(glGetUniformLocation(compositeProgram, "uCameraPos"),
                (float) cameraPosition.getX(),
                (float) cameraPosition.getY(),
                (float) cameraPosition.getZ());
        glUniform3f(glGetUniformLocation(compositeProgram, "uLightPos"),
                (float) lightPosition.getX(),
                (float) lightPosition.getY(),
                (float) lightPosition.getZ());
        Vec3D spotDirection = computeSpotDirection();
        glUniform3f(glGetUniformLocation(compositeProgram, "uSpotDirection"),
                (float) spotDirection.getX(),
                (float) spotDirection.getY(),
                (float) spotDirection.getZ());
        glUniform3f(glGetUniformLocation(compositeProgram, "uSpotAttenuation"), 1.0f, 0.05f, 0.01f);
        glUniform1f(glGetUniformLocation(compositeProgram, "uSpotInnerCutoff"), (float) Math.cos(Math.toRadians(spotlightInnerDegrees)));
        glUniform1f(glGetUniformLocation(compositeProgram, "uSpotOuterCutoff"), (float) Math.cos(Math.toRadians(spotlightOuterDegrees)));
        glUniform1i(glGetUniformLocation(compositeProgram, "uUseAmbient"), useAmbient ? 1 : 0);
        glUniform1i(glGetUniformLocation(compositeProgram, "uUseDiffuse"), useDiffuse ? 1 : 0);
        glUniform1i(glGetUniformLocation(compositeProgram, "uUseSpecular"), useSpecular ? 1 : 0);
        glUniform3f(glGetUniformLocation(compositeProgram, "uBaseColor"), 1.0f, 1.0f, 1.0f);
        screenQuad.draw(GL_TRIANGLE_STRIP, compositeProgram);
        glEnable(GL_DEPTH_TEST);
        glUseProgram(0);
    }

    private void setCommonDeferredUniforms(int program, Mat4 model, Mat4 mvp, Mat4 view, Vec3D baseColor, OGLTexture2D texture) {
        glUniformMatrix4fv(glGetUniformLocation(program, "uMvp"), false, ToFloatArray.convert(mvp));
        glUniformMatrix4fv(glGetUniformLocation(program, "uModel"), false, ToFloatArray.convert(model));
        glUniformMatrix4fv(glGetUniformLocation(program, "uView"), false, ToFloatArray.convert(view));
        glUniform3f(glGetUniformLocation(program, "uBaseColor"),
                (float) baseColor.getX(),
                (float) baseColor.getY(),
                (float) baseColor.getZ());
        glUniform1i(glGetUniformLocation(program, "uUseTexture"), useTexture ? 1 : 0);
        if (useTexture) {
            texture.bind(program, "uTexture", 0);
        }
    }

    private void bindDeferredInputs(int program, int posSlot, int normalSlot) {
        deferredTarget.bindColorTexture(program, "uPositionTex", posSlot, 0);
        deferredTarget.bindColorTexture(program, "uNormalTex", normalSlot, 1);
    }

    private void renderShadowPass(Mat4 lightViewProjection, double time) {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowFbo);
        glViewport(0, 0, SHADOW_SIZE, SHADOW_SIZE);
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(2.0f, 4.0f);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        OGLBuffers mesh = gridTopology == GridTopology.TRIANGLE_LIST ? gridList : gridStrip;
        Mat4 surfaceModel = new Mat4Scale(1.9, 1.9, 1.9).mul(new Mat4Transl(-2.4, 0.0, 0.0));

        glUseProgram(paramShadowProgram);
        setShadowUniforms(paramShadowProgram, surfaceModel, lightViewProjection, time, surfaceType.getIndex());
        mesh.draw(gridTopology.getGlTopology(), paramShadowProgram);
        glUseProgram(0);

        if (elephantModel != null && elephantModel.getBuffers() != null) {
            Mat4 elephantModelMatrix = new Mat4Scale(0.9, 0.9, 0.9)
                    .mul(new Mat4RotXYZ(0.0, time * 0.45, 0.0))
                    .mul(new Mat4Transl(3.0, -0.2, -0.8));
            glUseProgram(meshShadowProgram);
            setShadowUniforms(meshShadowProgram, elephantModelMatrix, lightViewProjection, time, 0);
            elephantModel.getBuffers().draw(elephantModel.getTopology(), meshShadowProgram);
            glUseProgram(0);
        }

        glDisable(GL_POLYGON_OFFSET_FILL);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDrawBuffer(GL_BACK);
        glReadBuffer(GL_BACK);
        glViewport(0, 0, width, height);
    }

    private void drawLightMarker(Mat4 view, Mat4 projection, Vec3D lightPosition) {
        Mat4 model = new Mat4Scale(0.12).mul(new Mat4Transl(lightPosition));
        Mat4 mvp = model.mul(view).mul(projection);

        glUseProgram(lightProgram);
        glUniformMatrix4fv(glGetUniformLocation(lightProgram, "uMvp"), false, ToFloatArray.convert(mvp));
        glUniform3f(glGetUniformLocation(lightProgram, "uColor"), 1.0f, 0.95f, 0.35f);
        lightMarker.draw(GL_POINTS, lightProgram);
        glUseProgram(0);
    }

    private void setCommonLitUniforms(int program, Mat4 model, Mat4 mvp, Mat4 lightViewProjection, Vec3D cameraPosition, Vec3D lightPosition,
                                      Vec3D baseColor, OGLTexture2D texture, DebugView debugView) {
        glUniformMatrix4fv(glGetUniformLocation(program, "uMvp"), false, ToFloatArray.convert(mvp));
        glUniformMatrix4fv(glGetUniformLocation(program, "uModel"), false, ToFloatArray.convert(model));
        glUniformMatrix4fv(glGetUniformLocation(program, "uShadowMvp"), false, ToFloatArray.convert(lightViewProjection));
        glUniform3f(glGetUniformLocation(program, "uCameraPos"),
                (float) cameraPosition.getX(),
                (float) cameraPosition.getY(),
                (float) cameraPosition.getZ());
        glUniform3f(glGetUniformLocation(program, "uLightPos"),
                (float) lightPosition.getX(),
                (float) lightPosition.getY(),
                (float) lightPosition.getZ());
        Vec3D spotDirection = computeSpotDirection();
        glUniform3f(glGetUniformLocation(program, "uSpotDirection"),
                (float) spotDirection.getX(),
                (float) spotDirection.getY(),
                (float) spotDirection.getZ());
        glUniform3f(glGetUniformLocation(program, "uSpotAttenuation"), 1.0f, 0.05f, 0.01f);
        glUniform1f(glGetUniformLocation(program, "uSpotInnerCutoff"), (float) Math.cos(Math.toRadians(spotlightInnerDegrees)));
        glUniform1f(glGetUniformLocation(program, "uSpotOuterCutoff"), (float) Math.cos(Math.toRadians(spotlightOuterDegrees)));
        glUniform3f(glGetUniformLocation(program, "uBaseColor"),
                (float) baseColor.getX(),
                (float) baseColor.getY(),
                (float) baseColor.getZ());
        glUniform1i(glGetUniformLocation(program, "uDebugView"), debugView.getIndex());
        glUniform1i(glGetUniformLocation(program, "uUseAmbient"), useAmbient ? 1 : 0);
        glUniform1i(glGetUniformLocation(program, "uUseDiffuse"), useDiffuse ? 1 : 0);
        glUniform1i(glGetUniformLocation(program, "uUseSpecular"), useSpecular ? 1 : 0);
        glUniform1i(glGetUniformLocation(program, "uUseTexture"), useTexture ? 1 : 0);
        if (useTexture) {
            texture.bind(program, "uTexture", 0);
        }
        shadowDepthTexture.bind(program, "uShadowMap", 1);
    }

    private void setShadowUniforms(int program, Mat4 model, Mat4 lightViewProjection, double time, int surfaceIndex) {
        Mat4 lightMvp = model.mul(lightViewProjection);
        glUniformMatrix4fv(glGetUniformLocation(program, "uMvp"), false, ToFloatArray.convert(lightMvp));
        glUniformMatrix4fv(glGetUniformLocation(program, "uModel"), false, ToFloatArray.convert(model));
        if (program == paramShadowProgram) {
            glUniform1f(glGetUniformLocation(program, "uTime"), (float) time);
            glUniform1i(glGetUniformLocation(program, "uSurfaceType"), surfaceIndex);
        }
    }

    private void updateCamera() {
        double speed = pressedKeys[GLFW_KEY_LEFT_SHIFT] ? CAMERA_FAST_SPEED : CAMERA_SPEED;
        double step = speed * frameTimer.getDeltaSeconds();
        double spotStep = 0.9 * frameTimer.getDeltaSeconds();

        if (pressedKeys[GLFW_KEY_W]) {
            cameraState.moveForward(step);
        }
        if (pressedKeys[GLFW_KEY_S]) {
            cameraState.moveForward(-step);
        }
        if (pressedKeys[GLFW_KEY_A]) {
            cameraState.moveRight(-step);
        }
        if (pressedKeys[GLFW_KEY_D]) {
            cameraState.moveRight(step);
        }
        if (pressedKeys[GLFW_KEY_Q]) {
            cameraState.moveUp(step);
        }
        if (pressedKeys[GLFW_KEY_E]) {
            cameraState.moveUp(-step);
        }

        if (pressedKeys[GLFW_KEY_LEFT]) {
            spotlightYaw -= spotStep;
        }
        if (pressedKeys[GLFW_KEY_RIGHT]) {
            spotlightYaw += spotStep;
        }
        if (pressedKeys[GLFW_KEY_UP]) {
            spotlightPitch = Math.min(1.35, spotlightPitch + spotStep);
        }
        if (pressedKeys[GLFW_KEY_DOWN]) {
            spotlightPitch = Math.max(-1.35, spotlightPitch - spotStep);
        }
        if (pressedKeys[GLFW_KEY_LEFT_BRACKET]) {
            spotlightOuterDegrees = Math.max(spotlightInnerDegrees + 1.0, spotlightOuterDegrees - 18.0 * spotStep);
        }
        if (pressedKeys[GLFW_KEY_RIGHT_BRACKET]) {
            spotlightOuterDegrees = Math.min(75.0, spotlightOuterDegrees + 18.0 * spotStep);
        }
    }

    private void drawHud(Vec3D cameraPosition, Vec3D lightPosition, double time) {
        textRenderer.clear();
        textRenderer.addStr2D(8, 20, "Grid surfaces: list/strip, debug views, model + light");
        textRenderer.addStr2D(8, 40, "WASD move, Q/E up/down, hold LMB look, V projection, M mode, N grid, B surface");
        textRenderer.addStr2D(8, 60, "0 lit, 1 pos, 2 normal, 3 uv, 4 depth, 5 texture, R reset");
        textRenderer.addStr2D(8, 80, String.format(Locale.US,
                "ambient[%s] diffuse[%s] specular[%s] texture[%s] deferred[%s] 6/7/8/T/F toggle",
                useAmbient ? "on" : "off",
                useDiffuse ? "on" : "off",
                useSpecular ? "on" : "off",
                useTexture ? "on" : "off",
                useDeferred ? "on" : "off"));
        textRenderer.addStr2D(8, 100, String.format(Locale.US,
                "shadow map: %dx%d, spotlight yaw/pitch and soft edge: arrows + [ ]",
                SHADOW_SIZE, SHADOW_SIZE));
        textRenderer.addStr2D(8, 120, String.format(Locale.US,
                "camera [%.2f, %.2f, %.2f] light [%.2f, %.2f, %.2f]",
                cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ(),
                lightPosition.getX(), lightPosition.getY(), lightPosition.getZ()));
        textRenderer.addStr2D(8, 140, String.format(Locale.US,
                "projection: %s, mode: %s, grid: %s, surface: %s, debug: %s, time: %.2f",
                cameraState.getProjectionMode(),
                renderMode.getLabel(),
                gridTopology.getLabel(),
                surfaceType.getLabel(),
                debugView.getLabel(),
                time));
        textRenderer.addStr2D(8, 160, String.format(Locale.US,
                "spotlight dir yaw %.2f pitch %.2f inner %.1f outer %.1f",
                spotlightYaw, spotlightPitch, spotlightInnerDegrees, spotlightOuterDegrees));
        textRenderer.addStr2D(8, 180, "F toggles deferred shading + AO preview");
        textRenderer.draw();
    }

    private Vec3D computeLightPosition(double time) {
        double radius = 5.0;
        double x = Math.cos(time * 0.5) * radius;
        double y = Math.sin(time * 0.5) * radius;
        double z = 3.5 + Math.sin(time * 0.8) * 1.2;
        return new Vec3D(x, y, z);
    }

    private Vec3D computeSpotDirection() {
        double cp = Math.cos(spotlightPitch);
        return new Vec3D(
                Math.cos(spotlightYaw) * cp,
                Math.sin(spotlightYaw) * cp,
                Math.sin(spotlightPitch)
        ).normalized().orElse(new Vec3D(0.0, 0.0, -1.0));
    }

    private Mat4 computeLightViewProjection(Vec3D lightPosition) {
        Vec3D target = new Vec3D(0.0, 0.0, 0.0);
        Vec3D direction = target.sub(lightPosition);
        Vec3D up = Math.abs(direction.getZ()) > 0.95 ? new Vec3D(0.0, 1.0, 0.0) : new Vec3D(0.0, 0.0, 1.0);
        Mat4 lightView = new Mat4ViewRH(lightPosition, direction, up);
        Mat4 lightProjection = new Mat4PerspRH(Math.toRadians(35.0), 1.0, 0.1, 30.0);
        return lightView.mul(lightProjection);
    }

    private void setupShadowMap() {
        shadowFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, shadowFbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowDepthTexture.getTextureId(), 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Shadow framebuffer is incomplete");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, shadowDepthTexture.getTextureId());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    }

    private void setupDeferredTargets() {
        deferredTarget = new OGLRenderTarget(width, height, 3);
        aoTarget = new OGLRenderTarget(width, height, 1);
        aoBlurTarget = new OGLRenderTarget(width, height, 1);
    }

    private void ensureDeferredTargets() {
        if (deferredTarget == null || deferredTarget.getWidth() != width || deferredTarget.getHeight() != height) {
            setupDeferredTargets();
        }
    }

    private OGLBuffers createScreenQuad() {
        float[] vertexData = {
                -1.0f, -1.0f, 0.0f, 0.0f,
                 1.0f, -1.0f, 1.0f, 0.0f,
                -1.0f,  1.0f, 0.0f, 1.0f,
                 1.0f,  1.0f, 1.0f, 1.0f
        };
        int[] indexData = {0, 1, 2, 3};
        return new OGLBuffers(vertexData, new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2),
                new OGLBuffers.Attrib("inTexCoord", 2)
        }, indexData);
    }

    private OGLBuffers createLightMarker() {
        float[] vertexData = {
                0.0f, 0.0f, 0.0f
        };

        return new OGLBuffers(vertexData, new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 3)
        }, null);
    }

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key >= 0 && key < pressedKeys.length) {
                pressedKeys[key] = action != GLFW_RELEASE;
            }

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }

            if (action == GLFW_PRESS) {
                if (key == GLFW_KEY_V) {
                    cameraState.toggleProjection();
                } else if (key == GLFW_KEY_M) {
                    renderMode = renderMode.next();
                } else if (key == GLFW_KEY_N) {
                    gridTopology = gridTopology.next();
                } else if (key == GLFW_KEY_B) {
                    surfaceType = surfaceType.next();
                } else if (key == GLFW_KEY_R) {
                    cameraState.reset();
                } else if (key == GLFW_KEY_0) {
                    debugView = DebugView.LIT;
                } else if (key == GLFW_KEY_1) {
                    debugView = DebugView.POSITION;
                } else if (key == GLFW_KEY_2) {
                    debugView = DebugView.NORMAL;
                } else if (key == GLFW_KEY_3) {
                    debugView = DebugView.UV;
                } else if (key == GLFW_KEY_4) {
                    debugView = DebugView.DEPTH;
                } else if (key == GLFW_KEY_5) {
                    debugView = DebugView.TEXTURE;
                } else if (key == GLFW_KEY_6) {
                    useAmbient = !useAmbient;
                } else if (key == GLFW_KEY_7) {
                    useDiffuse = !useDiffuse;
                } else if (key == GLFW_KEY_8) {
                    useSpecular = !useSpecular;
                } else if (key == GLFW_KEY_T) {
                    useTexture = !useTexture;
                } else if (key == GLFW_KEY_F) {
                    useDeferred = !useDeferred;
                }
            }
        }
    };

    private final GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0) {
                width = w;
                height = h;
                if (textRenderer != null) {
                    textRenderer.resize(width, height);
                }
            }
        }
    };

    private final GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button == GLFW_MOUSE_BUTTON_1) {
                mouseLookActive = action == GLFW_PRESS;
                firstMouseSample = true;
            }
        }
    };

    private final GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (!mouseLookActive) {
                return;
            }

            if (firstMouseSample) {
                lastMouseX = x;
                lastMouseY = y;
                firstMouseSample = false;
                return;
            }

            double dx = x - lastMouseX;
            double dy = y - lastMouseY;
            lastMouseX = x;
            lastMouseY = y;

            cameraState.addYaw(dx * MOUSE_SENSITIVITY);
            cameraState.addPitch(-dy * MOUSE_SENSITIVITY);
        }
    };

    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            cameraState.adjustOrthographicHeight(-dy * 0.4);
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    @Override
    public void dispose() {
        if (glIsProgram(paramProgram)) {
            glDeleteProgram(paramProgram);
        }
        if (glIsProgram(meshProgram)) {
            glDeleteProgram(meshProgram);
        }
        if (glIsProgram(lightProgram)) {
            glDeleteProgram(lightProgram);
        }
        if (glIsProgram(paramShadowProgram)) {
            glDeleteProgram(paramShadowProgram);
        }
        if (glIsProgram(meshShadowProgram)) {
            glDeleteProgram(meshShadowProgram);
        }
        if (shadowFbo != 0) {
            glDeleteFramebuffers(shadowFbo);
        }
    }
}
