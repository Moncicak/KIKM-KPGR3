import lwjglutils.OGLBuffers;
import lwjglutils.OGLModelOBJ;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexImageFloat;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4OrthoRH;
import transforms.Mat4PerspRH;
import transforms.Mat4RotXYZ;
import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Point3D;
import transforms.Vec3D;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer extends AbstractRenderer {
	private static final int GBUFFER_COLOR_ATTACHMENTS = 3;
	private static final float AO_SAMPLE_RADIUS = 0.35f;
	private static final float AO_PIXEL_RADIUS = 12.0f;
	private static final float AO_BIAS = 0.015f;

	private int paramProgram;
	private int staticProgram;
	private int lightProgram;
	private int aoProgram;
	private int blurProgram;
	private int compositeProgram;

	private OGLBuffers buffersList;
	private OGLBuffers buffersStrip;
	private OGLBuffers screenQuad;
	private OGLModelOBJ staticBody;

	private OGLRenderTarget gBuffer;
	private OGLRenderTarget aoTarget;
	private OGLRenderTarget aoBlurTarget;

	private GridTopology topology = GridTopology.TRIANGLES;
	private int renderMode = GL_FILL;
	private int functionMode = 0;
	private int debugMode = 0;
	private boolean ambientEnabled = true;
	private boolean diffuseEnabled = true;
	private boolean specularEnabled = true;
	private boolean spotlightEnabled = true;
	private float rotX = 0.0f;
	private float rotY = 0.0f;

	private Camera camera = new Camera();
	private Mat4 projection;
	private boolean[] keys = new boolean[1024];
	private boolean mousePressed = false;
	private double oldX;
	private double oldY;
	private boolean isPerspective = true;

	private Vec3D lightPos = new Vec3D(1.5, 1.5, 2.0);
	private float spotInnerDeg = 16.0f;
	private float spotOuterDeg = 24.0f;
	private static final String[] DEBUG_MODE_NAMES = {
			"Final lighting",
			"AO raw",
			"AO blur",
			"Normals",
			"View-space position",
			"Spotlight factor"
	};

	@Override
	public void init() {
		super.init();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		int m = 50;
		int n = 50;
		float[] vertices = GridMeshFactory.generateVertices(m, n);
		OGLBuffers.Attrib[] attributes = { new OGLBuffers.Attrib("inPosition", 3) };
		buffersList = new OGLBuffers(vertices, attributes, GridMeshFactory.generateIndicesList(m, n));
		buffersStrip = new OGLBuffers(vertices, attributes, GridMeshFactory.generateIndicesStrip(m, n));
		screenQuad = createScreenQuad();

		try {
			staticBody = new OGLModelOBJ("/obj/StaticBody.obj");
		} catch (Exception e) {
			throw new RuntimeException("Failed to load StaticBody.obj", e);
		}

		paramProgram = ShaderUtils.loadProgram("/shaders/start");
		staticProgram = ShaderUtils.loadProgram("/shaders/static");
		lightProgram = ShaderUtils.loadProgram("/shaders/light");
		aoProgram = ShaderUtils.loadProgram(new String[] { "/shaders/screen", "/shaders/ao" });
		blurProgram = ShaderUtils.loadProgram(new String[] { "/shaders/screen", "/shaders/blur" });
		compositeProgram = ShaderUtils.loadProgram(new String[] { "/shaders/screen", "/shaders/composite" });

		camera = camera.withPosition(new Vec3D(0.0, -3.0, 1.0))
				.withAzimuth(Math.PI / 2.0)
				.withZenith(-Math.PI / 8.0);

		ensureTargets();
	}

	@Override
	public void display() {
		pass++;
		updateCamera();
		ensureTargets();

		double aspect = height / (double) Math.max(width, 1);
		if (isPerspective) {
			projection = new Mat4PerspRH(Math.PI / 3.0, aspect, 0.1, 50.0);
		} else {
			projection = new Mat4OrthoRH(2.5 / aspect, 2.5, 0.1, 50.0);
		}

		Mat4 view = camera.getViewMatrix();
		Vec3D lightPosView = toViewSpace(lightPos, view);
		Vec3D spotTargetWorld = new Vec3D(0.0, 0.0, 0.0);
		Vec3D lightDirWorld = spotTargetWorld.sub(lightPos).normalized().orElse(new Vec3D(0.0, 0.0, -1.0));
		Vec3D spotDirView = toViewDirection(lightDirWorld, view);

		renderGeometryPass(view);
		renderAoPass();
		renderBlurPass();
		renderCompositePass(lightPosView, spotDirView);

		if (debugMode == 0) {
			renderLightMarker(view);
		}
	}

	private void updateCamera() {
		double speed = 0.05;
		if (keys[GLFW_KEY_W]) {
			camera = camera.forward(speed);
		}
		if (keys[GLFW_KEY_S]) {
			camera = camera.backward(speed);
		}
		if (keys[GLFW_KEY_A]) {
			camera = camera.left(speed);
		}
		if (keys[GLFW_KEY_D]) {
			camera = camera.right(speed);
		}
	}

	private void ensureTargets() {
		if (gBuffer == null || gBuffer.getWidth() != width || gBuffer.getHeight() != height) {
			gBuffer = new OGLRenderTarget(width, height, GBUFFER_COLOR_ATTACHMENTS);
		}
		if (aoTarget == null || aoTarget.getWidth() != width || aoTarget.getHeight() != height) {
			aoTarget = new OGLRenderTarget(width, height, 1, new OGLTexImageFloat.Format(1));
			aoBlurTarget = new OGLRenderTarget(width, height, 1, new OGLTexImageFloat.Format(1));
		}
	}

	private void renderGeometryPass(Mat4 view) {
		gBuffer.bind();
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glPolygonMode(GL_FRONT_AND_BACK, renderMode);

		drawProceduralBody(view);
		drawStaticBody(view);

		glUseProgram(0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private void drawProceduralBody(Mat4 view) {
		glUseProgram(paramProgram);

		Mat4 modelMatrix = new Mat4Transl(-0.6, 0.0, 0.0).mul(new Mat4RotXYZ(rotX, rotY, 0.0));
		Mat4 modelViewMatrix = modelMatrix.mul(view);
		Mat4 mvp = modelViewMatrix.mul(projection);

		glUniformMatrix4fv(glGetUniformLocation(paramProgram, "modelViewProjection"), false, ToFloatArray.convert(mvp));
		glUniformMatrix4fv(glGetUniformLocation(paramProgram, "modelViewMatrix"), false, ToFloatArray.convert(modelViewMatrix));
		glUniform1f(glGetUniformLocation(paramProgram, "time"), pass / 50.0f);
		glUniform1i(glGetUniformLocation(paramProgram, "mode"), functionMode);
		Vec3D color = proceduralColor(functionMode);
		glUniform3f(glGetUniformLocation(paramProgram, "uColor"), (float) color.getX(), (float) color.getY(), (float) color.getZ());

		drawGeometry();
	}

	private void drawStaticBody(Mat4 view) {
		if (staticBody == null || staticBody.getBuffers() == null) {
			return;
		}

		glUseProgram(staticProgram);

		Mat4 modelMatrix = new Mat4Transl(0.6, 0.0, 0.0).mul(new Mat4Scale(0.35));
		Mat4 modelViewMatrix = modelMatrix.mul(view);
		Mat4 mvp = modelViewMatrix.mul(projection);

		glUniformMatrix4fv(glGetUniformLocation(staticProgram, "modelViewProjection"), false, ToFloatArray.convert(mvp));
		glUniformMatrix4fv(glGetUniformLocation(staticProgram, "modelViewMatrix"), false, ToFloatArray.convert(modelViewMatrix));
		glUniform3f(glGetUniformLocation(staticProgram, "uColor"), 0.85f, 0.75f, 0.25f);

		staticBody.getBuffers().draw(staticBody.getTopology(), staticProgram);
	}

	private void renderAoPass() {
		aoTarget.bind();
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		glDisable(GL_DEPTH_TEST);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glUseProgram(aoProgram);

		gBuffer.bindColorTexture(aoProgram, "uPositionTex", 0, 0);
		gBuffer.bindColorTexture(aoProgram, "uNormalTex", 1, 1);
		glUniform2f(glGetUniformLocation(aoProgram, "uResolution"), width, height);
		glUniform1f(glGetUniformLocation(aoProgram, "uSampleRadius"), AO_SAMPLE_RADIUS);
		glUniform1f(glGetUniformLocation(aoProgram, "uPixelRadius"), AO_PIXEL_RADIUS);
		glUniform1f(glGetUniformLocation(aoProgram, "uBias"), AO_BIAS);

		screenQuad.draw(GL_TRIANGLE_STRIP, aoProgram);
		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private void renderBlurPass() {
		aoBlurTarget.bind();
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
		glDisable(GL_DEPTH_TEST);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glUseProgram(blurProgram);

		aoTarget.bindColorTexture(blurProgram, "uAOTex", 0, 0);
		glUniform2f(glGetUniformLocation(blurProgram, "uTexelSize"), 1.0f / width, 1.0f / height);

		screenQuad.draw(GL_TRIANGLE_STRIP, blurProgram);
		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private void renderCompositePass(Vec3D lightPosView, Vec3D spotDirView) {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, width, height);
		glClearColor(0.04f, 0.05f, 0.07f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glDisable(GL_DEPTH_TEST);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glUseProgram(compositeProgram);

		gBuffer.bindColorTexture(compositeProgram, "uPositionTex", 0, 0);
		gBuffer.bindColorTexture(compositeProgram, "uNormalTex", 1, 1);
		gBuffer.bindColorTexture(compositeProgram, "uAlbedoTex", 2, 2);
		aoTarget.bindColorTexture(compositeProgram, "uAOTex", 3, 0);
		aoBlurTarget.bindColorTexture(compositeProgram, "uAOBlurTex", 4, 0);

		glUniform3f(glGetUniformLocation(compositeProgram, "uLightPosView"),
				(float) lightPosView.getX(), (float) lightPosView.getY(), (float) lightPosView.getZ());
		glUniform3f(glGetUniformLocation(compositeProgram, "uSpotDirView"),
				(float) spotDirView.getX(), (float) spotDirView.getY(), (float) spotDirView.getZ());
		glUniform3f(glGetUniformLocation(compositeProgram, "uLightColor"), 2.0f, 2.0f, 1.8f);
		glUniform1f(glGetUniformLocation(compositeProgram, "uAmbientStrength"), 0.12f);
		glUniform1f(glGetUniformLocation(compositeProgram, "uSpotInnerCutoff"), (float) Math.cos(Math.toRadians(spotInnerDeg)));
		glUniform1f(glGetUniformLocation(compositeProgram, "uSpotOuterCutoff"), (float) Math.cos(Math.toRadians(spotOuterDeg)));
		glUniform1i(glGetUniformLocation(compositeProgram, "debugMode"), debugMode);
		glUniform1i(glGetUniformLocation(compositeProgram, "uAmbientEnabled"), ambientEnabled ? 1 : 0);
		glUniform1i(glGetUniformLocation(compositeProgram, "uDiffuseEnabled"), diffuseEnabled ? 1 : 0);
		glUniform1i(glGetUniformLocation(compositeProgram, "uSpecularEnabled"), specularEnabled ? 1 : 0);
		glUniform1i(glGetUniformLocation(compositeProgram, "uSpotEnabled"), spotlightEnabled ? 1 : 0);

		screenQuad.draw(GL_TRIANGLE_STRIP, compositeProgram);
		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
	}

	private void renderLightMarker(Mat4 view) {
		if (staticBody == null || staticBody.getBuffers() == null) {
			return;
		}

		glUseProgram(lightProgram);
		glDisable(GL_DEPTH_TEST);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		Mat4 modelMatrix = new Mat4Transl(lightPos).mul(new Mat4Scale(0.05));
		Mat4 modelViewMatrix = modelMatrix.mul(view);
		Mat4 mvp = modelViewMatrix.mul(projection);

		glUniformMatrix4fv(glGetUniformLocation(lightProgram, "modelViewProjection"), false, ToFloatArray.convert(mvp));
		glUniform3f(glGetUniformLocation(lightProgram, "uColor"), 1.0f, 1.0f, 1.0f);
		staticBody.getBuffers().draw(staticBody.getTopology(), lightProgram);

		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
	}

	private void drawGeometry() {
		if (topology == GridTopology.TRIANGLES) {
			if (buffersList != null) {
				buffersList.draw(GL_TRIANGLES, paramProgram);
			}
		} else {
			if (buffersStrip != null) {
				buffersStrip.draw(GL_TRIANGLE_STRIP, paramProgram);
			}
		}
	}

	private OGLBuffers createScreenQuad() {
		float[] vertexData = {
				-1.0f, -1.0f, 0.0f, 0.0f,
				1.0f, -1.0f, 1.0f, 0.0f,
				-1.0f, 1.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f
		};
		int[] indices = { 0, 1, 2, 3 };
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 2),
				new OGLBuffers.Attrib("inTexCoord", 2)
		};
		return new OGLBuffers(vertexData, attributes, indices);
	}

	private Vec3D proceduralColor(int mode) {
		return switch (mode) {
			case 1 -> new Vec3D(0.85, 0.35, 0.25);
			case 2 -> new Vec3D(0.35, 0.75, 0.95);
			case 3 -> new Vec3D(0.85, 0.65, 0.25);
			case 4 -> new Vec3D(0.45, 0.85, 0.45);
			case 5 -> new Vec3D(0.90, 0.45, 0.85);
			default -> new Vec3D(0.0, 0.4, 0.8);
		};
	}

	private Vec3D toViewSpace(Vec3D worldPos, Mat4 view) {
		Point3D p = new Point3D(worldPos).mul(view);
		return p.dehomog().orElse(worldPos);
	}

	private Vec3D toViewDirection(Vec3D worldDir, Mat4 view) {
		Point3D d = new Point3D(worldDir.getX(), worldDir.getY(), worldDir.getZ(), 0.0).mul(view);
		return new Vec3D(d.getX(), d.getY(), d.getZ()).normalized().orElse(new Vec3D(0.0, 0.0, -1.0));
	}

	@Override
	public GLFWKeyCallback getKeyCallback() {
		return new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key >= 0 && key < keys.length) {
					if (action == GLFW_PRESS) {
						keys[key] = true;
					} else if (action == GLFW_RELEASE) {
						keys[key] = false;
					}
				}

				if (action == GLFW_PRESS || action == GLFW_REPEAT) {
					switch (key) {
						case GLFW_KEY_P -> renderMode = GL_POINT;
						case GLFW_KEY_L -> renderMode = GL_LINE;
						case GLFW_KEY_F -> renderMode = GL_FILL;
						case GLFW_KEY_T -> topology = GridTopology.TRIANGLES;
						case GLFW_KEY_S -> topology = GridTopology.TRIANGLE_STRIP;
						case GLFW_KEY_1 -> functionMode = 0;
						case GLFW_KEY_2 -> functionMode = 1;
						case GLFW_KEY_3 -> functionMode = 2;
						case GLFW_KEY_4 -> functionMode = 3;
						case GLFW_KEY_5 -> functionMode = 4;
						case GLFW_KEY_6 -> functionMode = 5;
						case GLFW_KEY_M -> {
							debugMode = (debugMode + 1) % DEBUG_MODE_NAMES.length;
							System.out.println("Debug mode: " + DEBUG_MODE_NAMES[debugMode]);
						}
						case GLFW_KEY_O -> isPerspective = !isPerspective;
						case GLFW_KEY_UP -> rotX += 0.1f;
						case GLFW_KEY_DOWN -> rotX -= 0.1f;
						case GLFW_KEY_LEFT -> rotY -= 0.1f;
						case GLFW_KEY_RIGHT -> rotY += 0.1f;
						case GLFW_KEY_I -> lightPos = lightPos.add(new Vec3D(0.0, 0.0, 0.2));
						case GLFW_KEY_K -> lightPos = lightPos.add(new Vec3D(0.0, 0.0, -0.2));
						case GLFW_KEY_J -> lightPos = lightPos.add(new Vec3D(-0.2, 0.0, 0.0));
						case GLFW_KEY_U -> lightPos = lightPos.add(new Vec3D(0.2, 0.0, 0.0));
						case GLFW_KEY_Z -> {
							ambientEnabled = !ambientEnabled;
							printLightingToggles();
						}
						case GLFW_KEY_X -> {
							diffuseEnabled = !diffuseEnabled;
							printLightingToggles();
						}
						case GLFW_KEY_C -> {
							specularEnabled = !specularEnabled;
							printLightingToggles();
						}
						case GLFW_KEY_B -> {
							spotlightEnabled = !spotlightEnabled;
							System.out.println("Spotlight: " + onOff(spotlightEnabled)
									+ " (inner " + spotInnerDeg + " deg, outer " + spotOuterDeg + " deg)");
						}
						case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
					}
				}
			}
		};
	}

	private void printLightingToggles() {
		System.out.println("Lighting toggles -> ambient: " + onOff(ambientEnabled)
				+ ", diffuse: " + onOff(diffuseEnabled)
				+ ", specular: " + onOff(specularEnabled));
	}

	private String onOff(boolean enabled) {
		return enabled ? "ON" : "OFF";
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (button == GLFW_MOUSE_BUTTON_1) {
					mousePressed = action == GLFW_PRESS;
				}
			}
		};
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if (mousePressed) {
					camera = camera.addAzimuth((oldX - xpos) * 0.005);
					camera = camera.addZenith((oldY - ypos) * 0.005);
				}
				oldX = xpos;
				oldY = ypos;
			}
		};
	}
}
